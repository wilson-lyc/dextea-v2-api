CREATE TABLE `ingredient`
(
    `id`             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '原料ID',
    `name`           VARCHAR(64) NOT NULL COMMENT '原料名称',
    `unit`           VARCHAR(16) NOT NULL COMMENT '库存单位（如 g、kg、ml）',
    `shelf_life`     INT         NOT NULL COMMENT '保质时长数值',
    `shelf_life_unit` TINYINT    NOT NULL COMMENT '保质时长单位：1=小时，2=天',
    `storage_method`       TINYINT     NOT NULL COMMENT '存储方式：1=冷藏，2=冷冻，3=常温',
    `prepared_expiry`      INT         NOT NULL COMMENT '制备后保质时长数值',
    `prepared_expiry_unit` TINYINT     NOT NULL COMMENT '制备后保质时长单位：1=小时，2=天',
    `status`               TINYINT     NOT NULL COMMENT '状态：0=已删除，1=正常',
    `create_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`, `status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='原料表';
