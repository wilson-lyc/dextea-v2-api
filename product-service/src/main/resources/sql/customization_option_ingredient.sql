CREATE TABLE `customization_option_ingredient`
(
    `option_id`     BIGINT         NOT NULL COMMENT '客制化选项ID',
    `ingredient_id` BIGINT         NOT NULL COMMENT '原料ID',
    `quantity`      DECIMAL(10, 3) NOT NULL COMMENT '该选项对应的原料用量',
    `create_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`option_id`, `ingredient_id`),
    KEY `idx_option_id` (`option_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='客制化选项原料关联表';
