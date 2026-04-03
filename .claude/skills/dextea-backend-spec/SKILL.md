---
name: dextea-backend-spec
description: Use when adding or modifying backend code in this repository, especially Spring Boot microservice controllers, services, DTOs, converters, REST APIs, validation, and business error enums. This skill is self-contained and defines Dextea's default backend coding standard.
---

# Dextea Backend Spec

Use this skill for backend development in this repository. Treat this document as the default standard. Unless the user explicitly asks you to match an unusual existing implementation, do not explore unrelated modules first. Write code directly from the rules and templates below.

## Core principles

- Controller only handles HTTP protocol concerns.
- Service layer owns business logic.
- DTO defines API contract.
- Converter builds outward-facing response objects.
- Business errors belong to the current microservice.
- System errors belong to shared global exception handling.
- API design should be RESTful, predictable, and resource-oriented.

## Standard project structure

For a normal backend feature, prefer this structure:

- `controller`
- `service`
- `service/impl`
- `dto/request`
- `dto/response`
- `converter`
- `enums`
- `mapper`
- `entity`

Do not return entity objects directly from APIs.

## SQL DDL conventions

Each microservice must keep entity-aligned DDL files under:

- `src/main/resources/sql/`

Rules:

- Store one DDL SQL file per entity.
- The SQL file must describe the table structure corresponding to that entity.
- When creating a new entity, create the matching DDL SQL file in the same change.
- When updating entity structure, update the matching DDL SQL file in the same change.
- Do not leave entity and DDL definitions out of sync.

Recommended naming:

- entity `StaffEntity` -> `src/main/resources/sql/staff.sql`
- entity `RoleEntity` -> `src/main/resources/sql/role.sql`
- entity `StaffRoleRelEntity` -> `src/main/resources/sql/staff_role_rel.sql`

DDL expectations:

- one entity, one SQL file
- include full table DDL, not only fragments
- column names, types, nullability, defaults, indexes, and primary key definitions should match the entity and business constraints
- if the entity introduces unique constraints or relation constraints, reflect them in DDL

## Naming conventions

### Controllers

Split controllers by caller type:

- Admin APIs: `XxxAdminController`
- Business-facing APIs: `XxxBizController`
- Internal microservice APIs: `XxxInternalController`
- Authentication APIs: `XxxAuthController`

Choose controller type by caller, not by developer preference.

### Services

- Admin business service: `XxxAdminService`, `XxxAdminServiceImpl`
- Business-facing service: `XxxBizService`, `XxxBizServiceImpl`
- Auth service: `XxxAuthService`, `XxxAuthServiceImpl`

### DTOs

- Create request: `CreateXxxRequest`
- Update request: `UpdateXxxRequest`
- Page query request: `XxxPageQueryRequest`
- Detail response: `XxxDetailResponse`
- Create response: `CreateXxxResponse`

### Converter

- Converter class: `XxxConverter`
- Method naming: `toXxxResponse`, `toXxxDetailResponse`, `toCreateXxxResponse`

### Business errors

- Module business error enum: `XxxErrorCode`

## Route conventions

Use versioned RESTful routes with scope prefixes:

- Admin: `/v1/admin/...`
- Biz: `/v1/biz/...`
- Internal: `/v1/internal/...`

Use plural resource nouns by default:

- `/v1/admin/staffs`
- `/v1/admin/roles`
- `/v1/biz/products`

Prefer these route patterns:

- Create resource: `POST /v1/admin/resources`
- Page query: `GET /v1/admin/resources`
- Detail query: `GET /v1/admin/resources/{id}`
- Update resource: `PUT /v1/admin/resources/{id}`
- Delete resource: `DELETE /v1/admin/resources/{id}`
- Bind relation: `POST /v1/admin/resources/{id}/relations`
- Unbind relation: `DELETE /v1/admin/resources/{id}/relations/{relationId}`
- Explicit state change: `PUT /v1/admin/resources/{id}/enable`

Avoid these by default:

- `/add`
- `/update`
- `/delete`
- verb-heavy paths when resource paths can express the same meaning
- mixing singular and plural naming in one module

## Controller rules

Controllers must:

- return `ApiResponse<T>`
- define only route, parameter, and validation concerns
- delegate business logic to service
- use `@Validated` at class level when validation is needed
- use `@Valid` on DTO parameters
- validate path params such as IDs with annotations like `@Min`

Controller methods must include Javadoc in this exact format:

```java
/**
 * 接口名
 * @param 请求参数
 * @return 响应值
 */
```

If a method has multiple parameters, keep one `@param` line per parameter.

### Controller template

```java
@RestController
@RequestMapping("/v1/admin/resources")
@RequiredArgsConstructor
@Validated
public class ResourceAdminController {

    private final ResourceAdminService resourceAdminService;

    /**
     * 创建资源
     * @param request 创建资源请求参数
     * @return 创建结果
     */
    @PostMapping
    public ApiResponse<CreateResourceResponse> create(@Valid @RequestBody CreateResourceRequest request) {
        return resourceAdminService.create(request);
    }

    /**
     * 分页查询资源列表
     * @param request 资源分页查询请求参数
     * @return 资源分页数据
     */
    @GetMapping
    public ApiResponse<IPage<ResourceDetailResponse>> page(@Valid ResourcePageQueryRequest request) {
        return resourceAdminService.page(request);
    }

    /**
     * 查询资源详情
     * @param id 资源ID
     * @return 资源详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ResourceDetailResponse> detail(
            @PathVariable("id") @Min(value = 1, message = "资源ID不能为空") Long id) {
        return resourceAdminService.detail(id);
    }

    /**
     * 更新资源
     * @param id 资源ID
     * @param request 更新资源请求参数
     * @return 更新后的资源详情
     */
    @PutMapping("/{id}")
    public ApiResponse<ResourceDetailResponse> update(
            @PathVariable("id") @Min(value = 1, message = "资源ID不能为空") Long id,
            @Valid @RequestBody UpdateResourceRequest request) {
        return resourceAdminService.update(id, request);
    }
}
```

## DTO rules

Every API must define dedicated request and response DTOs.

Request DTO rules:

- put request DTOs in `dto/request`
- put all input constraints in DTO fields
- use Jakarta Validation annotations
- use Chinese validation messages
- use explicit field types and explicit bounds
- define DTO even for query APIs when the input is not trivial

Response DTO rules:

- put response DTOs in `dto/response`
- expose only API contract fields
- do not expose entity or persistence-specific details unless required by the API contract

### Validation rules

Use these validation annotations as appropriate:

- `@NotNull`
- `@NotBlank`
- `@Size`
- `@Min`
- `@Max`

Any integer field that represents an enum value must use:

```java
@EnumValue(enumClass = XxxEnum.class, fieldName = "字段中文名")
```

The `fieldName` should be a Chinese business name for friendly validation messages.

### Request DTO template

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateResourceRequest {

    @NotBlank(message = "名称不能为空")
    @Size(max = 64, message = "名称长度不能超过64位")
    private String name;

    @NotNull(message = "状态不能为空")
    @EnumValue(enumClass = ResourceStatus.class, fieldName = "资源状态")
    private Integer status;
}
```

### Page query DTO template

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePageQueryRequest {

    @Min(value = 1, message = "当前页码不能小于1")
    @Builder.Default
    private Long current = 1L;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    @Builder.Default
    private Long size = 10L;

    @Size(max = 64, message = "名称长度不能超过64位")
    private String name;

    @EnumValue(enumClass = ResourceStatus.class, fieldName = "资源状态")
    private Integer status;
}
```

### Response DTO template

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDetailResponse {
    private Long id;
    private String name;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

## Converter rules

Converter responsibility is single-purpose:

- build response DTOs
- build outward transport models
- centralize object mapping

Converter must not:

- query database
- decide business validity
- hold transaction logic
- perform route or request validation

### Converter template

```java
@Component
public class ResourceConverter {

    public CreateResourceResponse toCreateResponse(ResourceEntity entity) {
        return CreateResourceResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .build();
    }

    public ResourceDetailResponse toDetailResponse(ResourceEntity entity) {
        return ResourceDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
```

## Service rules

Service interfaces define business capabilities. Service implementations contain business logic.

Service implementation should normally do work in this order:

1. Normalize request data when needed, such as trimming strings.
2. Check business preconditions.
3. Query current data.
4. Return business error when target data does not exist or state is invalid.
5. Perform writes.
6. Convert final data through converter.
7. Return `ApiResponse.success(...)`.

Use `@Transactional(rollbackFor = Exception.class)` for multi-step writes or important state changes.

### Service implementation template

```java
@Service
@RequiredArgsConstructor
public class ResourceAdminServiceImpl implements ResourceAdminService {

    private final ResourceMapper resourceMapper;
    private final ResourceConverter resourceConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateResourceResponse> create(CreateResourceRequest request) {
        String name = request.getName().trim();

        if (existsByName(name, null)) {
            return fail(ResourceErrorCode.RESOURCE_NAME_ALREADY_EXISTS);
        }

        ResourceEntity entity = ResourceEntity.builder()
                .name(name)
                .status(request.getStatus())
                .build();

        if (resourceMapper.insert(entity) != 1) {
            return fail(ResourceErrorCode.CREATE_FAILED);
        }

        return ApiResponse.success(resourceConverter.toCreateResponse(entity));
    }

    @Override
    public ApiResponse<ResourceDetailResponse> detail(Long id) {
        ResourceEntity entity = resourceMapper.selectById(id);
        if (entity == null) {
            return fail(ResourceErrorCode.RESOURCE_NOT_FOUND);
        }
        return ApiResponse.success(resourceConverter.toDetailResponse(entity));
    }
}
```

### Service design rules

- Keep controller thin and service readable.
- Prefer simple sequential business checks over deep nesting.
- If a relation is created, validate both sides exist first.
- If a resource is updated, check it exists first.
- If uniqueness matters, validate before write.
- If physical delete is not appropriate, use status-based disable or soft delete.

## Business error rules

Each microservice should define its own business error enum for domain failures.

Business error examples:

- target resource does not exist
- duplicate name
- duplicate relation binding
- relation does not exist
- invalid state transition
- operation not allowed under current business rules

### Business error enum template

```java
@Getter
@RequiredArgsConstructor
public enum ResourceErrorCode {
    RESOURCE_NOT_FOUND(10001, "资源不存在"),
    RESOURCE_NAME_ALREADY_EXISTS(10002, "资源名称已存在"),
    CREATE_FAILED(10003, "资源创建失败"),
    UPDATE_FAILED(10004, "资源更新失败"),
    DELETE_FAILED(10005, "资源删除失败");

    private final Integer code;
    private final String msg;
}
```

### Error ownership boundary

- Business rule errors: current microservice enum
- Parameter validation errors: validation framework plus global exception handler
- Database access errors: shared global exception handler
- Duplicate key and framework exceptions: shared global exception handler

Do not add repetitive per-controller `try/catch` for common framework exceptions.

## Default generation checklist

When implementing a new backend API, generate or update all relevant pieces:

1. correct controller type
2. service interface
3. service implementation
4. request DTO
5. response DTO
6. converter
7. business error enum
8. route and method annotations
9. validation annotations
10. controller Javadoc
11. entity-aligned DDL SQL under `src/main/resources/sql/` when entity is added or changed

## Decision rules

- If the field has format, range, or enum constraints, validate it in DTO.
- If the rule depends on database state, validate it in service.
- If the API returns structured data, convert it in converter.
- If the failure is business-semantic, use module error enum.
- If the failure is infrastructure-semantic, rely on global handling.
- If the caller is admin, use `AdminController`.
- If the caller is end-user business flow, use `BizController`.
- If the caller is another microservice, use `InternalController`.
- If an entity is created or structurally changed, create or update its matching SQL DDL file in the same change.

## Anti-patterns

Do not:

- return entity objects directly from controller
- put business logic in controller
- skip request/response DTOs for convenience
- omit `@EnumValue` for enum-like integer inputs
- mix admin and biz APIs in one controller
- build large response objects repeatedly inside service methods instead of converter reuse
- handle database/framework exceptions as if they were normal business branches
- write routes using `add`, `update`, `delete` suffixes when standard RESTful paths are sufficient
- add or change an entity without updating its matching DDL SQL file

## Final instruction

By default, write new backend code strictly according to this document. Only inspect existing implementation details when the user explicitly requires compatibility with a specific module or endpoint.
