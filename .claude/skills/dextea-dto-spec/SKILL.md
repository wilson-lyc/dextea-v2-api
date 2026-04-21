---
name: dextea-dto-spec
description: Use when adding or modifying DTO classes in this repository. Defines the complete specification for all request and response DTOs: naming conventions, class structure, Lombok annotations, validation rules, and field design.
---

# Dextea DTO Specification

Apply this skill whenever creating, renaming, or reviewing any DTO class. All rules are mandatory. Do not invent alternative patterns.

---

## 1. Naming Conventions

### Request DTOs

**Non-page-query pattern:** `{Verb}{Resource}[Qualifier]Request`

- Verb comes first, always.
- Qualifier is optional — use it only when verb + resource alone are ambiguous.

Standard verbs:

| Verb | Usage |
|---|---|
| `Create` | Creating a new resource |
| `Update` | Modifying an existing resource or sub-field |
| `Bind` | Establishing a relation between two resources |
| `Login` | Authentication entry point |

**Page-query pattern:** `{Resource}PageQueryRequest`

- Resource comes first — this is the only request type where resource precedes the action.
- Never use `{Verb}PageQueryRequest` or `PageQuery{Resource}Request`.

Examples:
```
CreateStaffRequest       UpdateStaffRequest       UpdateStaffPasswordRequest
CreateRoleRequest        UpdateRoleRequest         BindRolePermissionRequest
BindStaffStoreRequest    LoginStaffRequest
StaffPageQueryRequest    RolePageQueryRequest      PermissionPageQueryRequest
```

### Response DTOs

| Response type | Pattern | Example |
|---|---|---|
| Create result | `Create{Resource}Response` | `CreateStaffResponse` |
| Single-record query | `{Resource}DetailResponse` | `StaffDetailResponse` |
| Other operations | `{Resource}{Action}Response` | `StaffLoginResponse`, `StaffResetPasswordResponse` |

### Anti-patterns (never use)

- `StaffCreateRequest` — resource before verb in a non-page request
- `StaffUpdatePasswordRequest` — resource before verb in a non-page request
- `StaffLoginRequest` — resource before verb in a non-page request
- `ResetStaffPasswordResponse` — verb before resource in a non-create response
- `PageQueryStaffRequest` — wrong order for page query
- `StaffPageQuery` — missing `Request` suffix

### Placement rules

- All request DTOs → `dto/request/`
- All response DTOs → `dto/response/`
- One class per file; file name must exactly match class name.

---

## 2. Class Structure

### Required Lombok annotations (all DTOs)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XxxRequest { ... }
```

All four annotations are required on every DTO, both request and response.

### Javadoc

Every DTO class must have a one-line Chinese Javadoc comment describing the DTO's purpose.

```java
/**
 * 创建员工请求。
 */
```

---

## 3. Request DTO Validation Rules

Apply `jakarta.validation.constraints.*` annotations to every field. Select the annotation set based on whether the field is **required** or **optional** and on the field's **type**.

### String fields

| Required? | Annotations |
|---|---|
| Required | `@NotBlank(message = "X不能为空")` + `@Size(max = N, message = "X长度不能超过N位")` |
| Optional | `@Size(max = N, message = "X长度不能超过N位")` only |

`@NotBlank` rejects null, empty, and whitespace-only strings; do not use `@NotNull` for String fields.

Size limits by field category:

| Category | Max length |
|---|---|
| General name / username / title | 64 |
| Remark / description | 255 |
| Password / token | 128 |

```java
// Required string
@NotBlank(message = "用户名不能为空")
@Size(max = 64, message = "用户名长度不能超过64位")
private String username;

// Optional string
@Size(max = 255, message = "备注长度不能超过255位")
private String remark;
```

### ID fields (Long, numeric primary/foreign key)

ID fields are Long values that must be ≥ 1 when present. Use `@Min(value = 1)` to enforce both the lower bound and implicitly reject 0.

| Required? | Annotations |
|---|---|
| Required | `@NotNull(message = "X不能为空")` + `@Min(value = 1, message = "X不能为空")` |
| Optional | `@Min(value = 1, message = "X不能小于1")` only |

```java
// Required ID
@NotNull(message = "权限ID不能为空")
@Min(value = 1, message = "权限ID不能为空")
private Long permissionId;

// Optional ID filter
@Min(value = 1, message = "门店ID不能小于1")
private Long storeId;
```

### Enum-backed Integer fields

Enum values are transmitted as `Integer`. Use `@EnumValue` from `cn.dextea.common.validation.annotation` to verify the value is a legal enum member.

| Required? | Annotations |
|---|---|
| Required | `@NotNull(message = "X不能为空")` + `@EnumValue(enumClass = X.class, fieldName = "X")` |
| Optional | `@EnumValue(enumClass = X.class, fieldName = "X")` only |

```java
// Required enum
@NotNull(message = "员工类型不能为空")
@EnumValue(enumClass = StaffType.class, fieldName = "员工类型")
private Integer userType;

// Optional enum filter
@EnumValue(enumClass = StaffStatus.class, fieldName = "员工状态")
private Integer status;
```

`fieldName` must be the Chinese display name of the field; it is used in the validation error message.

### Numeric range fields (non-ID Long/Integer)

Use `@Min` and/or `@Max` as appropriate. Add `@NotNull` only when the field is required.

```java
// Required with range
@NotNull(message = "数量不能为空")
@Min(value = 1, message = "数量不能小于1")
@Max(value = 999, message = "数量不能超过999")
private Integer quantity;
```

### Page query fields (special case)

Every `{Resource}PageQueryRequest` must include exactly these two pagination fields with defaults:

```java
@NotNull(message = "页码不能为空")
@Min(value = 1, message = "当前页码不能小于1")
@Builder.Default
private Long current = 1L;

@NotNull(message = "分页大小不能为空")
@Min(value = 1, message = "每页条数不能小于1")
@Max(value = 100, message = "每页条数不能大于100")
@Builder.Default
private Long size = 10L;
```

- `current` defaults to `1L`, type is `Long`.
- `size` defaults to `10L`, type is `Long`, max is `100`.
- `@Builder.Default` is required so the Builder API respects the default.
- All other filter fields in the same class are **optional** — apply type-appropriate constraints but omit `@NotNull`/`@NotBlank`.

---

## 4. Response DTO Design Rules

Response DTOs carry no validation annotations.

### Field types

| Data | Java type |
|---|---|
| Date and time | `LocalDateTime` |
| Enum value | `Integer` (raw value, not the enum type) |
| Nested DTO | Allowed — use a typed response DTO, e.g. `StaffDetailResponse` |

### What to include

- Return all fields the caller needs; do not omit audit fields (`createTime`, `updateTime`) from detail responses.
- Create responses (`Create{Resource}Response`) may include an `initialPassword` field when the system generates a temporary password.
- Avoid returning sensitive fields (plaintext passwords, internal tokens) unless the endpoint's sole purpose is to deliver them.

### Nesting example

```java
/**
 * 员工登录响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffLoginResponse {
    private String tokenName;
    private String tokenValue;
    private StaffDetailResponse staff;
}
```

---

## 5. Complete Examples

### Create request

```java
/**
 * 创建员工请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStaffRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名长度不能超过64位")
    private String username;

    @NotBlank(message = "员工姓名不能为空")
    @Size(max = 64, message = "员工姓名长度不能超过64位")
    private String realName;

    @NotNull(message = "员工类型不能为空")
    @EnumValue(enumClass = StaffType.class, fieldName = "员工类型")
    private Integer userType;
}
```

### Page query request

```java
/**
 * 员工分页查询请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffPageQueryRequest {
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "当前页码不能小于1")
    @Builder.Default
    private Long current = 1L;

    @NotNull(message = "分页大小不能为空")
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    @Builder.Default
    private Long size = 10L;

    @Size(max = 64, message = "用户名长度不能超过64位")
    private String username;

    @EnumValue(enumClass = StaffStatus.class, fieldName = "员工状态")
    private Integer status;
}
```

### Detail response

```java
/**
 * 员工详情响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDetailResponse {
    private Long id;
    private String username;
    private String realName;
    private Integer userType;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```
