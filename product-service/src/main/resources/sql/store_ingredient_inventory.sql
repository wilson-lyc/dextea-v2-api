CREATE TABLE `store_ingredient_inventory`
(
    `store_id`          BIGINT         NOT NULL COMMENT '门店ID',
    `ingredient_id`     BIGINT         NOT NULL COMMENT '原料ID',
    `quantity`          DECIMAL(10, 3) NOT NULL COMMENT '当前库存数量',
    `unit`              VARCHAR(16)    NOT NULL COMMENT '库存单位（如 g、kg、ml）',
    `warn_threshold`    DECIMAL(10, 3)          DEFAULT NULL COMMENT '低库存预警阈值，quantity <= warn_threshold 时触发预警',
    `last_restock_time` DATETIME                DEFAULT NULL COMMENT '最近一次补货时间',
    `create_time`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`store_id`, `ingredient_id`),
    KEY `idx_store_id` (`store_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='门店原料库存表';
