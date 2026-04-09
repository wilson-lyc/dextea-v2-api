CREATE TABLE `store_menu_rel`
(
    `store_id`    BIGINT   NOT NULL COMMENT '门店ID',
    `menu_id`     BIGINT   NOT NULL COMMENT '菜单ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    PRIMARY KEY (`store_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='门店菜单绑定表（每个门店只能绑定一个菜单）';
