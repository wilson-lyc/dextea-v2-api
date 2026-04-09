-- 顾客表
CREATE TABLE IF NOT EXISTS `customer` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '顾客ID',
    `nickname`    VARCHAR(32)  NOT NULL COMMENT '昵称',
    `email`       VARCHAR(128) NOT NULL COMMENT '邮箱',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码摘要',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='顾客表';
