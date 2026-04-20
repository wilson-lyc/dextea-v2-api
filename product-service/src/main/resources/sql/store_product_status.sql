CREATE TABLE `store_product_status`
(
    `store_id`    BIGINT   NOT NULL COMMENT '门店ID',
    `product_id`  BIGINT   NOT NULL COMMENT '商品ID',
    `status`      TINYINT  NOT NULL DEFAULT 0 COMMENT '门店商品状态：0=售罄，1=在售',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`store_id`, `product_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='门店商品状态表';
