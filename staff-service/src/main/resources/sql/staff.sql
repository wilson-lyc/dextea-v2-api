-- 员工表
CREATE TABLE IF NOT EXISTS `staff` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '员工ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码摘要',
    `real_name` VARCHAR(64) NOT NULL COMMENT '员工姓名',
    `user_type` TINYINT NOT NULL COMMENT '员工类型：0-公司员工，1-门店员工',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '员工状态：0-禁用，1-可用',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(64) DEFAULT NULL COMMENT '最后登录IP',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';
