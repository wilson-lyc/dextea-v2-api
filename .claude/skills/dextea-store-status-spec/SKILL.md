---
name: dextea-store-status-spec
description: Use when an entity needs both a global status (stored on the entity table) and a per-store status (stored in a dedicated status table). Defines the complete specification for table design, entity, enum, DTO, service read/write patterns, and page-query filter logic.
---

# Dextea Global Status + Store Status Management Specification

Apply this skill whenever an entity has both a **global status** (e.g. product on-shelf / off-shelf) and a **per-store status** (e.g. available / sold-out at each store). All rules are mandatory. Do not invent alternative patterns.

---

## 1. Core Design Principles

| Dimension | Storage | Read default | Write strategy |
|---|---|---|---|
| **Global status** | `status` field on the entity's main table | — | Direct `updateById` |
| **Store status** | Dedicated store-status table | No record → DISABLED | EXISTS → UPDATE, absent → INSERT |

**Do not pre-populate the store-status table when creating an entity.** The table is written lazily on demand. On read, a missing record is treated as DISABLED.

---

## 2. Store-Status Table Design

### DDL Structure

```sql
CREATE TABLE `store_{entity}_status`
(
    `store_id`    BIGINT   NOT NULL COMMENT 'Store ID',
    `{entity}_id` BIGINT   NOT NULL COMMENT '{Entity} ID',
    `status`      TINYINT  NOT NULL DEFAULT 0 COMMENT 'Store {entity} status: 0=disabled, 1=enabled',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    PRIMARY KEY (`store_id`, `{entity}_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = 'Store {entity} status table';
```

Rules:
- No `update_time` — follows the Relation Entity convention (see dextea-entity-spec)
- The composite PK `(store_id, {entity}_id)` guarantees uniqueness; no additional unique index is needed
- `status` defaults to `0` (DISABLED), matching the code-layer default
- **No foreign key constraints** — consistency is enforced by the business layer

### Naming Conventions

| Table name | Entity class | Mapper interface |
|---|---|---|
| `store_product_status` | `StoreProductStatusEntity` | `StoreProductStatusMapper` |
| `store_customization_item_status` | `StoreCustomizationItemStatusEntity` | `StoreCustomizationItemStatusMapper` |

---

## 3. Status Enum Design

### Global Status Enum

```java
@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    DISABLED(0, "Off shelf"),
    ENABLED(1, "On shelf");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) return false;
        for (ProductStatus s : values()) {
            if (s.value == value) return true;
        }
        return false;
    }
}
```

### Store Status Enum

Naming rule: `Store{EntityName}Status`

```java
@Getter
@RequiredArgsConstructor
public enum StoreProductStatus {
    DISABLED(0, "Sold out"),
    ENABLED(1, "Available");

    private final Integer value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) return false;
        for (StoreProductStatus s : values()) {
            if (Objects.equals(s.value, value)) return true;
        }
        return false;
    }
}
```

Rules:
- Global status `value` field uses `int` (primitive); store status `value` field uses `Integer` (wrapper)
- `DISABLED` value is always `0` and is the default for store status
- `isValid` must be implemented — it is used by the `@EnumValue` validation annotation

---

## 4. Request DTO Design

### Update Store Status Request

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStore{Entity}StatusRequest {

    @NotNull(message = "Store ID is required")
    @Min(value = 1, message = "Invalid store ID")
    private Long storeId;

    @NotNull(message = "Store status is required")
    @EnumValue(enumClass = Store{Entity}Status.class, fieldName = "store status")
    private Integer status;
}
```

`storeId` is carried in the request body, not as a path variable. Example controller signature:

```java
@PutMapping("/{id}/store-status")
public ApiResponse<Void> updateStoreStatus(
        @PathVariable Long id,
        @RequestBody @Validated UpdateStore{Entity}StatusRequest request) {
    return service.updateStatus(id, request);
}
```

---

## 5. Service Layer: Writing Store Status

### Standard Upsert Logic

```java
@Transactional(rollbackFor = Exception.class)
public ApiResponse<Void> updateStatus(Long entityId, UpdateStoreProductStatusRequest request) {
    // 1. Verify entity exists
    if (entityMapper.selectById(entityId) == null) {
        return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

    Long storeId = request.getStoreId();

    // 2. Look up the current store-status record
    StoreProductStatusEntity existing = storeStatusMapper.selectOne(
            new LambdaQueryWrapper<StoreProductStatusEntity>()
                    .eq(StoreProductStatusEntity::getStoreId, storeId)
                    .eq(StoreProductStatusEntity::getProductId, entityId));

    if (existing == null) {
        // 3a. No record → INSERT
        StoreProductStatusEntity statusEntity = StoreProductStatusEntity.builder()
                .storeId(storeId)
                .productId(entityId)
                .status(request.getStatus())
                .build();
        if (storeStatusMapper.insert(statusEntity) != 1) {
            return fail(ProductErrorCode.STORE_SALE_STATUS_UPDATE_FAILED);
        }
    } else {
        // 3b. Record exists → UPDATE
        int rows = storeStatusMapper.update(null,
                new LambdaUpdateWrapper<StoreProductStatusEntity>()
                        .eq(StoreProductStatusEntity::getStoreId, storeId)
                        .eq(StoreProductStatusEntity::getProductId, entityId)
                        .set(StoreProductStatusEntity::getStatus, request.getStatus()));
        if (rows != 1) {
            return fail(ProductErrorCode.STORE_SALE_STATUS_UPDATE_FAILED);
        }
    }

    return ApiResponse.success();
}
```

---

## 6. Service Layer: Reading Store Status

### Single Entity

```java
// Query status record; fall back to DISABLED if absent
StoreProductStatusEntity storeStatusRecord = storeStatusMapper.selectOne(
        new LambdaQueryWrapper<StoreProductStatusEntity>()
                .eq(StoreProductStatusEntity::getStoreId, storeId)
                .eq(StoreProductStatusEntity::getProductId, productId));

int storeStatus = storeStatusRecord != null
        ? storeStatusRecord.getStatus()
        : StoreProductStatus.DISABLED.getValue();
```

### Batch Back-fill (Paginated List)

```java
private IPage<ProductDetailResponse> fillStoreStatuses(IPage<ProductEntity> page, Long storeId) {
    List<ProductEntity> products = page.getRecords();
    if (products.isEmpty()) {
        return page.convert(
                entity -> converter.toResponse(entity, StoreProductStatus.DISABLED.getValue()));
    }

    List<Long> ids = products.stream().map(ProductEntity::getId).toList();

    // Batch-query status records and build id → status map
    Map<Long, Integer> statusMap = storeStatusMapper.selectList(
            new LambdaQueryWrapper<StoreProductStatusEntity>()
                    .eq(StoreProductStatusEntity::getStoreId, storeId)
                    .in(StoreProductStatusEntity::getProductId, ids))
            .stream()
            .collect(Collectors.toMap(
                    StoreProductStatusEntity::getProductId,
                    StoreProductStatusEntity::getStatus,
                    (left, right) -> right));

    // Missing records default to DISABLED
    return page.convert(entity -> {
        int storeStatus = statusMap.getOrDefault(entity.getId(), StoreProductStatus.DISABLED.getValue());
        return converter.toResponse(entity, storeStatus);
    });
}
```

---

## 7. Service Layer: Filtering Pages by Store Status

The filter logic depends on the invariant that the default store status is DISABLED. The two cases are handled differently.

### Filter target = DISABLED (the default)

Because a missing record already means DISABLED, a direct `IN` filter would miss entities with no record. Use **inverse exclusion** instead:

```java
// Find all IDs explicitly set to a non-DISABLED status, then exclude them with NOT IN
List<Long> excludedIds = storeStatusMapper.selectList(
        new LambdaQueryWrapper<StoreProductStatusEntity>()
                .eq(StoreProductStatusEntity::getStoreId, storeId)
                .ne(StoreProductStatusEntity::getStatus, StoreProductStatus.DISABLED.getValue()))
        .stream()
        .map(StoreProductStatusEntity::getProductId)
        .toList();

if (!excludedIds.isEmpty()) {
    productQuery.notIn(ProductEntity::getId, excludedIds);
}
// If excludedIds is empty, no filter is applied — all entities are treated as DISABLED
```

### Filter target = non-default value (e.g. ENABLED)

```java
// Fetch matching IDs directly, filter with IN
List<Long> matchingIds = storeStatusMapper.selectList(
        new LambdaQueryWrapper<StoreProductStatusEntity>()
                .eq(StoreProductStatusEntity::getStoreId, storeId)
                .eq(StoreProductStatusEntity::getStatus, targetStatus))
        .stream()
        .map(StoreProductStatusEntity::getProductId)
        .toList();

if (matchingIds.isEmpty()) {
    // No matches — return an empty page immediately
    return ApiResponse.success(new Page<>(request.getCurrent(), request.getSize()));
}
productQuery.in(ProductEntity::getId, matchingIds);
```

### Page Method Organisation Pattern

Split into two private overloads and dispatch from the public entry point:

```java
@Override
public ApiResponse<IPage<EntityResponse>> getPage(StoreEntityPageRequest request) {
    LambdaQueryWrapper<EntityEntity> query = buildBaseQuery(request);

    if (request.getStoreStatus() != null) {
        return getPage(request, request.getStoreId(), request.getStoreStatus(), query);
    }
    return getPage(request, request.getStoreId(), query);
}

// With store-status filter
private ApiResponse<IPage<EntityResponse>> getPage(
        StoreEntityPageRequest request, Long storeId, Integer targetStatus,
        LambdaQueryWrapper<EntityEntity> query) { ... }

// Without store-status filter
private ApiResponse<IPage<EntityResponse>> getPage(
        StoreEntityPageRequest request, Long storeId,
        LambdaQueryWrapper<EntityEntity> query) { ... }
```

---

## 8. ErrorCode Requirements

Store-status update failures require a dedicated error code:

```java
STORE_SALE_STATUS_UPDATE_FAILED(xxxxx, "Failed to update store {entity} status"),
```

Follow the ErrorCode design rules in dextea-backend-spec.

---

## 9. Key Constraints Summary

1. **No pre-population**: do not write to the store-status table when creating an entity
2. **Default is DISABLED**: a missing store-status record is always treated as DISABLED in code
3. **Manual upsert — never `saveOrUpdate`**: call `selectOne` first, then INSERT or UPDATE, and check the affected-row count
4. **Composite PK**: the store-status table's primary key is `(store_id, {entity}_id)` — no separate auto-increment ID
5. **No `update_time`**: store-status tables follow the Relation Entity convention and only have `create_time`
6. **No enum in Entity**: the `status` field on an entity is `Integer`; enum conversion happens in the service or converter layer
