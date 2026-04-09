CREATE TABLE `product`
(
    `id`          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name`        VARCHAR(100)   NOT NULL COMMENT '商品名称',
    `description` VARCHAR(500)            DEFAULT NULL COMMENT '商品简介',
    `price`       DECIMAL(10, 2) NOT NULL COMMENT '售价',
    `status`      TINYINT        NOT NULL COMMENT '商品状态：0=下架，1=上架',
    `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品表';
