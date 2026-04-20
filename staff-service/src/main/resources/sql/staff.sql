CREATE TABLE `staff`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`        VARCHAR(64)  NOT NULL COMMENT '登录账号',
    `password`        VARCHAR(255) NOT NULL COMMENT '登录密码（加密存储）',
    `real_name`       VARCHAR(64)  NOT NULL COMMENT '真实姓名',
    `user_type`       TINYINT      NOT NULL COMMENT '员工类型（见 StaffType 枚举）',
    `status`          TINYINT      NOT NULL DEFAULT 2 COMMENT '账号状态：0=禁用，1=可用，2=未激活',
    `last_login_time` DATETIME              DEFAULT NULL COMMENT '最近登录时间',
    `last_login_ip`   VARCHAR(64)           DEFAULT NULL COMMENT '最近登录IP',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='员工表';
