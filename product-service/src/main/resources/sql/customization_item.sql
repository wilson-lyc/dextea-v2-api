CREATE TABLE `customization_item`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '客制化项目ID',
    `name`        VARCHAR(64)  NOT NULL COMMENT '项目名称（如：甜度、温度）',
    `description` VARCHAR(255) NULL     COMMENT '项目介绍',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '全局状态：0=禁用，1=激活',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='客制化项目表（全局独立，可复用绑定至多个商品）';
