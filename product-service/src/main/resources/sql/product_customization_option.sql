CREATE TABLE `product_customization_option`
(
    `id`               BIGINT         NOT NULL AUTO_INCREMENT COMMENT '客制化选项ID',
    `item_id`          BIGINT         NOT NULL COMMENT '所属客制化项目ID',
    `name`             VARCHAR(64)    NOT NULL COMMENT '选项名称（如：少糖、标准糖）',
    `price_adjustment` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '价格调整（正=加价，负=减价，0=不变）',
    `sort_order`       INT            NOT NULL DEFAULT 0 COMMENT '排序序号',
    `is_default`       TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '是否默认选项：0=否，1=是',
    `status`           TINYINT        NOT NULL COMMENT '状态：0=已删除，1=正常',
    `create_time`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品客制化选项表';
