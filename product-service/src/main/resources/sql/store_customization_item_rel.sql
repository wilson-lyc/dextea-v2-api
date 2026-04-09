CREATE TABLE `store_customization_item_rel`
(
    `store_id`    BIGINT   NOT NULL COMMENT '门店ID',
    `item_id`     BIGINT   NOT NULL COMMENT '客制化项目ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`store_id`, `item_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='门店客制化项目在售关联表（存在记录表示该门店该项目在售，不存在表示售罄）';
