-- 门店表
CREATE TABLE IF NOT EXISTS `store` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '门店ID',
    `name` VARCHAR(64) NOT NULL COMMENT '门店名称',
    `province` VARCHAR(32) NOT NULL COMMENT '省份',
    `city` VARCHAR(32) NOT NULL COMMENT '城市',
    `district` VARCHAR(32) NOT NULL COMMENT '区县',
    `address` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '门店状态：0-休息，1-营业，2-繁忙，3-关店',
    `longitude` DECIMAL(10,6) NOT NULL COMMENT '经度',
    `latitude` DECIMAL(10,6) NOT NULL COMMENT '纬度',
    `phone` VARCHAR(32) NOT NULL COMMENT '联系电话',
    `open_time` VARCHAR(64) NOT NULL COMMENT '营业时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`),
    KEY `idx_region` (`province`, `city`, `district`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='门店表';
