CREATE TABLE `product_customization_item_binding`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
    `product_id`  BIGINT   NOT NULL COMMENT '商品ID',
    `item_id`     BIGINT   NOT NULL COMMENT '客制化项目ID',
    `sort_order`  INT      NOT NULL DEFAULT 0 COMMENT '该商品下的排序序号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_product_item` (`product_id`, `item_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品与客制化项目绑定关系表';
