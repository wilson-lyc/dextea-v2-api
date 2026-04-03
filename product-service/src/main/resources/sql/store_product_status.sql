CREATE TABLE `store_product_status`
(
    `store_id`    BIGINT   NOT NULL COMMENT '门店ID',
    `product_id`  BIGINT   NOT NULL COMMENT '商品ID',
    `status`      TINYINT  NOT NULL COMMENT '门店商品销售状态：0=售罄，1=在售',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`store_id`, `product_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='门店商品销售状态表';
