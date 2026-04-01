---
name: create-interface
description: To standardize the creation of back-end interfaces
---

# Create a Restful Interface

You are helping create a RESTful API interface in this Spring Boot microservice project.
This skill file is self-contained: it defines explicit conventions, file templates, naming rules, and code patterns an agent must follow to create a new RESTful resource interface. Do NOT attempt to infer conventions from other files or modules — use only the rules, templates, and examples provided below.

Purpose: an agent that reads only this file should be able to generate all required Java sources for a new resource (DTOs, entity, mapper, converter, service, impl, controller, and error codes) that conform to the project's conventions.

---

## Step 1 — Gather Requirements

Ask the user (or infer from context):

1. **Module** — which service module? (e.g., `staff-service`, `store-service`)
2. **Resource name** — e.g., `staff`, `role`, `store`
3. **HTTP method** — GET / POST / PUT / DELETE (or multiple)
4. **URL path** — e.g., `/v1/admin/staffs/{id}`
5. **Request fields** — names, types, validation rules
6. **Response fields** — names, types
7. **Error scenarios** — what can go wrong? (drives error code definitions)
8. **Error code prefix** — e.g., staff=`10xxx`, role=`11xxx`; pick the next unused range for a new resource

---

## Step 2 — Files to Create / Modify

For a new interface, identify which of these need to be created or updated:

| Layer | File | Action |
|---|---|---|
| Error codes | `enums/XxxErrorCode.java` | Create if new resource; append new codes if existing |
| Request DTO | `dto/request/XxxRequest.java` | Create |
| Response DTO | `dto/response/XxxResponse.java` | Create |
| Converter | `converter/XxxConverter.java` | Create or add method |
| Service interface | `service/XxxService.java` | Create or add method |
| Service impl | `service/impl/XxxServiceImpl.java` | Create or add method |
| Mapper | `mapper/XxxMapper.java` | Create if new entity |
| Entity | `entity/XxxEntity.java` | Create if new entity |
| Controller | `controller/XxxController.java` | Create or add method |

---

## Step 3 — Code Patterns (copy exactly)

### 3.1 Error Code Enum

```java
@Getter
@RequiredArgsConstructor
public enum XxxErrorCode {
    RECORD_NOT_FOUND(10001, "记录不存在"),
    CREATE_FAILED(10002, "创建失败"),
    UPDATE_FAILED(10003, "更新失败"),
    NAME_ALREADY_EXISTS(10004, "名称已存在");

    private final Integer code;
    private final String msg;
}
```

Rules:
- `@Getter` + `@RequiredArgsConstructor` (Lombok), no manual getters
- Code format: `<module-prefix><3-digit sequence>` (e.g., staff=10xxx, role=11xxx, store=20xxx)
- One enum file per resource type; add new cases to the existing file if it exists
- Message in Chinese

---

### 3.2 Request DTO

**CREATE:**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateXxxRequest {
    @NotBlank(message = "名称不能为空")
    @Size(max = 64, message = "名称长度不能超过64位")
    private String name;

    @NotNull(message = "类型不能为空")
    @EnumValue(enumClass = XxxType.class, fieldName = "类型")
    private Integer type;
}
```

**UPDATE:**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateXxxRequest {
    // Same fields as Create, but all fields required for full replacement
    // Add @EnumValue(enumClass = XxxStatus.class) for status field if updatable
}
```

**PAGE QUERY:**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XxxPageQueryRequest {
    @Min(value = 1, message = "当前页码不能小于1")
    @Builder.Default
    private Long current = 1L;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    @Builder.Default
    private Long size = 10L;

    // Optional filter fields — no @NotNull unless truly mandatory for query
    @Size(max = 64, message = "名称长度不能超过64位")
    private String name;
}
```

Rules:
- Always use all four Lombok annotations: `@Data @Builder @NoArgsConstructor @AllArgsConstructor`
- Use `@Builder.Default` for pagination defaults (`current=1L`, `size=10L`)
- Use `@NotBlank` for required Strings, `@NotNull` for required Objects/Integers
- Use `@Size(max=...)` from `jakarta.validation.constraints` for length limits
- Use `@EnumValue(enumClass=..., fieldName=...)` for integer enum fields
- Use `@PhoneNumber(fieldName=...)` for phone fields
- Use `@Min` / `@Max` for numeric ranges
- Import from `cn.dextea.common.validation.annotation.*` for custom validators

---

### 3.3 Response DTO

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XxxDetailResponse {
    private Long id;
    private String name;
    private Integer type;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

Rules:
- Flat structure — no nested objects except when unavoidable (e.g., login response with token + user)
- Always include `createTime` and `updateTime` for main entities
- No validation annotations on response DTOs
- Use `Long` for IDs, `Integer` for enum-backed integers, `LocalDateTime` for timestamps

---

### 3.4 Converter

```java
@Component
public class XxxConverter {

    public XxxDetailResponse toXxxDetailResponse(XxxEntity entity) {
        return XxxDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    public CreateXxxResponse toCreateXxxResponse(XxxEntity entity) {
        return CreateXxxResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createTime(entity.getCreateTime())
                .build();
    }
}
```

Rules:
- `@Component`, injected via constructor in service
- Method naming: `to<TargetType>` (e.g., `toXxxDetailResponse`)
- Use Builder exclusively — never set fields directly
- Can accept extra parameters beyond entity when needed (e.g., `toCreateResponse(entity, generatedPassword)`)

---

### 3.5 Entity

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("xxx")
public class XxxEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer type;

    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
```

Rules:
- Annotations order: `@Data @Builder @AllArgsConstructor @NoArgsConstructor @TableName`
- `@TableId(value = "id", type = IdType.AUTO)` for primary key
- `@TableField("column_name")` when field name ≠ column name (camelCase ≠ snake_case)
- `@TableField(select = false)` for sensitive fields excluded from SELECT (e.g., password)
- Always include `createTime` and `updateTime`

---

### 3.6 Mapper

```java
@Mapper
public interface XxxMapper extends BaseMapper<XxxEntity> {
    // Add custom query methods only if BaseMapper cannot satisfy the need
}
```

Rules:
- Extend `BaseMapper<XxxEntity>` — provides all standard CRUD
- Add custom methods only for complex JOINs or subqueries not possible with `LambdaQueryWrapper`

---

### 3.7 Service Interface

```java
public interface XxxAdminService {
    ApiResponse<CreateXxxResponse> createXxx(CreateXxxRequest request);
    ApiResponse<IPage<XxxDetailResponse>> getXxxPage(XxxPageQueryRequest request);
    ApiResponse<XxxDetailResponse> getXxxDetail(Long id);
    ApiResponse<XxxDetailResponse> updateXxx(Long id, UpdateXxxRequest request);
    ApiResponse<Void> deleteXxx(Long id);
}
```

Rules:
- Return type is always `ApiResponse<T>`, never raw T or void
- Use `IPage<XxxDetailResponse>` for paginated results
- Use `ApiResponse<Void>` for operations with no return data (delete, enable, disable)

---

### 3.8 Service Implementation

```java
@Service
@RequiredArgsConstructor
public class XxxAdminServiceImpl implements XxxAdminService {
    private final XxxMapper xxxMapper;
    private final XxxConverter xxxConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateXxxResponse> createXxx(CreateXxxRequest request) {
        String name = request.getName().trim();

        // 1. Duplicate check
        if (existsByName(name, null)) {
            return fail(XxxErrorCode.NAME_ALREADY_EXISTS);
        }

        // 2. Build entity
        XxxEntity entity = XxxEntity.builder()
                .name(name)
                .type(request.getType())
                .status(XxxStatus.ACTIVE.getValue())
                .build();

        // 3. Persist
        if (xxxMapper.insert(entity) != 1) {
            return fail(XxxErrorCode.CREATE_FAILED);
        }

        return ApiResponse.success(xxxConverter.toCreateXxxResponse(entity));
    }

    @Override
    public ApiResponse<IPage<XxxDetailResponse>> getXxxPage(XxxPageQueryRequest request) {
        LambdaQueryWrapper<XxxEntity> queryWrapper = new LambdaQueryWrapper<XxxEntity>()
                .like(hasText(request.getName()), XxxEntity::getName, trim(request.getName()))
                .orderByDesc(XxxEntity::getId);

        IPage<XxxEntity> entityPage = xxxMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        IPage<XxxDetailResponse> responsePage = entityPage.convert(xxxConverter::toXxxDetailResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<XxxDetailResponse> getXxxDetail(Long id) {
        XxxEntity entity = xxxMapper.selectById(id);
        if (entity == null) {
            return fail(XxxErrorCode.RECORD_NOT_FOUND);
        }
        return ApiResponse.success(xxxConverter.toXxxDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<XxxDetailResponse> updateXxx(Long id, UpdateXxxRequest request) {
        XxxEntity entity = xxxMapper.selectById(id);
        if (entity == null) {
            return fail(XxxErrorCode.RECORD_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (existsByName(name, id)) {
            return fail(XxxErrorCode.NAME_ALREADY_EXISTS);
        }

        entity.setName(name);
        entity.setType(request.getType());

        if (xxxMapper.updateById(entity) != 1) {
            return fail(XxxErrorCode.UPDATE_FAILED);
        }

        return ApiResponse.success(xxxConverter.toXxxDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteXxx(Long id) {
        XxxEntity entity = xxxMapper.selectById(id);
        if (entity == null) {
            return fail(XxxErrorCode.RECORD_NOT_FOUND);
        }
        xxxMapper.deleteById(id);
        return ApiResponse.success();
    }

    // ---- Helpers ----

    private boolean existsByName(String name, Long excludeId) {
        LambdaQueryWrapper<XxxEntity> queryWrapper = new LambdaQueryWrapper<XxxEntity>()
                .eq(XxxEntity::getName, name)
                .ne(excludeId != null, XxxEntity::getId, excludeId);
        return xxxMapper.exists(queryWrapper);
    }

    private <T> ApiResponse<T> fail(XxxErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
```

Rules:
- `@Service @RequiredArgsConstructor` — no `@Autowired`
- `@Transactional(rollbackFor = Exception.class)` on write operations (POST/PUT/DELETE)
- Always trim String inputs from request before using them
- Check existence before update/delete; return `fail(XxxErrorCode.RECORD_NOT_FOUND)` if missing
- Check uniqueness before create/update; return specific duplicate error code
- Private `fail()` helper method to avoid repeating `ApiResponse.fail(errorCode.getCode(), errorCode.getMsg())`
- Private `hasText()` and `trim()` helpers for conditional query building

---

### 3.9 Controller

```java
@RestController
@RequestMapping("/v1/admin/xxxs")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class XxxAdminController {
    private final XxxAdminService xxxAdminService;

    @PostMapping
    public ApiResponse<CreateXxxResponse> createXxx(
            @Valid @RequestBody CreateXxxRequest request) {
        return xxxAdminService.createXxx(request);
    }

    @GetMapping
    public ApiResponse<IPage<XxxDetailResponse>> getXxxPage(
            @Valid XxxPageQueryRequest request) {
        return xxxAdminService.getXxxPage(request);
    }

    @GetMapping("/{id}")
    public ApiResponse<XxxDetailResponse> getXxxDetail(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return xxxAdminService.getXxxDetail(id);
    }

    @PutMapping("/{id}")
    public ApiResponse<XxxDetailResponse> updateXxx(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id,
            @Valid @RequestBody UpdateXxxRequest request) {
        return xxxAdminService.updateXxx(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteXxx(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return xxxAdminService.deleteXxx(id);
    }
}
```

Rules:
- Class annotations: `@RestController @RequestMapping @RequiredArgsConstructor @SaCheckLogin @Validated`
- URL pattern: `/v1/{scope}/{resource}s` (e.g., `/v1/admin/staffs`, `/v1/admin/roles`)
- `@Valid @RequestBody` for POST/PUT body params
- `@Valid` without `@RequestBody` for GET query params (Spring binds from query string)
- `@PathVariable` with `@Min(value=1)` for ID path variables
- Controller methods are thin — no business logic, just delegate to service
- Return type mirrors the service interface exactly

Controller method Javadoc requirements:

- Every controller method MUST have a Javadoc-style comment immediately above the method declaration. The comment must follow this exact structure:

/**
 * Interface name (description in Chinese)
 * @param <parameterName> <short description in Chinese>
 * @return <description of the return value, typically ApiResponse<...>>
 */

- Key rules for comments:
    - The first line must be the interface name, written in concise Chinese (e.g. "创建门店", "获取员工详情").
    - Every method parameter must have a matching `@param` line. Parameter names must match the method signature. For request bodies use `request` and indicate the DTO type (e.g. `@param request CreateXxxRequest 创建请求体`).
    - The `@return` line must state the `ApiResponse` generic type (e.g. `@return ApiResponse<CreateXxxResponse>` or `@return ApiResponse<Void>`).
    - Comment content MUST be written in Chinese, concise, and should not include implementation details or usage examples.

Example (note the comment text is Chinese as required):

/**
 * 创建Xxx
 * @param request
 * @return Response
 */
@PostMapping
public ApiResponse<CreateXxxResponse> createXxx(
                @Valid @RequestBody CreateXxxRequest request) {
        return xxxAdminService.createXxx(request);
}

---

## Step 4 — Checklist Before Writing Code

- [ ] Error code enum created/updated with all possible failure cases
- [ ] Request DTO has proper Lombok annotations + validation annotations
- [ ] Response DTO is flat (no unnecessary nesting)
- [ ] Converter has `to<ResponseType>` methods for each response
- [ ] Service interface declares method with correct `ApiResponse<T>` return type
- [ ] Service impl trims all String inputs, checks existence, checks uniqueness
- [ ] Service impl has `@Transactional` on write methods
- [ ] Controller uses `@Valid` on request params, `@Min(1)` on path ID variables
- [ ] Controller is thin — zero business logic

---

## Step 5 — Common Validation Annotations Reference

| Annotation | Package | Use case |
|---|---|---|
| `@NotBlank` | `jakarta.validation.constraints` | Required String field |
| `@NotNull` | `jakarta.validation.constraints` | Required non-String field |
| `@Size(max=N)` | `jakarta.validation.constraints` | Max string length |
| `@Min(value=N)` | `jakarta.validation.constraints` | Min numeric value |
| `@Max(value=N)` | `jakarta.validation.constraints` | Max numeric value |
| `@EnumValue(enumClass=..., fieldName=...)` | `cn.dextea.common.validation.annotation` | Integer backed enum field |
| `@PhoneNumber(fieldName=...)` | `cn.dextea.common.validation.annotation` | Chinese phone number |
| `@Range(min=..., max=...)` | `org.hibernate.validator.constraints` | Integer range |
| `@DecimalMin` / `@DecimalMax` | `jakarta.validation.constraints` | Decimal range (geo coords) |

Common max lengths from `ValidationConstant`:
- `NAME_MAX_LENGTH = 64`
- `TITLE_MAX_LENGTH = 128`
- `DESCRIPTION_MAX_LENGTH = 255`
- `ADDRESS_MAX_LENGTH = 255`

---

## Step 6 — Output

Generate all necessary files in order:
1. Error code enum (append or create)
2. Request DTO(s)
3. Response DTO(s)
4. Entity (if new)
5. Mapper (if new)
6. Converter (add methods or create)
7. Service interface (add methods or create)
8. Service impl (add methods or create)
9. Controller (add methods or create)

After generating, confirm with the user which files were created/modified.
