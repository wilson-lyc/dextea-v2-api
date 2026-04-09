# 门店微服务缓存架构设计

## 1. 背景与目标

### 现状

当前门店服务的数据访问模式如下：

- **附近门店查询**：Redis GEO 索引 → 批量 `selectBatchIds` 查 MySQL
- **门店详情查询**（Admin/Internal）：每次直接查 MySQL
- **门店有效性校验**（`checkValidity`）：每次直接查 MySQL，被其他微服务通过 Feign 高频调用

核心问题：门店数据写少读多，且被多个微服务依赖，无任何应用层缓存保护，高并发时 MySQL 压力集中。

### 目标

| 目标 | 说明 |
|------|------|
| 降低 MySQL 读压力 | 热点门店数据走缓存，减少 DB 直接访问 |
| 提升读接口响应速度 | 本地缓存命中时 P99 < 1ms |
| 保证数据一致性 | 写操作后缓存及时失效，不提供脏读 |
| 防止缓存三大问题 | 击穿、穿透、雪崩 |

---

## 2. 缓存数据选型

并非所有数据都值得缓存，以下为分析结论：

| 数据 | 是否缓存 | 理由 |
|------|----------|------|
| 门店详情（by ID） | **是** | 读多写少，Feign 高频调用，适合两级缓存 |
| 门店有效性（valid/invalid） | **是** | `checkValidity` 被跨服务高频调用，结果轻量 |
| 门店 GEO 索引 | **已有**（Redis GEO） | 现有实现保持不变 |
| 分页列表 | **否** | 查询条件组合爆炸，缓存收益低，容易脏 |
| Admin 分页 | **否** | 管理端流量低，不值得维护缓存 |

---

## 3. 两级缓存架构

```
请求
  │
  ▼
[L1 本地缓存 · Caffeine]
  │ Miss
  ▼
[L2 分布式缓存 · Redis Hash]
  │ Miss
  ▼
[MySQL · store 表]
  │
  └─ 回填 L2 → 回填 L1
```

### 3.1 L1：本地缓存（Caffeine）

- **库**：Caffeine（Spring Cache 默认集成，无额外依赖）
- **数据范围**：门店详情、门店有效性
- **容量上限**：每个缓存 500 条（门店总量预估 < 500）
- **过期策略**：写入后 60 秒过期（`expireAfterWrite`）
- **特点**：进程内，访问速度 < 1μs，无网络开销；实例重启后冷启动

### 3.2 L2：分布式缓存（Redis）

- **数据结构**：Hash（存储门店完整字段）
- **数据范围**：门店详情、门店有效性（独立 key）
- **TTL**：门店详情 30 分钟，有效性 10 分钟
- **特点**：多实例共享，服务重启不失效；需要网络 I/O（通常 < 1ms）

---

## 4. 缓存 Key 设计

所有 Key 统一使用 `store:` 前缀，避免与其他微服务冲突。

| 用途 | Key 格式 | 类型 | TTL |
|------|----------|------|-----|
| 门店详情 | `store:detail:{storeId}` | Redis Hash | 30 min |
| 门店有效性 | `store:valid:{storeId}` | Redis String（`"true"/"false"`） | 10 min |
| 门店 GEO 索引 | `store:geo`（现有） | Redis GEO | 无 TTL，写操作维护 |

> **Caffeine 缓存名**：`storeDetail`、`storeValidity`，对应 Spring Cache `@CacheName`。

---

## 5. 读取流程

### 5.1 门店详情（`getStoreDetail(Long id)`）

```
1. 查 Caffeine storeDetail[id]
   命中 → 返回

2. 查 Redis store:detail:{id}
   命中 → 回填 Caffeine → 返回

3. 查 MySQL selectById(id)
   命中 → 序列化写 Redis（TTL 30min）→ 写 Caffeine → 返回
   未命中 → 返回 STORE_NOT_FOUND 错误（不缓存空值，见防穿透）
```

### 5.2 门店有效性（`checkValidity(Long id)`）

```
1. 查 Caffeine storeValidity[id]
   命中 → 返回

2. 查 Redis store:valid:{id}
   命中 → 回填 Caffeine → 返回

3. 查 MySQL
   命中 → 写 Redis（TTL 10min）→ 写 Caffeine → 返回
   未命中 → 写 "false" 进缓存（短 TTL 60s，防穿透）→ 返回 false
```

### 5.3 附近门店（`getNearbyStores`）

流程不变：Redis GEO GEORADIUS → `selectBatchIds` MySQL。  
**优化点**：`selectBatchIds` 结果可以逐个经过 L1/L2 缓存查询，减少 MySQL 批量查询压力（在门店数量增大后再考虑实施）。

---

## 6. 写操作缓存维护策略

采用 **Cache-Aside（旁路缓存）+ 写后删除** 模式：写操作先更新 DB，再删除缓存（而非更新缓存），由下次读请求触发回填。

> **不采用写时更新的原因**：写操作（如 update）包含地理编码等异步逻辑，状态复杂，删除比更新更安全可靠。

### 6.1 创建门店（`create`）

```
1. 写 MySQL
2. 同步 Redis GEO（现有）
3. 无需操作缓存（新门店尚未被缓存）
```

### 6.2 更新门店（`update`）

```
1. 写 MySQL
2. 同步 Redis GEO（现有）
3. 删除 Redis store:detail:{id}
4. 删除 Redis store:valid:{id}
5. 删除 Caffeine storeDetail[id]
6. 删除 Caffeine storeValidity[id]
```

### 6.3 删除门店（软删除，`delete`）

```
1. 更新 MySQL status = CLOSED
2. 从 Redis GEO 移除（现有）
3. 删除 Redis store:detail:{id}
4. 删除 Redis store:valid:{id}
5. 删除 Caffeine storeDetail[id]
6. 删除 Caffeine storeValidity[id]
```

---

## 7. 缓存三大问题防护

### 7.1 缓存击穿（热点 key 过期，大量请求打 DB）

**方案：Caffeine + 互斥锁（Mutex）**

- L1 Caffeine 作为第一道防线，大量并发请求在本地命中，不穿透到 Redis/DB
- Redis 层使用 `SETNX` 实现分布式锁，保证同一时刻只有一个实例回源 DB
- 锁粒度：`lock:store:detail:{id}`，TTL 3 秒，防止死锁

### 7.2 缓存穿透（查询不存在的门店）

**方案：空值缓存（针对有效性查询） + 参数校验**

- `checkValidity` 对不存在的 ID 缓存 `"false"`（TTL 60 秒），避免重复打 DB
- 所有接口在缓存层前做 ID 基本合法性校验（> 0），直接拦截明显无效请求
- Admin/Internal 接口无需防穿透（管理员操作，不存在恶意流量）

### 7.3 缓存雪崩（大量 key 同时过期）

**方案：TTL 随机抖动 + 本地缓存兜底**

- Redis key TTL 在基础值上叠加随机抖动：`TTL = baseTTL + Random(0, baseTTL * 0.2)`
- Caffeine 作为 L2 Redis 不可用时的降级兜底（Caffeine TTL 60s，短于 Redis）
- Redis 节点故障时，Caffeine 命中率下降但服务不中断（仍可兜底一分钟）

---

## 8. 数据一致性分析

### 强一致性场景（不允许读到旧数据）

当前业务中，门店有效性（`checkValidity`）被订单服务等下游依赖。若门店被关闭，下游应尽快感知。

**措施**：
- 删除操作执行后，L1 + L2 缓存立即删除（同步删除，非异步）
- `storeValidity` Caffeine TTL 设为 60 秒（短），最坏情况 60 秒后自动过期
- 如需强一致（即刻感知），可在 delete 接口同步发 MQ 事件，各实例收到消息后主动 evict L1

### 弱一致性场景（可短暂读旧数据）

- 门店名称、电话、营业时间等展示字段容忍 30 分钟内的轻微延迟
- Admin 分页不缓存，始终读最新数据

---

## 9. 缓存预热

服务启动后 L1 为空，冷启动期间请求全部透穿到 MySQL：

**预热策略**：
1. 应用启动时（`ApplicationRunner`），查询所有**非关闭**门店，批量写入 L2 Redis
2. L1 Caffeine 由首次请求触发懒加载，无需主动预热（TTL 短，60s 后自动轮转）
3. Redis 预热使用 Pipeline 批量写入，减少 RTT

---

## 10. 监控指标

| 指标 | 说明 |
|------|------|
| L1 命中率 | Caffeine `stats().hitRate()`，目标 > 80% |
| L2 命中率 | Redis `INFO stats` keyspace_hits/misses，目标 > 95% |
| DB 慢查询 | `store` 表 `selectById` 调用频率，缓存生效后应显著下降 |
| 缓存 key 数量 | Redis `DBSIZE` / `SCAN store:detail:*` 监控 key 规模 |

---

## 11. 实施阶段建议

| 阶段 | 内容 | 优先级 |
|------|------|--------|
| P0 | `checkValidity` 加两级缓存（Feign 高频调用，收益最大） | 高 |
| P1 | 门店详情（by ID）加两级缓存 | 中 |
| P2 | 附近门店结果的 `selectBatchIds` 走缓存 | 低 |
| P3 | 缓存预热 + 监控接入 | 低 |
