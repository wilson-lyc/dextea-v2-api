-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` VARCHAR(255) NOT NULL COMMENT '角色名称',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '角色备注',
    `data_scope` TINYINT NOT NULL COMMENT '数据范围：0-所有数据，1-绑定门店数据，2-自定义数据',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '角色状态：0-禁用，1-可用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_name` (`name`),
    KEY `idx_data_scope` (`data_scope`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
