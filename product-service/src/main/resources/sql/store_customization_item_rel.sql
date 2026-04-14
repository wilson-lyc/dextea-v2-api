CREATE TABLE `store_customization_item_status`
(
    `store_id`    BIGINT  NOT NULL COMMENT '门店ID',
    `item_id`     BIGINT  NOT NULL COMMENT '客制化项目ID',
    `status`      TINYINT NOT NULL DEFAULT 0 COMMENT '门店客制化项目状态：0=售罄，1=在售',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`store_id`, `item_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='门店客制化项目状态表';
