CREATE TABLE `store_product_rel`
(
    `store_id`    BIGINT   NOT NULL COMMENT '门店ID',
    `product_id`  BIGINT   NOT NULL COMMENT '商品ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`store_id`, `product_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='门店在售商品关联表（存在记录表示该门店该商品在售，不存在表示售罄）';
