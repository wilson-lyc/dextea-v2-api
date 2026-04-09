-- 权限表
CREATE TABLE IF NOT EXISTS `permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `name` VARCHAR(255) NOT NULL COMMENT '权限名称',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '权限备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';
