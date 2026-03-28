# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DexTea V2 API is a microservice-based backend for a tea shop platform. Currently under refactoring — only **common**, **staff**, and **store** modules are being actively developed. Other modules (auth, customer, menu, order, product) are legacy code.

## Build Commands

```bash
# Build all modules
mvn clean package -DskipTests

# Build specific module
mvn clean package -pl staff-service -am -DskipTests

# Run single test
mvn test -pl staff-service -Dtest=StaffAdminServiceTest
```

## Architecture

### Module Structure

```
dextea-api/
├── common/                    # Shared library for all modules
│   ├── code/                  # Enums: ResultCode, ResponseCode, GlobalErrorCode
│   ├── config/                # Druid, Redis, MybatisPlus config
│   ├── exception/             # UnauthorizedException, TosUtilException, GlobalExceptionHandler
│   ├── feign/                 # Feign clients: AuthFeign, StaffFeign, StoreInternalFeign
│   ├── model/                 # Shared models: StaffModel, StoreModel, OrderModel
│   ├── util/                  # DexteaJWTUtil, TosUtil
│   ├── validation/            # Custom validators and annotations
│   └── web/                   # ApiResponse, GlobalExceptionHandler
├── staff-service/             # Staff management microservice
│   ├── controller/            # REST endpoints (StaffAdminController, RoleAdminController)
│   ├── converter/             # Object converters (StaffConverter, RoleConverter)
│   ├── dto/                   # Request/Response DTOs
│   ├── entity/                # MyBatis-Plus entities
│   ├── enums/                 # Error codes (StaffErrorCode, StaffStatus, StaffType)
│   ├── mapper/                # MyBatis-Plus mappers
│   └── service/               # Business logic
├── store-service/             # Store management microservice
│   ├── controller/
│   ├── converter/
│   ├── dto/
│   ├── entity/
│   ├── enums/
│   ├── mapper/
│   └── service/
└── store-service-api/         # Store API interfaces for Feign
```

### Key Technologies

- **Java 21** with Spring Boot 3.5
- **MyBatis-Plus 3.5.9** for ORM
- **Sa-Token 1.45** for authentication
- **Hibernate Validator** for bean validation
- **Hutool 5.8** for utility functions

## Coding Conventions

### Response Format

All API responses use `ApiResponse<T>` from `cn.dextea.common.web.response.ApiResponse`:

```java
// Success
return ApiResponse.success(data);
return ApiResponse.success();  // void responses

// Failure (use module-specific error codes)
return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
```

### DTO Structure

**Request DTOs** (`dto/request/`):
- Use `@Data @Builder @NoArgsConstructor @AllArgsConstructor` from Lombok
- Validate fields with Jakarta Validation annotations
- Use `@Builder.Default` for pagination defaults

**Response DTOs** (`dto/response/`):
- Flat structure, no nested objects
- Include timestamps (`createTime`, `updateTime`) where applicable

### Validation Annotations

Use Jakarta Validation (`jakarta.validation.constraints.*`) for basic constraints:

```java
@NotBlank(message = "用户名不能为空")
@Size(max = 64, message = "用户名长度不能超过64位")
@NotNull(message = "员工类型不能为空")
@Min(value = 1, message = "当前页码不能小于1")
@Max(value = 100, message = "每页条数不能大于100")
@DecimalMin(value = "73.0", message = "经度范围无效")
@DecimalMax(value = "135.0", message = "经度范围无效")
```

Use custom `@EnumValue` for enum validation — enum must have static `isValid(Integer value)` method:

```java
@EnumValue(enumClass = StaffStatus.class, fieldName = "员工状态")
private Integer status;
```

Use `@PhoneNumber` for Chinese mobile validation:

```java
@PhoneNumber(fieldName = "联系电话")
private String phone;
```

Use `@Range` from Hibernate for range validation on integers:

```java
@Range(min = 500, max = 10000, message = "搜索半径建议在 500-10000 米之间")
private Integer radius;
```

### Validation Groups

Located at `cn.dextea.common.validation.group.ValidationGroups`:

```java
public interface Create {}
public interface Update {}
public interface PageQuery {}
```

Apply to DTO fields when the same DTO is used in multiple scenarios with different validation rules.

### Converter Pattern

All entity-to-response conversions go through converters (not direct mapping):

```java
@Component
public class StaffConverter {
    public StaffDetailResponse toStaffDetailResponse(StaffEntity staffEntity) {
        return StaffDetailResponse.builder()
                .id(staffEntity.getId())
                .username(staffEntity.getUsername())
                // ... other fields
                .build();
    }
}
```

Converters are injected into services and used for all response construction.

### Controller Pattern

```java
@RestController
@RequestMapping("/v1/admin/staffs")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class StaffAdminController {
    private final StaffAdminService staffAdminService;

    @PostMapping
    public ApiResponse<CreateStaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return staffAdminService.createStaff(request);
    }
}
```

- Use `@Valid` on `@RequestBody` parameters
- Use `@Validated` on controller class
- Path variable validation uses `@Min` directly on parameter
- Authentication via `@SaCheckLogin` annotation

### Error Codes

Module-specific error codes defined as enums implementing business errors:

```java
public enum StaffErrorCode {
    USERNAME_ALREADY_EXISTS(10001, "用户名已存在"),
    STAFF_NOT_FOUND(10003, "员工不存在");
    // ...
}
```

Generic HTTP errors use `ResponseCode` enum (0=success, 400=fail, 401=unauthorized, etc.).

### Entity Pattern

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("staff")
public class StaffEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    @TableField(select = false)  // exclude from SELECT queries
    private String password;
}
```

### Service Pattern

Service interfaces in `service/`, implementations in `service/impl/`:

```java
public interface StaffAdminService {
    ApiResponse<CreateStaffResponse> createStaff(CreateStaffRequest request);
}

@Service
@RequiredArgsConstructor
public class StaffAdminServiceImpl implements StaffAdminService {
    private final StaffMapper staffMapper;
    private final StaffConverter staffConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateStaffResponse> createStaff(CreateStaffRequest request) {
        // business logic
        return ApiResponse.success(staffConverter.toCreateStaffResponse(entity, password));
    }
}
```

### Pagination

Use `MyBatis-Plus` pagination with `IPage`:

```java
IPage<StaffEntity> entityPage = staffMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
IPage<StaffDetailResponse> responsePage = entityPage.convert(staffConverter::toStaffDetailResponse);
return ApiResponse.success(responsePage);
```

### Request Data Trimming

Always trim string inputs in service layer to avoid spacing issues:

```java
String username = request.getUsername().trim();
String realName = request.getRealName().trim();
```

### Common Validation Constants

From `cn.dextea.common.validation.constant.ValidationConstant`:

- Pagination: `PAGE_CURRENT_MIN=1`, `PAGE_SIZE_MIN=1`, `PAGE_SIZE_MAX=100`, `PAGE_SIZE_DEFAULT=10`
- String lengths: `NAME_MAX_LENGTH=64`, `TITLE_MAX_LENGTH=128`, `DESCRIPTION_MAX_LENGTH=255`, `ADDRESS_MAX_LENGTH=255`
- Geo: `LONGITUDE_MIN/MAX`, `LATITUDE_MIN/MAX`

## Current Refactoring Scope

Only the following are being refactored:
- `common/` module
- `staff-service/` module
- `store-service/` module

Legacy modules (auth, customer, menu, order, product) are out of scope and should not be modified during current refactoring.
