---
name: dextea-response-spec
description: Use when writing or reviewing any API return in this repository. Defines the complete specification for ApiResponse structure, error code design, success/fail return patterns, controller return type declarations, and cross-service Feign response consumption.
---

# Dextea Response Specification

Apply this skill whenever writing controller return types, service return statements, error code enums, or consuming Feign responses. All rules are mandatory. Do not invent alternative patterns.

---

## 1. Unified Response Wrapper `ApiResponse<T>`

Every API must return `ApiResponse<T>`. The structure is fixed:

```json
{
  "code": 0,
  "msg": "操作成功",
  "data": { ... }
}
```

| Field | Type | Rule |
|-------|------|------|
| `code` | `Integer` | `0` = success; non-zero = failure |
| `msg` | `String` | Human-readable result message |
| `data` | `T` (generic) | Business payload on success; `null` on failure |

Never invent a custom wrapper. Never return raw objects, maps, or entity classes directly from a controller.

---

## 2. Error Code Two-Layer Design

### Layer 1 — Global system codes (`ResponseCode` enum, lives in `common` module)

Represents the overall request result type, not the specific business reason:

```java
SUCCESS(0, "操作成功")
FAIL(400, "操作失败")
UNAUTHORIZED(401, "未登录或登录已过期")
FORBIDDEN(403, "无权访问")
NOT_FOUND(404, "资源不存在")
SERVER_ERROR(500, "服务器内部错误")
SERVICE_UNAVAILABLE(503, "服务暂不可用")
```

Do not define these codes again in any microservice. Do not reuse these numeric values in business error enums.

### Layer 2 — Business error codes (`XxxErrorCode` enum, one per microservice module)

Captures the specific business failure reason. Starts at `10001`:

```java
@Getter
@RequiredArgsConstructor
public enum StaffErrorCode {
    USERNAME_ALREADY_EXISTS(10001, "用户名已存在"),
    CREATE_FAILED(10002, "员工创建失败"),
    STAFF_NOT_FOUND(10003, "员工不存在"),
    UPDATE_FAILED(10004, "员工更新失败");

    private final Integer code;
    private final String msg;
}
```

Rules:
- Fields are exactly `Integer code` and `String msg` — no other field names.
- Use `@Getter` + `@RequiredArgsConstructor` — no `@AllArgsConstructor`.
- Code numbering starts at `10001` and increments sequentially within the enum.
- One enum per microservice module (e.g. `StaffErrorCode`, `RoleErrorCode`, `PermissionErrorCode`).
- A class-level Javadoc comment in Chinese is required, describing the module scope.

---

## 3. Four Standard Return Patterns

### 3.1 Success with data

```java
return ApiResponse.success(converter.toXxxResponse(entity));
```

The data argument must always be a response DTO produced by the module's converter. Never pass an entity directly.

### 3.2 Success without data

```java
return ApiResponse.success();
```

Use for operation-type APIs (delete, bind, state change) where no payload needs to be returned. The controller return type must be `ApiResponse<Void>`.

### 3.3 Business failure

```java
return fail(StaffErrorCode.STAFF_NOT_FOUND);
```

Always go through the private `fail()` helper (see Section 4). Never call `ApiResponse.fail(code, msg)` directly in business logic.

### 3.4 System-level failure

Do not handle in service or controller. Let the global exception handler produce `unauthorized`, `forbidden`, or `serverError` responses.

---

## 4. Private `fail()` Helper in ServiceImpl

Every `ServiceImpl` must define exactly one private generic `fail()` method. It is the only permitted way to construct a failure response inside business logic:

```java
private <T> ApiResponse<T> fail(StaffErrorCode errorCode) {
    return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
}
```

Rules:
- The method is `private`, generic `<T>`, returns `ApiResponse<T>`.
- Parameter is the module's error code enum, not `Integer` or `String`.
- Call site is always `return fail(XxxErrorCode.SOME_CASE)` — one line, no inline code or message strings.
- Do not define multiple overloads or variants.

---

## 5. Controller Return Type Declarations

| API intent | Return type |
|------------|-------------|
| Create resource | `ApiResponse<CreateXxxResponse>` |
| Query single record | `ApiResponse<XxxDetailResponse>` |
| Query page | `ApiResponse<IPage<XxxDetailResponse>>` |
| Delete / bind / unbind / state change | `ApiResponse<Void>` |
| Auth (login) | `ApiResponse<XxxLoginResponse>` |
| Special operation with result payload | `ApiResponse<XxxXxxResponse>` |

The type parameter `T` must always be a named response DTO or `Void`. Never use `ApiResponse<?>`, `ApiResponse<Object>`, or raw `ApiResponse`.

---

## 6. Response DTO as Data Payload

The `data` field in `ApiResponse` must always be one of:

- A single response DTO: `XxxDetailResponse`, `CreateXxxResponse`, `XxxLoginResponse`, etc.
- A paginated result: `IPage<XxxDetailResponse>`
- `null` (when `ApiResponse.success()` is called with no argument, i.e. `Void` APIs)

Never put an entity, a `Map`, a plain `List` (without `IPage` wrapper), or a primitive directly in `data`.

Response DTOs may nest other response DTOs:

```java
public class StaffLoginResponse {
    private String tokenName;
    private String tokenValue;
    private StaffDetailResponse staff;  // nested response DTO — allowed
}
```

---

## 7. Consuming Cross-Service Feign Responses

When calling another microservice via Feign and consuming its `ApiResponse`, always perform a full guard before accessing `data`:

```java
ApiResponse<StoreValidityResponse> response = storeInternalFeign.checkStoreValidity(storeId);
return response != null
        && response.getCode() != null
        && response.getCode() == 0
        && response.getData() != null
        && Boolean.TRUE.equals(response.getData().getValid());
```

Required checks in order:
1. `response != null` — Feign may return null on network failure.
2. `response.getCode() != null` — guard against a malformed response.
3. `response.getCode() == 0` — only `0` means success (`ResponseCode.SUCCESS`).
4. `response.getData() != null` — success does not guarantee a non-null payload.
5. Access the specific field on `getData()` only after all four guards pass.

Never call `.getData()` directly without the full guard chain.

---

## 8. Error Ownership Boundary

| Error type | Owner |
|------------|-------|
| Parameter validation failure (`@NotBlank`, `@Min`, etc.) | Global exception handler — do not handle in service |
| Business rule failure (not found, duplicate, invalid state) | `XxxErrorCode` enum + service `fail()` |
| Database / framework exception | Global exception handler — do not `try/catch` in service |
| Unauthenticated / forbidden | Global exception handler (Sa-Token interceptor) |
| Cross-service call failure | Treat as a business failure in the calling service; map to a local `XxxErrorCode` |

Do not write `try/catch` blocks in service implementations for infrastructure exceptions. Do not duplicate global error handling locally.

---

## 9. Anti-Patterns

Do not:

- Return entity objects directly from controller or service.
- Call `ApiResponse.fail(code, msg)` inline in business logic — use `fail(XxxErrorCode.X)` instead.
- Use `ApiResponse<?>`, `ApiResponse<Object>`, or raw `ApiResponse` as return type.
- Define business error codes below `10001` or reuse system-level codes (`0`, `400`, `401`, `403`, `500`, `503`).
- Access Feign response data without the full four-check guard.
- Put a plain `List<T>` in `data` — wrap it in `IPage` for paged results or use a dedicated response DTO.
- Handle infrastructure exceptions (DB, framework) as if they were normal business branches.
- Write multiple `fail()` overloads or pass raw strings to construct failure responses.
