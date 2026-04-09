CREATE TABLE `store_customization_option_rel`
(
    `store_id`    BIGINT   NOT NULL COMMENT '门店ID',
    `option_id`   BIGINT   NOT NULL COMMENT '客制化选项ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`store_id`, `option_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='门店客制化选项在售关联表（存在记录表示该门店该选项在售，不存在表示售罄）';
