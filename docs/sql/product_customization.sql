-- Product Customization DDL
-- Module: product-service
-- Updated: 2026-04-09

-- -------------------------------------------------------------------------
-- Table: product_customization_item
-- Global customization dimension (e.g. 温度、糖度、配料).
-- Items are standalone and can be bound to multiple products for reuse.
-- Status is company-controlled: 0=disabled, 1=active.
-- -------------------------------------------------------------------------
CREATE TABLE product_customization_item (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(64)  NOT NULL                COMMENT '定制项名称（如：温度、糖度）',
    sort_order  INT          NOT NULL DEFAULT 0      COMMENT '全局展示排序，越小越靠前',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1=激活，0=禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客制化项目（全局可复用）';

-- -------------------------------------------------------------------------
-- Table: product_customization_item_binding
-- Many-to-many join between products and customization items.
-- sort_order controls the display order of items within a specific product.
-- -------------------------------------------------------------------------
CREATE TABLE product_customization_item_binding (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    product_id  BIGINT   NOT NULL               COMMENT '商品ID',
    item_id     BIGINT   NOT NULL               COMMENT '客制化项目ID',
    sort_order  INT      NOT NULL DEFAULT 0     COMMENT '该商品下的排序序号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_product_item (product_id, item_id),
    KEY idx_product_id (product_id),
    KEY idx_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品与客制化项目绑定关系表';

-- -------------------------------------------------------------------------
-- Table: product_customization_option
-- One selectable value within a customization item (e.g. 热、少冰、去冰).
-- An option belongs to exactly one item.
-- Status is company-controlled: 0=disabled, 1=active.
-- -------------------------------------------------------------------------
CREATE TABLE product_customization_option (
    id               BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    item_id          BIGINT         NOT NULL               COMMENT '所属定制项ID',
    name             VARCHAR(64)    NOT NULL               COMMENT '选项名称（如：热、少冰）',
    price_adjustment DECIMAL(10,2)  NOT NULL DEFAULT 0.00  COMMENT '价格调整（正数加价，负数减价，0不变）',
    sort_order       INT            NOT NULL DEFAULT 0     COMMENT '展示排序，越小越靠前',
    is_default       TINYINT(1)     NOT NULL DEFAULT 0     COMMENT '是否默认选中：1=是，0=否',
    status           TINYINT        NOT NULL DEFAULT 1     COMMENT '状态：1=激活，0=禁用',
    create_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客制化选项';

-- -------------------------------------------------------------------------
-- Table: customization_option_ingredient
-- Optional binding between a customization option and an ingredient.
-- When a customer selects this option, the bound ingredient is deducted from
-- the store's inventory by the specified quantity.
-- -------------------------------------------------------------------------
CREATE TABLE customization_option_ingredient (
    id            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    option_id     BIGINT        NOT NULL               COMMENT '定制选项ID',
    ingredient_id BIGINT        NOT NULL               COMMENT '原料ID',
    quantity      DECIMAL(10,2) NOT NULL               COMMENT '每次选择该选项消耗的原料数量',
    unit          VARCHAR(16)   NOT NULL               COMMENT '计量单位（如：g、ml、个）',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_option_ingredient (option_id, ingredient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定制选项与原料的关联';
