-- 员工门店关联表
CREATE TABLE IF NOT EXISTS `staff_store_rel` (
    `staff_id` BIGINT NOT NULL COMMENT '员工ID',
    `store_id` BIGINT NOT NULL COMMENT '门店ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`staff_id`, `store_id`),
    KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工门店关联表';
