# 购物车设计文档

## 概述

购物车功能基于 Redis 存储，数据在 **24 小时无操作后自动过期**。每次对购物车进行写操作（加购、修改数量、删除条目）时刷新过期时间。

购物车以**顾客**为维度存储，不按门店隔离：一位顾客只有一个购物车。顾客切换门店时，需调用"切换门店"接口，系统将对购物车进行自检，下架的商品或客制化选项将被自动移除，并在响应中告知前端具体移除了哪些条目。

---

## 数据模型

### 商品与客制化关系（来自商品服务）

```
Product
  └── 绑定多个 CustomizationItem（客制化分类，如"甜度"、"冰量"）
            └── 每个 CustomizationItem 包含多个 CustomizationOption（选项，如"少糖"、"正常糖"）
                        └── CustomizationOption 有独立 price（附加价格，可为 0）
```

顾客加购时，需为每个 CustomizationItem 选择一个 CustomizationOption。最终价格 = 商品基础价 + 所有选中 option 的价格之和。

---

## SKU ID 设计

### 问题

同一商品在不同客制化配置下是不同的商品（如"少糖+去冰"和"全糖+正常冰"不应合并）。需要一种标识来唯一区分一个商品+客制化配置的组合。

### 方案

**skuId** = `{productId}:{optionId1},{optionId2},...`

生成规则：
1. 取本次加购所选的全部 `optionId`
2. 将 optionId 列表**升序排序**
3. 拼接格式：`productId:optionId1,optionId2,...`

**示例：**

| 商品ID | 选中 option | skuId |
|--------|-------------|-------|
| 42 | 甜度→3，冰量→7，杯型→12 | `42:3,7,12` |
| 42 | 甜度→3，冰量→9，杯型→12 | `42:3,9,12` |
| 42 | 无客制化 | `42:` |

**合并逻辑：** 加购时计算出 skuId，若购物车中已存在相同 skuId 的条目，则直接累加数量；否则新增条目。

---

## Redis 存储结构

### Key 设计

```
cart:{customerId}
```

- 类型：**Hash**
- Field：`skuId`
- Value：CartItem JSON 字符串

**示例：**
```
Key:   cart:1001
Field: 42:3,7,12
Value: {"skuId":"42:3,7,12","productId":42,"productName":"茉莉奶绿","basePrice":18.00,...}
```

### 过期策略

- 每次对该 Hash 进行写操作后，重置 TTL 为 **86400 秒（24 小时）**
- 使用 `EXPIRE cart:{customerId} 86400` 刷新
- 读取操作不刷新 TTL

---

## CartItem 数据结构

```json
{
  "skuId": "42:3,7,12",
  "productId": 42,
  "productName": "茉莉奶绿",
  "basePrice": 18.00,
  "quantity": 2,
  "selections": [
    {
      "itemId": 1,
      "itemName": "甜度",
      "optionId": 3,
      "optionName": "少糖",
      "optionPrice": 0.00
    },
    {
      "itemId": 2,
      "itemName": "冰量",
      "optionId": 7,
      "optionName": "去冰",
      "optionPrice": 0.00
    },
    {
      "itemId": 3,
      "itemName": "杯型",
      "optionId": 12,
      "optionName": "大杯",
      "optionPrice": 3.00
    }
  ],
  "unitPrice": 21.00,
  "addedAt": "2026-04-09T10:00:00"
}
```

字段说明：

| 字段 | 类型 | 说明 |
|------|------|------|
| skuId | String | 商品+客制化唯一标识 |
| productId | Long | 商品ID |
| productName | String | 商品名称（快照，加购时记录） |
| basePrice | BigDecimal | 商品基础价（快照） |
| quantity | Integer | 数量 |
| selections | List | 各客制化分类所选选项列表 |
| selections[].itemId | Long | 客制化分类ID |
| selections[].itemName | String | 客制化分类名称（快照） |
| selections[].optionId | Long | 选中的选项ID |
| selections[].optionName | String | 选项名称（快照） |
| selections[].optionPrice | BigDecimal | 选项附加价格（快照） |
| unitPrice | BigDecimal | 单价 = basePrice + sum(optionPrice)，加购时计算并存储 |
| addedAt | LocalDateTime | 首次加入购物车时间 |

> **快照设计说明：** productName、basePrice、optionName、optionPrice 在加购时写入快照，不随商品价格变动实时更新。购物车展示以快照为准，下单时再验价。

---

## API 设计

所有接口需要顾客登录态（从 JWT 中提取 customerId）。

### 1. 加购 / 增加数量

```
POST /v1/biz/carts/items
```

**请求体：**
```json
{
  "productId": 42,
  "quantity": 1,
  "selections": [
    { "itemId": 1, "optionId": 3 },
    { "itemId": 2, "optionId": 7 },
    { "itemId": 3, "optionId": 12 }
  ]
}
```

**处理逻辑：**
1. 计算 skuId
2. 查询 Redis，若 skuId 已存在 → quantity 累加
3. 若不存在 → 从商品服务读取商品与选项信息，构建 CartItem，写入 Hash
4. 刷新 TTL

**响应：** `ApiResponse<Void>`

---

### 2. 查看购物车

```
GET /v1/biz/carts
```

**响应：**
```json
{
  "items": [
    {
      "skuId": "42:3,7,12",
      "productId": 42,
      "productName": "茉莉奶绿",
      "quantity": 2,
      "unitPrice": 21.00,
      "subtotal": 42.00,
      "selections": [...]
    }
  ],
  "totalQuantity": 2,
  "totalPrice": 42.00
}
```

---

### 3. 修改条目数量

```
PUT /v1/biz/carts/items/{skuId}
```

**请求体：**
```json
{ "quantity": 3 }
```

- quantity = 0 时等同于删除该条目
- 刷新 TTL

**响应：** `ApiResponse<Void>`

---

### 4. 删除条目

```
DELETE /v1/biz/carts/items/{skuId}
```

- 从 Hash 中删除对应 field
- 若 Hash 变为空，删除整个 Key
- 刷新 TTL（若 Hash 非空）

**响应：** `ApiResponse<Void>`

---

### 5. 清空购物车

```
DELETE /v1/biz/carts
```

- 删除整个 Hash Key

**响应：** `ApiResponse<Void>`

---

### 6. 切换门店（购物车自检）

```
POST /v1/biz/carts/switch-store
```

顾客切换门店时调用此接口。系统将用新门店的商品数据对当前购物车进行一次完整校验：
- 商品已下架 → 整条 CartItem 移除
- 商品中有任一客制化选项已下架 → 整条 CartItem 移除

**请求体：**
```json
{
  "storeId": 5
}
```

**处理逻辑：**
1. 读取顾客当前购物车全部 CartItem
2. 向商品服务批量查询各 productId 在目标门店下的上架状态及其全部 optionId 的可用状态
3. 遍历 CartItem：
   - 若商品不存在或已下架 → 标记为移除，原因：`PRODUCT_UNAVAILABLE`
   - 若商品可售但存在任一 optionId 已下架 → 标记为移除，原因：`OPTION_UNAVAILABLE`
   - 否则保留
4. 从 Redis Hash 中批量删除被标记的条目
5. 若购物车非空，刷新 TTL

**响应：**
```json
{
  "removedItems": [
    {
      "skuId": "42:3,7,12",
      "productName": "茉莉奶绿",
      "reason": "PRODUCT_UNAVAILABLE"
    },
    {
      "skuId": "18:5,6",
      "productName": "桃桃乌龙",
      "reason": "OPTION_UNAVAILABLE"
    }
  ],
  "hasRemovedItems": true
}
```

`reason` 枚举值：

| 值 | 含义 |
|----|------|
| `PRODUCT_UNAVAILABLE` | 商品在目标门店已下架或不存在 |
| `OPTION_UNAVAILABLE` | 商品可售，但所选客制化选项中有一个或多个已下架 |

> 若购物车为空或全部条目均有效，`removedItems` 返回空列表，`hasRemovedItems` 为 `false`。

---

## 关键实现要点

### skuId 生成工具

```java
// 将 optionId 列表升序排序后拼接
public static String buildSkuId(Long productId, List<Long> optionIds) {
    String options = optionIds.stream()
        .sorted()
        .map(String::valueOf)
        .collect(Collectors.joining(","));
    return productId + ":" + options;
}
```

### Redis Key 工具

```java
// key 格式: cart:{customerId}
private String cartKey(Long customerId) {
    return "cart:" + customerId;
}
```

### 刷新过期时间

每次写操作后执行：
```java
redisTemplate.expire(cartKey, 24, TimeUnit.HOURS);
```

### URL Encoding of skuId

skuId 作为 URL 路径参数时，其中的 `:` 和 `,` 需要做 URL encode，或改为请求体传递。
建议将删除/修改接口的 skuId 改为放在请求体或 Query 参数中，避免路径编码问题。

---

## 边界情况

| 情况 | 处理方式 |
|------|----------|
| 商品已下架（快照存在但商品不可售） | 切换门店时自检移除，下单时也校验并提示 |
| 选项已下架 | 切换门店时自检移除，下单时也校验并提示 |
| quantity 减到 0 | 自动从 Hash 中删除该条目 |
| 购物车为空 | Hash 不存在，返回空列表 |
| TTL 到期 | Redis 自动清除，查询返回空购物车 |
| 切换门店时购物车为空 | 无需自检，直接返回空 removedItems |
| 切换门店后所有条目均被移除 | Hash 变为空则删除整个 Key |
