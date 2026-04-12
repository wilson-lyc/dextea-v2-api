CREATE TABLE `product_ingredient_binding`
(
    `product_id`    BIGINT         NOT NULL COMMENT '商品ID',
    `ingredient_id` BIGINT         NOT NULL COMMENT '原料ID',
    `quantity`      DECIMAL(10, 3) NOT NULL COMMENT '每份用量',
    `create_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`product_id`, `ingredient_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品原料关联表';
