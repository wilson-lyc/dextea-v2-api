# 商品全局状态与门店状态管理方案

## 1. 目标

本文档说明 `product-service` 中商品全局状态与门店状态的推荐实现方案。

适用场景：

- 商品存在一个公司级全局状态
- 同一个商品会在多个门店维度下维护门店状态
- 全局状态变更频率低于门店状态变更频率
- 要求状态判断强一致，允许门店状态读时懒修复

本方案采用 `versioned-status-sync` 模式：

- `product.status` 作为商品全局状态
- `product.version` 作为全局状态版本号
- `store_product_status.status` 作为门店商品状态
- `store_product_status.version` 作为门店状态所对齐的商品版本号

核心约束：

- 仅当 `store_product_status.version = product.version` 时，门店状态才有效
- 版本不一致的门店状态必须按默认禁用态处理
- 本模块统一使用 `0 = 售罄/不可售` 作为门店状态默认禁用值

## 2. 数据模型

### 2.1 商品主表

表：`product`

关键字段：

- `id`：商品 ID
- `status`：全局状态，`0=下架`，`1=上架`
- `version`：全局状态版本号

说明：

- 每次修改商品全局状态时，都要递增 `version`
- 商品名称、价格、描述更新不要求变更 `version`

### 2.2 门店商品状态表

表：`store_product_status`

关键字段：

- `store_id`：门店 ID
- `product_id`：商品 ID
- `status`：门店状态，`0=售罄`，`1=在售`
- `version`：当前门店状态所对齐的商品版本号

说明：

- 主键为 `(store_id, product_id)`
- 门店状态写入时必须把 `version` 设置为商品当前 `product.version`
- 不存在门店记录时，按 `status = 0` 处理

## 3. 状态语义

### 3.1 全局状态

- `product.status = 0`：商品全局下架
- `product.status = 1`：商品全局上架

全局下架优先级高于门店状态。

即使某门店记录为在售，只要商品全局下架，外部读路径也必须按不可售处理。

### 3.2 门店状态

- `store_product_status.status = 0`：门店售罄
- `store_product_status.status = 1`：门店在售

门店状态只有在版本匹配时才生效：

- `status = 1` 且 `version = product.version`，才算门店有效在售
- `status = 0` 且 `version = product.version`，算门店有效售罄
- `version != product.version`，无论门店状态原值为何，都按售罄处理
- 不存在门店状态记录，也按售罄处理

## 4. 为什么需要 version

如果商品全局状态变化后不处理门店数据，会出现历史门店状态继续生效的问题。

例子：

1. 商品 A 全局上架，`product.version = 3`
2. 门店 X 将商品 A 设为在售，写入 `store_product_status.status = 1, version = 3`
3. 公司把商品 A 全局下架，再重新上架，`product.version` 变为 `4`
4. 此时门店 X 的旧记录仍是 `version = 3`

如果没有版本校验，门店 X 仍会被误判为在售。

有版本机制后：

- 旧门店状态会被识别为过期
- 读路径返回售罄
- 后台异步感知不是必须，读时懒修复即可把旧记录重置为 `status = 0, version = 4`

## 5. 写路径设计

### 5.1 商品全局状态更新

入口：

- 公司端更新商品全局状态

规则：

1. 开启事务
2. `SELECT product ... FOR UPDATE`
3. 更新 `product.status`
4. `product.version = product.version + 1`
5. 清理受影响缓存
6. 提交事务

注意：

- 不主动 fan-out 更新所有门店状态
- 门店状态统一通过版本失效

### 5.2 门店商品状态更新

入口：

- 门店端更新某商品在本门店的在售/售罄状态

规则：

1. 开启事务
2. `SELECT product ... FOR UPDATE`
3. `SELECT store_product_status ... FOR UPDATE`
4. 若不存在则插入
5. 若已存在则更新
6. 写入 `store_product_status.version = product.version`
7. 清理受影响缓存
8. 提交事务

锁顺序固定为：

1. `product`
2. `store_product_status`

禁止反向加锁，避免死锁和状态竞争。

## 6. 读路径设计

### 6.1 通用判定规则

任意依赖门店商品状态的读路径，都应遵循以下顺序：

1. 读取商品全局数据
2. 读取门店商品状态
3. 比对 `store_product_status.version` 与 `product.version`
4. 版本不一致时，把该门店状态视为售罄
5. 对过期记录执行懒修复
6. 当前响应返回售罄结果

### 6.2 门店商品分页

适用接口：

- 门店端商品分页查询

规则：

- 仅查询全局上架商品
- 若按门店状态筛选“在售”，则只认 `status = 1 且 version = product.version`
- 若按门店状态筛选“售罄”，则包含以下商品：
  - 没有门店状态记录
  - 门店状态为售罄
  - 门店状态版本过期

### 6.3 商品详情

适用接口：

- 顾客端商品详情

规则：

- 商品全局下架时直接返回业务错误
- 商品全局上架时，门店状态通过版本判定决定最终 `storeStatus`
- 门店状态缺失或过期，统一返回售罄

### 6.4 批量可售校验

适用接口：

- 内部购物车切店校验
- 批量商品可用性判断

规则：

- 商品可售必须同时满足：
  - 商品存在
  - 商品全局上架
  - 门店状态有效且为在售

### 6.5 门店菜单

适用接口：

- 顾客端按门店拉取菜单

规则：

- 菜单中的商品不仅要全局上架
- 还必须是当前门店有效在售商品
- 版本过期或缺失的门店状态，都不能出现在门店菜单中

## 7. 懒修复策略

### 7.1 为什么使用懒修复

商品全局状态更新是低频操作，而门店可能很多。

如果每次商品全局状态变化都同步更新所有门店状态，会带来：

- 大量无意义写入
- 更高的事务开销
- 更复杂的失败恢复逻辑

因此本方案使用懒修复：

- 全局状态变化时只更新 `product.status/version`
- 读路径发现门店状态版本过期后，再把门店记录修正为最新版本下的售罄状态

### 7.2 懒修复规则

若发现某门店状态已过期：

- 数据库修复为 `status = 0`
- `version` 更新为当前 `product.version`
- 当前请求仍然返回售罄

### 7.3 批量懒修复

在分页或批量读场景中：

- 先批量读取门店状态
- 找出版本不一致的商品集合
- 对过期集合执行批量 `UPDATE`
- 当前请求直接按售罄返回

## 8. Mapper 与 Service 职责划分

### 8.1 Mapper

负责：

- 悲观锁查询
- 显式更新门店状态
- 批量重置过期门店状态
- 查询某门店下当前有效在售商品 ID 集合

不建议在 Service 中拼接跨表 SQL 字符串。

像“查询某门店当前有效在售商品 ID”这类 SQL，应下沉到 mapper，例如：

- `selectValidEnabledProductIdsByStoreId`

### 8.2 Service

负责：

- 业务校验
- 事务边界
- 锁顺序控制
- 缓存失效
- 结果组装

## 9. 缓存策略

### 9.1 商品全局状态更新后

需要清理：

- 商品详情缓存
- 依赖商品可售状态的菜单缓存

原因：

- 全局状态变化会影响所有门店下的最终可售结果

### 9.2 门店商品状态更新后

需要清理：

- 对应 `productId + storeId` 的商品详情缓存
- 对应门店菜单缓存

原因：

- 门店商品状态会直接影响顾客端商品详情和门店菜单展示

## 10. 当前实现要点

当前代码实现中，关键点如下：

- `ProductMapper.selectByIdForUpdate`：锁商品主记录
- `StoreProductStatusMapper.selectByStoreIdAndProductIdForUpdate`：锁门店状态记录
- `StoreProductStatusMapper.updateStatus`：按复合主键显式更新门店状态
- `StoreProductStatusMapper.resetExpiredStatuses`：批量重置过期门店状态
- `StoreProductStatusMapper.selectValidEnabledProductIdsByStoreId`：查询门店下当前有效在售商品 ID
- `ProductStoreStatusSyncSupport`：统一封装版本校验、懒修复、有效状态映射构建

## 11. 典型时序

### 11.1 全局下架后首次门店读取

1. 商品原本上架，门店记录为在售
2. 公司将商品全局下架，`product.version + 1`
3. 顾客端或门店端再次读取该商品
4. 读取到门店状态版本旧于商品版本
5. 当前请求返回售罄
6. 后台将旧门店记录修正为 `status = 0, version = 最新版本`

### 11.2 门店重新上架商品

1. 读取商品并加锁
2. 读取门店状态记录并加锁
3. 写入 `status = 1`
4. 写入 `version = 当前 product.version`
5. 清理门店详情和菜单缓存
6. 后续读取开始返回门店在售

## 12. 方案边界

本方案适用于：

- 商品全局状态变更低频
- 单商品并发写冲突量可接受
- 更重视正确性，而不是极限吞吐

如果未来出现以下情况，应评估是否切换方案：

- 同一商品的门店状态更新极高频
- 商品全局状态不再是低频操作
- 大量写请求被 `product FOR UPDATE` 串行化后成为瓶颈

此时可考虑：

- 异步传播
- 条件更新
- 更细粒度的状态拆分

但在当前业务下，不建议只局部弱化锁模型，否则容易引入状态竞争问题。

## 13. 总结

本方案的核心原则只有三条：

1. 商品全局状态是真正的主状态
2. 门店状态只有在版本与商品一致时才有效
3. 写入走悲观锁，读取走懒修复

这样可以在不进行全量门店 fan-out 更新的前提下，保证商品全局状态与门店状态的一致性，并把复杂度控制在可维护范围内。
