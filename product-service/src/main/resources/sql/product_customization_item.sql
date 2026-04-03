CREATE TABLE `product_customization_item`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '客制化项目ID',
    `product_id`  BIGINT      NOT NULL COMMENT '所属商品ID',
    `name`        VARCHAR(64) NOT NULL COMMENT '项目名称（如：甜度、温度）',
    `sort_order`  INT         NOT NULL DEFAULT 0 COMMENT '排序序号',
    `status`      TINYINT     NOT NULL COMMENT '状态：0=已删除，1=正常',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品客制化项目表';
