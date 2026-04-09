# 商品服务缓存架构设计

## 1. 背景与目标

### 1.1 问题分析

商品服务承载以下高频读取场景：
- 顾客端浏览商品列表、商品详情（高并发、读多写少）
- 门店端加载菜单结构（包含分组、商品、客制化项的嵌套查询，计算代价高）
- 客制化项与选项查询（多次 JOIN，每次点单都会触发）

当前无任何业务层缓存，所有请求直接打到数据库。

### 1.2 目标

- 降低数据库查询压力，核心读接口 DB 命中率降低 80% 以上
- 顾客端商品详情、菜单接口 P99 延迟 < 50ms
- 保证缓存与数据库最终一致性，误差窗口在可接受范围内

---

## 2. 缓存层次架构

采用 **两级缓存（L1 本地缓存 + L2 分布式缓存）** 架构。

```
请求
 │
 ▼
┌─────────────────────────────┐
│  L1: Caffeine 本地缓存       │  单实例内存，极低延迟（微秒级）
│  TTL: 30s ~ 2min            │
└────────────┬────────────────┘
             │ 未命中
             ▼
┌─────────────────────────────┐
│  L2: Redis 分布式缓存        │  跨实例共享，毫秒级延迟
│  TTL: 5min ~ 30min          │
└────────────┬────────────────┘
             │ 未命中
             ▼
┌─────────────────────────────┐
│  数据库（MySQL via MyBatis） │
└─────────────────────────────┘
```

### 2.1 为什么需要两级缓存

| 维度 | 仅 Redis | 仅本地缓存 | 两级缓存 |
|---|---|---|---|
| 延迟 | 毫秒级（网络开销） | 微秒级 | 微秒级（L1命中）/ 毫秒级（L2命中） |
| 一致性 | 强（单点） | 弱（多实例各自独立） | 较强（L2作为共享基准） |
| 容灾 | Redis宕机则全部穿透 | 进程重启则失效 | 互为兜底 |
| 适用流量 | 中等 | 极热点 | 高并发热点 |

---

## 3. 技术选型

### 3.1 L1 本地缓存 — Caffeine

**选择理由：**
- Spring Boot 3.x 官方推荐的本地缓存实现，与 `spring-boot-starter-cache` 深度集成
- 基于 W-TinyLFU 淘汰算法，命中率优于 LRU
- 支持细粒度 TTL 配置、最大容量限制、统计监控

**引入方式：**
```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <!-- 版本由 Spring Boot BOM 管理 -->
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### 3.2 L2 分布式缓存 — Redis

**已有基础：**
- `spring-boot-starter-data-redis` 已引入
- `commons-pool2` 连接池已配置
- `common` 模块中已有 `RedisConfiguration`，配置了 JSON 序列化的 `RedisTemplate<String, Object>`

**序列化方案：** `GenericJackson2JsonRedisSerializer`（已有配置，保持一致）

### 3.3 两级缓存协调 — 自定义 `TwoLevelCacheManager`

Spring 的 `@Cacheable` 注解只支持单一 `CacheManager`。需自定义实现，逻辑：
1. 读取时：先查 Caffeine → 再查 Redis → 最后查 DB，逐级回填
2. 写入/失效时：同时清除 Caffeine 和 Redis

可选方案：
- **方案A（推荐）：自定义 `TwoLevelCacheManager`** — 实现 Spring Cache 抽象，对上层透明，直接使用 `@Cacheable`/`@CacheEvict`
- **方案B：手动编码** — 在 Service 层显式操作，灵活但冗余代码多

**选择方案A**，保持代码整洁，统一 Cache 抽象。

---

## 4. 缓存数据与接口规划

### 4.1 需要缓存的数据

根据 **读多写少 + 查询代价高** 两个维度筛选：

| 数据 | 读频率 | 写频率 | DB查询代价 | 是否缓存 |
|---|---|---|---|---|
| 商品详情（含客制化绑定） | 极高 | 低 | 中（2-3次JOIN） | **是** |
| 门店商品列表（分页） | 高 | 低 | 中 | **是**（L2 only） |
| 菜单结构（含分组+商品） | 极高 | 低 | 高（嵌套查询） | **是** |
| 客制化项详情 | 高 | 低 | 低 | **是** |
| 客制化选项列表（按项） | 高 | 低 | 低 | **是** |
| 门店客制化项状态 | 高 | 中 | 中 | **是**（L2 only） |
| 食材列表（分页） | 低 | 低 | 低 | 否（管理端为主） |
| 食材库存 | 中 | 高（实时调整） | 低 | **否**（实时性要求高） |
| 管理端分页列表 | 低 | 中 | 中 | 否 |

### 4.2 接口缓存策略

#### 4.2.1 顾客/门店端（Biz）— 核心缓存接口

**商品详情**
```
接口：GET /v1/biz/products/{id}?storeId={storeId}
缓存键：product:biz:detail:{productId}:store:{storeId}
L1 TTL：60s
L2 TTL：10min
失效触发：商品更新、删除；门店商品状态变更
```

**门店商品列表**
```
接口：GET /v1/biz/products?storeId={storeId}&page={p}&size={s}&...
缓存层：L2 only（参数组合多，L1 存储效率低）
缓存键：product:biz:list:store:{storeId}:{参数哈希}
L2 TTL：5min
失效触发：商品上下架、商品信息更新
```

**门店菜单（含分组与商品）**
```
接口：GET /v1/biz/menus?storeId={storeId}
缓存键：menu:biz:store:{storeId}
L1 TTL：2min
L2 TTL：30min
失效触发：菜单结构变更；菜单与门店绑定/解绑；商品信息更新
说明：此接口查询代价最高，为最优先缓存目标
```

**客制化项详情**
```
接口：GET /v1/biz/customization-items/{id}（含门店状态）
缓存键：customization:item:{itemId}:store:{storeId}
L1 TTL：2min
L2 TTL：15min
失效触发：客制化项更新；门店端状态变更
```

**客制化选项列表**
```
接口：GET /v1/biz/customization-items/{itemId}/options（含门店状态）
缓存键：customization:item:{itemId}:options:store:{storeId}
L1 TTL：2min
L2 TTL：15min
失效触发：选项增删改；门店端选项状态变更
```

#### 4.2.2 管理端（Admin）— 有限缓存

**商品详情（管理端）**
```
接口：GET /v1/admin/products/{id}
缓存键：product:admin:detail:{productId}
L1 TTL：30s（管理端更新操作频繁，短TTL避免脏读）
L2 TTL：不缓存（管理端直接查DB，保证一致性）
```

**客制化项详情（管理端）**
```
接口：GET /v1/admin/customization-items/{id}
策略：同商品管理端，仅L1短TTL缓存
```

> 管理端列表（分页+筛选）组合多、写操作后需实时更新，**不做缓存**。

---

## 5. 缓存 Key 规范

### 5.1 命名规则

```
{服务前缀}:{模块}:{端}:{数据类型}:{维度1}:{值1}[:{维度2}:{值2}]
```

| 缓存项 | Key 示例 |
|---|---|
| Biz 商品详情 | `dextea:product:biz:detail:productId:42:storeId:7` |
| Biz 商品列表 | `dextea:product:biz:list:storeId:7:hash:a3f2c1` |
| Biz 菜单 | `dextea:menu:biz:storeId:7` |
| Biz 客制化项 | `dextea:customization:biz:item:itemId:5:storeId:7` |
| Biz 客制化选项 | `dextea:customization:biz:options:itemId:5:storeId:7` |
| Admin 商品详情 | `dextea:product:admin:detail:productId:42` |

### 5.2 通配符失效模式（Redis SCAN）

```
商品更新后，失效所有相关 Key：
  SCAN + DEL  dextea:product:*:detail:productId:{id}:*
  SCAN + DEL  dextea:product:biz:list:storeId:{storeId}:*
  SCAN + DEL  dextea:menu:biz:storeId:{storeId}
```

> 使用 `SCAN` 替代 `KEYS`，避免生产环境阻塞。

---

## 6. 缓存一致性策略

### 6.1 写操作失效策略（Cache-Aside + 先更新DB再删缓存）

```
写操作流程：
1. 写入数据库（MyBatis Mapper）
2. 删除 L1 缓存（Caffeine evict）
3. 删除 L2 缓存（Redis DEL / SCAN + DEL）
4. 返回结果

读操作流程：
1. 查 L1（Caffeine）→ 命中则返回
2. 查 L2（Redis）→ 命中则回填 L1，返回
3. 查 DB → 回填 L2，回填 L1，返回
```

**为何选择删除而非更新缓存：** 避免并发写场景下缓存值覆盖顺序问题（Delete 操作是幂等的）。

### 6.2 各写操作的失效范围

| 触发操作 | 需失效的缓存 |
|---|---|
| 更新商品信息（名称、价格、描述） | 该商品所有门店的 `product:biz:detail`；该商品所在门店的 `product:biz:list`；包含该商品的 `menu:biz` |
| 商品上下架（门店端 sale 状态） | 对应门店的 `product:biz:detail`、`product:biz:list`、`menu:biz` |
| 菜单绑定/解绑门店 | 对应门店的 `menu:biz` |
| 更新客制化项 | 所有门店的 `customization:biz:item:{itemId}:*`、`product:biz:detail`（含该项的商品） |
| 更新客制化选项 | `customization:biz:options:itemId:{itemId}:*` |
| 门店客制化项状态变更 | `customization:biz:item:{itemId}:store:{storeId}`、`customization:biz:options:itemId:{itemId}:store:{storeId}` |

### 6.3 Biz 端门店状态变更的局部失效

门店端操作（如商品上下架、客制化项状态变更）只影响特定门店的视图，应精确失效带 `storeId` 的缓存，**不需要**全量失效该商品的所有缓存。

---

## 7. 容量与 TTL 规划

### 7.1 TTL 汇总

| 缓存项 | L1 TTL | L2 TTL |
|---|---|---|
| Biz 菜单（storeId） | 2min | 30min |
| Biz 商品详情 | 60s | 10min |
| Biz 商品列表 | 不缓存 | 5min |
| Biz 客制化项 | 2min | 15min |
| Biz 客制化选项列表 | 2min | 15min |
| Admin 商品/客制化项详情 | 30s | 不缓存 |

### 7.2 L1 Caffeine 容量规划

```
每个缓存命名空间的最大条目数（maximumSize）：

product-biz-detail        → 500 条（按商品ID+门店ID）
menu-biz                  → 100 条（按门店ID）
customization-item-biz    → 300 条（按itemId+storeId）
customization-options-biz → 300 条（按itemId+storeId）
product-admin-detail      → 200 条
```

> 估算依据：单实例堆内存按 50MB 上限，每条缓存对象平均 5-10KB（包含序列化后数据）。

### 7.3 L2 Redis 内存估算

| 缓存项 | 单条大小 | 最大条目 | 估算内存 |
|---|---|---|---|
| 商品详情（含客制化） | ~3KB | 10,000 | ~30MB |
| 菜单结构（含分组+商品） | ~20KB | 500 | ~10MB |
| 客制化项 | ~2KB | 5,000 | ~10MB |
| 客制化选项列表 | ~3KB | 5,000 | ~15MB |
| 商品列表（每页） | ~10KB | 3,000 | ~30MB |
| **合计** | | | **~95MB** |

---

## 8. 缓存保护机制

### 8.1 缓存穿透 — 缓存空值

对于查询结果为空（商品不存在、门店无此商品）的情况，缓存空对象，TTL 设为较短时间（60s），防止恶意或异常请求反复穿透到 DB。

```
如果 DB 查询结果为 null：
  L2 存储标记值（如 "NULL_PLACEHOLDER"）TTL = 60s
  L1 存储 Optional.empty() TTL = 30s
```

### 8.2 缓存击穿 — 本地互斥锁

热点 Key 失效瞬间，大量并发请求同时穿透到 DB（缓存击穿）。

防护方案：在 L2 未命中并准备查询 DB 时，使用 **Caffeine 本身的 `LoadingCache`** 内置互斥（同一 Key 同时只有一个线程执行加载函数），或在 Service 层使用 `synchronized` 块 + double-check。

### 8.3 缓存雪崩 — TTL 随机抖动

避免大量缓存同时过期：
```
实际 TTL = 配置 TTL × (1 + random(-0.1, 0.1))
```
在写入 L2 时，对 TTL 加 ±10% 随机抖动。

### 8.4 大 Key 告警

菜单结构数据可能体积较大，需监控：
- Redis 写入前检查序列化后大小，超过 100KB 则记录告警日志
- 考虑拆分：菜单元数据与菜单内商品列表分开缓存

---

## 9. 缓存监控与可观测性

### 9.1 关键指标

| 指标 | 来源 | 告警阈值 |
|---|---|---|
| L1 命中率 | Caffeine Stats | < 60% 告警 |
| L2 命中率 | Redis `INFO stats` / Micrometer | < 70% 告警 |
| L2 内存使用 | Redis `INFO memory` | > 80% 告警 |
| 缓存穿透率（DB查询量） | Micrometer + DB慢查询日志 | 突增 200% 告警 |
| 缓存操作延迟 | Micrometer Timer | P99 > 10ms 告警 |

### 9.2 暴露方式

- 通过 Spring Boot Actuator + Micrometer 将 Caffeine 统计数据暴露为 Prometheus 指标
- Redis 指标通过 `spring.cache.redis` 配置开启统计
- 指标前缀：`dextea.product.cache.*`

---

## 10. 与现有架构的集成点

### 10.1 配置中心（Nacos）

Redis 连接配置已通过 Nacos 动态下发（`redis.yaml`），缓存相关配置（TTL、容量）也应放入 Nacos，支持不重启调整：

```yaml
# Nacos: cache.yaml
dextea:
  cache:
    product-detail:
      l1-ttl-seconds: 60
      l2-ttl-seconds: 600
    menu-biz:
      l1-ttl-seconds: 120
      l2-ttl-seconds: 1800
```

### 10.2 服务调用关系

菜单接口的缓存数据包含从 `store-service` 获取的门店信息（如门店状态），需要注意：
- 缓存失效也应考虑跨服务变更触发（如门店被停用时，应失效相关门店的菜单缓存）
- 可通过发布领域事件（MQ）或在调用方做延时自动过期来处理

---

## 11. 实施优先级

| 优先级 | 缓存项 | 理由 |
|---|---|---|
| P0 | Biz 菜单（`GET /v1/biz/menus`） | 查询代价最高，每次点单必查 |
| P0 | Biz 商品详情（`GET /v1/biz/products/{id}`） | 高频核心接口 |
| P1 | Biz 客制化项与选项 | 商品详情依赖，一并缓存 |
| P1 | Biz 商品列表（L2） | 浏览场景频繁 |
| P2 | Admin 商品/客制化详情（L1短TTL） | 降低管理端 DB 压力 |
| P2 | 缓存监控接入 | 可观测性，线上稳定性保障 |
