-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission_rel` (
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_name` VARCHAR(255) NOT NULL COMMENT '权限名称',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`role_id`, `permission_name`),
    KEY `idx_permission_name` (`permission_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';
