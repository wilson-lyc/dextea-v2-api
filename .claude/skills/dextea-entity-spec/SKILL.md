---
name: dextea-entity-spec
description: Use when adding or modifying Entity classes in this repository. Defines the complete specification for all MyBatis-Plus entity classes and their corresponding SQL DDL files: naming conventions, class structure, Lombok annotations, field types, time fields, and DDL format.
---

# Dextea Entity Specification

Apply this skill whenever creating, renaming, or reviewing any Entity class or its SQL DDL file. All rules are mandatory. Do not invent alternative patterns.

---

## 1. Naming Conventions

### Entity class name

`{TableName}Entity` — table name in PascalCase with `Entity` suffix.

| Table name | Class name |
|---|---|
| `staff` | `StaffEntity` |
| `role` | `RoleEntity` |
| `staff_role_rel` | `StaffRoleRelEntity` |
| `role_permission_rel` | `RolePermissionRelEntity` |

### Package

All entity classes live in `entity/` under the service's base package.

```
cn.dextea.{service}.entity.{TableName}Entity
```

### SQL DDL file name

Each entity must have one corresponding DDL file:

```
src/main/resources/sql/{table_name}.sql
```

One file per table. File name must exactly match the table name.

---

## 2. Entity Types

There are two entity types with slightly different rules:

| Type | Description | Has `updateTime`? |
|---|---|---|
| **Main entity** | Has its own auto-increment primary key | Yes |
| **Relation entity** | Junction table, PK is composite foreign keys | No |

---

## 3. Class Structure

### Required Lombok annotations (all entity classes)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
```

All four annotations are required on every entity class.

### MyBatis-Plus annotations

| Annotation | Usage |
|---|---|
| `@TableName("table_name")` | Every entity class, value is the exact DB table name |
| `@TableId(value = "id", type = IdType.AUTO)` | Only on the `id` field of main entities |
| `@TableField("column_name")` | On every field whose Java name differs from the DB column name |
| `@TableField(select = false)` | On fields to exclude from all SELECT queries (e.g. password) |

**`@TableField` rule:** add it only when the Java camelCase field name does not match the DB column name, or when a special flag like `select = false` is needed. Do not add redundant `@TableField` on single-word fields that already match their column (e.g. `private String name` with column `name` needs no annotation).

---

## 4. Field Rules

### Primary key (`id`)

Main entities only. Always `Long`, always auto-increment.

```java
@TableId(value = "id", type = IdType.AUTO)
private Long id;
```

### Field types — use wrapper classes only

**Never use Java primitives.** All numeric, boolean, and character fields must use wrapper types.

| DB type | Java type |
|---|---|
| `bigint` | `Long` |
| `int` / `tinyint` | `Integer` |
| `varchar` / `text` | `String` |
| `datetime` | `LocalDateTime` |
| `decimal` | `BigDecimal` |
| `bit(1)` / boolean | `Boolean` |

### Time fields

**Main entity** — both fields required:

```java
@TableField("create_time")
private LocalDateTime createTime;

@TableField("update_time")
private LocalDateTime updateTime;
```

**Relation entity** — `createTime` only, no `updateTime`:

```java
@TableField("create_time")
private LocalDateTime createTime;
```

Time fields must always be at the end of the class, after all business fields. `createTime` comes before `updateTime`.

### Status / enum-backed fields

Use `Integer` (never an enum type in the entity). The enum conversion happens in the service layer or converter.

```java
private Integer status;

@TableField("user_type")
private Integer userType;
```

---

## 5. Complete Examples

### Main entity

```java
package cn.dextea.staff.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("role")
public class RoleEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String remark;

    @TableField("data_scope")
    private Integer dataScope;

    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
```

### Main entity with `select = false` field

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

    @TableField(select = false)
    private String password;

    @TableField("real_name")
    private String realName;

    @TableField("user_type")
    private Integer userType;

    private Integer status;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
```

### Relation entity

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("staff_role_rel")
public class StaffRoleRelEntity {
    @TableField("staff_id")
    private Long staffId;

    @TableField("role_id")
    private Long roleId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
```

---

## 6. SQL DDL Rules

### File header

Every DDL file must begin with the standard Navicat dump header:

```sql
/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : dextea_new

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: {DD/MM/YYYY HH:MM:SS}
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
```

End the file with:

```sql
SET FOREIGN_KEY_CHECKS = 1;
```

### CREATE TABLE skeleton

```sql
-- ----------------------------
-- Table structure for {table_name}
-- ----------------------------
DROP TABLE IF EXISTS `{table_name}`;
CREATE TABLE `{table_name}` (
  -- columns
  PRIMARY KEY (`id`)
  -- indexes
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='{table Chinese description}';
```

### Column type mapping

| Java type | SQL type | Notes |
|---|---|---|
| `Long` (PK) | `bigint NOT NULL AUTO_INCREMENT` | Primary key only |
| `Long` (FK / other) | `bigint NOT NULL` | Foreign keys and non-null long values |
| `Integer` (enum/status) | `tinyint NOT NULL` | Small integer enumerations |
| `Integer` (general) | `int NOT NULL` | General integers |
| `String` name/username | `varchar(64) NOT NULL` | |
| `String` remark/description | `varchar(255) DEFAULT NULL` | Nullable by default |
| `String` password/token | `varchar(255) NOT NULL` | |
| `BigDecimal` | `decimal(10,6)` | Adjust precision as needed |
| `LocalDateTime` (nullable) | `datetime DEFAULT NULL` | |
| `LocalDateTime` (createTime) | `datetime NOT NULL DEFAULT CURRENT_TIMESTAMP` | |
| `LocalDateTime` (updateTime) | `datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | |

All columns must have a `COMMENT` with a Chinese description.

### Time columns (always last, always this exact form)

```sql
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
```

Relation tables omit `update_time`.

### Index conventions

| Type | Syntax |
|---|---|
| Unique | `UNIQUE KEY \`uk_{column}\` (\`{column}\`)` |
| Normal index | `KEY \`idx_{column}\` (\`{column}\`)` |
| Composite PK (relation table) | `PRIMARY KEY (\`col1\`,\`col2\`)` |

Add `KEY idx_{column}` for any column that will be used as a filter in common queries (status, foreign keys in relation tables, etc.).

### Complete DDL examples

**Main entity:**

```sql
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '角色名',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `data_scope` tinyint NOT NULL COMMENT '可访问数据范围',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_name` (`name`),
  KEY `idx_data_scope` (`data_scope`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
```

**Relation table:**

```sql
DROP TABLE IF EXISTS `staff_role_rel`;
CREATE TABLE `staff_role_rel` (
  `staff_id` bigint NOT NULL COMMENT '员工ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`staff_id`,`role_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```
