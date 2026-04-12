CREATE TABLE `menu`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `name`        VARCHAR(64)  NOT NULL COMMENT '菜单名称',
    `description` VARCHAR(255)          DEFAULT NULL COMMENT '菜单描述',
    `status`      TINYINT      NOT NULL COMMENT '菜单状态：0=禁用，1=启用',
    `groups`      JSON                  DEFAULT NULL COMMENT '分组列表（JSON）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='菜单表';
