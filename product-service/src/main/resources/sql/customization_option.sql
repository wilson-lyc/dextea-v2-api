CREATE TABLE `customization_option`
(
    `id`                  BIGINT         NOT NULL AUTO_INCREMENT COMMENT '客制化选项ID',
    `item_id`             BIGINT         NOT NULL COMMENT '所属客制化项目ID',
    `name`                VARCHAR(64)    NOT NULL COMMENT '选项名称（如：少糖、标准糖）',
    `price`               DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '选项价格',
    `ingredient_id`       BIGINT         NULL     COMMENT '绑定原料ID，NULL表示不绑定',
    `ingredient_quantity` DECIMAL(10, 3) NULL     COMMENT '原料用量，ingredient_id不为NULL时有效',
    `status`              TINYINT        NOT NULL DEFAULT 1 COMMENT '全局状态：0=禁用，1=激活',
    `create_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='客制化选项表';
