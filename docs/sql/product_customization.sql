-- Product Customization DDL
-- Module: product-service
-- Created: 2026-04-01

-- -------------------------------------------------------------------------
-- Table: product_customization_item
-- A customization dimension attached to a product (e.g. 温度、糖度、配料).
-- Single-selection only — each item allows the customer to pick exactly one
-- option from its option list.
-- -------------------------------------------------------------------------
CREATE TABLE product_customization_item (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    product_id  BIGINT       NOT NULL                COMMENT '所属产品ID',
    name        VARCHAR(64)  NOT NULL                COMMENT '定制项名称（如：温度、糖度）',
    sort_order  INT          NOT NULL DEFAULT 0      COMMENT '展示排序，越小越靠前',
    is_required TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '是否必选：1=必选，0=可选',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1=正常，0=已删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品定制项';

-- -------------------------------------------------------------------------
-- Table: product_customization_option
-- One selectable value within a customization item (e.g. 热、少冰、去冰).
-- Only a single option per item may be selected per order line.
-- -------------------------------------------------------------------------
CREATE TABLE product_customization_option (
    id               BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    item_id          BIGINT         NOT NULL               COMMENT '所属定制项ID',
    name             VARCHAR(64)    NOT NULL               COMMENT '选项名称（如：热、少冰）',
    price_adjustment DECIMAL(10,2)  NOT NULL DEFAULT 0.00  COMMENT '价格调整（正数加价，负数减价，0不变）',
    sort_order       INT            NOT NULL DEFAULT 0     COMMENT '展示排序，越小越靠前',
    is_default       TINYINT(1)     NOT NULL DEFAULT 0     COMMENT '是否默认选中：1=是，0=否',
    status           TINYINT        NOT NULL DEFAULT 1     COMMENT '状态：1=正常，0=已删除',
    create_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品定制选项';

-- -------------------------------------------------------------------------
-- Table: customization_option_ingredient
-- Optional binding between a customization option and an ingredient.
-- When a customer selects this option, the bound ingredient is deducted from
-- the store's inventory by the specified quantity.
-- An option with no row here requires no inventory deduction.
-- -------------------------------------------------------------------------
CREATE TABLE customization_option_ingredient (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    option_id   BIGINT        NOT NULL               COMMENT '定制选项ID',
    ingredient_id BIGINT      NOT NULL               COMMENT '原料ID（关联 ingredient 表）',
    quantity    DECIMAL(10,2) NOT NULL               COMMENT '每次选择该选项消耗的原料数量',
    unit        VARCHAR(16)   NOT NULL               COMMENT '计量单位（如：g、ml、个）',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_option_ingredient (option_id, ingredient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定制选项与原料的关联';
