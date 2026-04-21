---
name: dextea-controller-spec
description: Use when adding or modifying Controller classes in this repository. Defines the complete specification for RESTful controller design: naming conventions, class-level annotations, method naming, Javadoc format, parameter validation, and templates for Admin, Biz, Internal, and Auth controllers.
---

# Dextea Controller Spec

Use this skill when creating or modifying any Controller class. Treat this document as the authoritative standard for all controller code in this repository.

## Controller type selection

Choose controller type by caller, not by developer preference:

| Type | Class suffix | Route prefix | Caller |
|------|-------------|--------------|--------|
| Admin | `XxxAdminController` | `/v1/admin/` | Internal company management |
| Biz | `XxxBizController` | `/v1/biz/` | End-user business flows |
| Internal | `XxxInternalController` | `/v1/internal/` | Other microservices (Feign) |
| Auth | `XxxAuthController` | `/v1/auth/` | Authentication flows |

Never mix caller types in a single controller class.

## Class-level annotations

All controllers must carry these annotations in this order:

```java
@RestController
@RequestMapping("/v1/{scope}/{resources}")
@RequiredArgsConstructor
@Validated
```

Add `@SaCheckLogin` immediately after `@Validated` for any controller that requires a logged-in session. Omit it for public or internal endpoints that do not require session verification.

```java
// Admin controller requiring login
@RestController
@RequestMapping("/v1/admin/staffs")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class StaffAdminController { ... }

// Internal controller — no login check
@RestController
@RequestMapping("/v1/internal/products")
@RequiredArgsConstructor
@Validated
public class ProductInternalController { ... }
```

Add a class-level Javadoc that names the business domain:

```java
/**
 * 员工管理
 */
@RestController
...
public class StaffAdminController { ... }
```

## Route conventions

Use plural resource nouns. Use versioned, scope-prefixed paths:

```
/v1/admin/staffs
/v1/admin/roles
/v1/biz/products
/v1/biz/stores
/v1/internal/products
```

Standard route patterns:

| Operation | HTTP method | Path |
|-----------|------------|------|
| Create resource | `POST` | `/v1/{scope}/{resources}` |
| Page query | `GET` | `/v1/{scope}/{resources}` |
| Detail query | `GET` | `/v1/{scope}/{resources}/{id}` |
| Update resource | `PUT` | `/v1/{scope}/{resources}/{id}` |
| Delete / disable resource | `DELETE` | `/v1/{scope}/{resources}/{id}` |
| Enable resource | `PUT` | `/v1/{scope}/{resources}/{id}/enable` |
| Explicit state change | `PUT` | `/v1/{scope}/{resources}/{id}/{action}` |
| Bind sub-relation | `POST` | `/v1/{scope}/{resources}/{id}/{relations}` |
| Bind specific sub-relation | `POST` | `/v1/{scope}/{resources}/{id}/{relations}/{relId}` |
| Unbind sub-relation | `DELETE` | `/v1/{scope}/{resources}/{id}/{relations}/{relId}` |
| Update sub-resource | `PUT` | `/v1/{scope}/{resources}/{id}/{sub}` |

Avoid verb suffixes like `/add`, `/update`, `/delete` in route paths.

## Method naming

Method names must follow this pattern: `verb + ResourceName` or `verb + ResourceName + Qualifier`.

The resource name part is the entity name in PascalCase. The verb must match the operation.

### Standard verb map

| Operation | Verb prefix | Example |
|-----------|------------|---------|
| Create | `create` | `createStaff` |
| Page query | `get` + name + `Page` | `getStaffPage` |
| Detail query | `get` + name + `Detail` | `getStaffDetail` |
| Update (general) | `update` | `updateStaff` |
| Update specific field group | `update` + name + Qualifier | `updateStaffInfo`, `updateProductStatus` |
| Delete or disable | `delete` | `deleteStaff` |
| Enable | `enable` | `enableStaff` |
| Reset | `reset` + name + Qualifier | `resetStaffPassword` |
| Assign / bind | `assign` or `bind` | `assignStaffRole`, `bindStaffStore` |
| Remove / unbind | `remove` or `unbind` | `removeStaffRole`, `unbindStaffStore` |

Use `assign`/`remove` for role-like relations, `bind`/`unbind` for store-like relations.

For Biz controllers where the operation name is clear and entity is already in the class name, a shorter form is acceptable:

```java
// BizController method — short form acceptable
public ApiResponse<List<StoreNearbyResponse>> getNearbyStores(...) { ... }
```

For Internal controllers, use descriptive names that explain what data is fetched:

```java
public ApiResponse<ProductDetailResponse> getProductById(...) { ... }
public ApiResponse<List<ProductDetailResponse>> getProductsByIds(...) { ... }
```

## Javadoc format

Every controller method must have a Javadoc comment. The format is:

```java
/**
 * 接口描述（Chinese, imperative verb phrase）
 * @param paramName 参数说明
 * @return 返回值说明
 */
```

Rules:

- The description line is always Chinese.
- Include one `@param` line per method parameter. Path variables and request body both count.
- The `@return` line must describe what is returned, not just the type.
- If a path variable is `id`, its `@param` line is `@param id {实体名}ID`.
- Do not omit Javadoc even for simple passthrough methods.

Examples:

```java
/**
 * 创建员工
 * @param request 创建员工请求参数
 * @return 创建结果
 */

/**
 * 分页查询员工列表
 * @param request 员工分页查询请求参数
 * @return 员工分页数据
 */

/**
 * 查询员工详情
 * @param id 员工ID
 * @return 员工详情
 */

/**
 * 更新员工资料
 * @param id 员工ID
 * @param request 更新员工请求参数
 * @return 更新后的员工详情
 */

/**
 * 禁用员工账号
 * @param id 员工ID
 * @return 禁用结果
 */

/**
 * 为员工分配角色
 * @param id 员工ID
 * @param roleId 角色ID
 * @return 分配结果
 */
```

## Parameter rules

- DTO parameters: always annotate with `@Valid`.
- Request body DTOs: annotate with `@Valid @RequestBody`.
- Query string DTOs: annotate with `@Valid` only (no `@RequestBody`).
- Path variable IDs: annotate with `@PathVariable("id") @Min(value = 1, message = "{实体名}ID不能为空") Long id`.
- Always name the path variable explicitly in `@PathVariable("id")`.

```java
// Request body
public ApiResponse<CreateStaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request)

// Query string (GET with DTO)
public ApiResponse<IPage<StaffDetailResponse>> getStaffPage(@Valid StaffPageQueryRequest request)

// Path variable only
public ApiResponse<StaffDetailResponse> getStaffDetail(
        @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id)

// Path variable + request body
public ApiResponse<StaffDetailResponse> updateStaff(
        @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id,
        @Valid @RequestBody UpdateStaffRequest request)

// Two path variables
public ApiResponse<Void> assignStaffRole(
        @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id,
        @PathVariable("roleId") @Min(value = 1, message = "角色ID不能为空") Long roleId)
```

## Return type rules

- All controller methods return `ApiResponse<T>`.
- For operations with meaningful response data: `ApiResponse<XxxDetailResponse>`, `ApiResponse<CreateXxxResponse>`.
- For operations that only confirm success or failure: `ApiResponse<Void>`.
- For paginated lists: `ApiResponse<IPage<XxxDetailResponse>>`.
- For non-paginated lists: `ApiResponse<List<XxxDetailResponse>>`.

Never return raw entity objects, `Map`, or untyped containers.

## Service delegation

Controller methods must only:

1. Declare the route and HTTP method annotation.
2. Declare the method Javadoc.
3. Accept and validate parameters.
4. Delegate to service with a single call.
5. Return the service result directly.

Do not add conditional logic, loops, or business calculations in controllers.

```java
// Correct
@PostMapping
public ApiResponse<CreateStaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
    return staffAdminService.createStaff(request);
}

// Wrong — business logic in controller
@PostMapping
public ApiResponse<CreateStaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
    if (request.getName().length() > 10) {   // business rule belongs in service
        return ApiResponse.fail(...);
    }
    return staffAdminService.createStaff(request);
}
```

## Service method naming alignment

Controller method names and service method names should match. If the controller method is `createStaff`, the corresponding service interface method is also `createStaff`.

```java
// Controller
public ApiResponse<CreateStaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
    return staffAdminService.createStaff(request);
}

// Service interface
ApiResponse<CreateStaffResponse> createStaff(CreateStaffRequest request);
```

## Complete AdminController template

```java
/**
 * 员工管理
 */
@RestController
@RequestMapping("/v1/admin/staffs")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class StaffAdminController {

    private final StaffAdminService staffAdminService;

    /**
     * 创建员工
     * @param request 创建员工请求参数
     * @return 创建结果
     */
    @PostMapping
    public ApiResponse<CreateStaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return staffAdminService.createStaff(request);
    }

    /**
     * 分页查询员工列表
     * @param request 员工分页查询请求参数
     * @return 员工分页数据
     */
    @GetMapping
    public ApiResponse<IPage<StaffDetailResponse>> getStaffPage(@Valid StaffPageQueryRequest request) {
        return staffAdminService.getStaffPage(request);
    }

    /**
     * 查询员工详情
     * @param id 员工ID
     * @return 员工详情
     */
    @GetMapping("/{id}")
    public ApiResponse<StaffDetailResponse> getStaffDetail(
            @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id) {
        return staffAdminService.getStaffDetail(id);
    }

    /**
     * 更新员工资料
     * @param id 员工ID
     * @param request 更新员工请求参数
     * @return 更新后的员工详情
     */
    @PutMapping("/{id}")
    public ApiResponse<StaffDetailResponse> updateStaff(
            @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id,
            @Valid @RequestBody UpdateStaffRequest request) {
        return staffAdminService.updateStaff(id, request);
    }

    /**
     * 禁用员工账号
     * @param id 员工ID
     * @return 禁用结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStaff(
            @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id) {
        return staffAdminService.deleteStaff(id);
    }

    /**
     * 激活员工账号
     * @param id 员工ID
     * @return 激活结果
     */
    @PutMapping("/{id}/enable")
    public ApiResponse<Void> enableStaff(
            @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id) {
        return staffAdminService.enableStaff(id);
    }

    /**
     * 重置员工登录密码
     * @param id 员工ID
     * @return 重置后的密码信息
     */
    @PutMapping("/{id}/password")
    public ApiResponse<StaffResetPasswordResponse> resetStaffPassword(
            @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id) {
        return staffAdminService.resetStaffPassword(id);
    }

    /**
     * 为员工分配角色
     * @param id 员工ID
     * @param roleId 角色ID
     * @return 分配结果
     */
    @PostMapping("/{id}/roles/{roleId}")
    public ApiResponse<Void> assignStaffRole(
            @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id,
            @PathVariable("roleId") @Min(value = 1, message = "角色ID不能为空") Long roleId) {
        return staffAdminService.assignStaffRole(id, roleId);
    }

    /**
     * 移除员工的角色
     * @param id 员工ID
     * @param roleId 角色ID
     * @return 移除结果
     */
    @DeleteMapping("/{id}/roles/{roleId}")
    public ApiResponse<Void> removeStaffRole(
            @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id,
            @PathVariable("roleId") @Min(value = 1, message = "角色ID不能为空") Long roleId) {
        return staffAdminService.removeStaffRole(id, roleId);
    }

    /**
     * 绑定员工所属门店
     * @param id 员工ID
     * @param request 门店绑定请求参数
     * @return 绑定结果
     */
    @PutMapping("/{id}/store")
    public ApiResponse<Void> bindStaffStore(
            @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id,
            @Valid @RequestBody BindStaffStoreRequest request) {
        return staffAdminService.bindStaffStore(id, request);
    }

    /**
     * 解绑员工所属门店
     * @param id 员工ID
     * @return 解绑结果
     */
    @DeleteMapping("/{id}/store")
    public ApiResponse<Void> unbindStaffStore(
            @PathVariable("id") @Min(value = 1, message = "员工ID不能为空") Long id) {
        return staffAdminService.unbindStaffStore(id);
    }
}
```

## BizController template

```java
/**
 * 门店业务接口
 */
@RestController
@RequestMapping("/v1/biz/stores")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class StoreBizController {

    private final StoreBizService storeBizService;

    /**
     * 推荐附近门店
     * @param request 包含用户经纬度和搜索参数
     * @return 按距离排序的附近门店列表
     */
    @GetMapping("/nearby")
    public ApiResponse<List<StoreNearbyResponse>> getNearbyStores(@Valid QueryNearbyStoreRequest request) {
        return storeBizService.getNearbyStores(request);
    }
}
```

## InternalController template

```java
/**
 * 商品内部接口
 */
@RestController
@RequestMapping("/v1/internal/products")
@RequiredArgsConstructor
@Validated
public class ProductInternalController {

    private final ProductInternalService productInternalService;

    /**
     * 根据ID查询商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> getProductById(
            @PathVariable("id") @Min(value = 1, message = "商品ID不能为空") Long id) {
        return productInternalService.getProductById(id);
    }

    /**
     * 批量查询商品详情
     * @param request 商品ID列表请求参数
     * @return 商品详情列表
     */
    @PostMapping("/batch")
    public ApiResponse<List<ProductDetailResponse>> getProductsByIds(
            @Valid @RequestBody BatchQueryProductRequest request) {
        return productInternalService.getProductsByIds(request);
    }
}
```

## Anti-patterns

Do not:

- Use verb suffixes in route paths (`/addStaff`, `/updateStaff`, `/deleteStaff`).
- Use generic method names without entity context (`create`, `update`, `page`) in Admin/Biz controllers — always prefix with the resource name.
- Mix admin and biz operations in one controller class.
- Omit Javadoc on any method.
- Omit `@param` lines for any method parameter.
- Use `@RequestParam` for individual fields when a DTO can group them.
- Return `Map`, raw entities, or `Object` from any method.
- Add business conditions or data transformation inside the controller method body.
- Omit `@Valid` on any DTO parameter.
- Omit `@Min` validation on path variable IDs.
- Write `@PathVariable Long id` without the explicit variable name string (`@PathVariable("id")`).
