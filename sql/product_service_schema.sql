-- =============================================
-- Product Service 数据库表结构
-- =============================================

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` VARCHAR(64) NOT NULL COMMENT '商品名',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '商品介绍',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 原料表
CREATE TABLE IF NOT EXISTS `ingredient` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '原料ID',
    `name` VARCHAR(64) NOT NULL COMMENT '原料名称',
    `unit` VARCHAR(16) NOT NULL COMMENT '库存单位',
    `shelf_life` INT NOT NULL COMMENT '保质时长',
    `shelf_life_unit` TINYINT NOT NULL COMMENT '保质时长单位：1-天，2-周，3-月',
    `storage_method` TINYINT NOT NULL COMMENT '存储方式：1-常温，2-冷藏，3-冷冻',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='原料表';

-- 商品原料绑定表
CREATE TABLE IF NOT EXISTS `product_ingredient` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `ingredient_id` BIGINT NOT NULL COMMENT '原料ID',
    `quantity` DECIMAL(10,3) NOT NULL COMMENT '用量',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_ingredient_id` (`ingredient_id`),
    UNIQUE KEY `uk_product_ingredient` (`product_id`, `ingredient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品原料绑定表';

-- 商品客制化项目表
CREATE TABLE IF NOT EXISTS `product_customization_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `name` VARCHAR(64) NOT NULL COMMENT '项目名称',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `is_required` TINYINT NOT NULL DEFAULT 0 COMMENT '是否必选：0-否，1-是',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品客制化项目表';

-- 商品客制化选项表
CREATE TABLE IF NOT EXISTS `product_customization_option` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '选项ID',
    `item_id` BIGINT NOT NULL COMMENT '项目ID',
    `name` VARCHAR(64) NOT NULL COMMENT '选项名称',
    `price_adjustment` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格调整',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认选项：0-否，1-是',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品客制化选项表';

-- 选项原料绑定表
CREATE TABLE IF NOT EXISTS `customization_option_ingredient` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
    `option_id` BIGINT NOT NULL COMMENT '选项ID',
    `ingredient_id` BIGINT NOT NULL COMMENT '原料ID',
    `quantity` DECIMAL(10,3) NOT NULL COMMENT '消耗数量',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_option_id` (`option_id`),
    KEY `idx_ingredient_id` (`ingredient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选项原料绑定表';

-- 门店原料库存表
CREATE TABLE IF NOT EXISTS `store_ingredient_inventory` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '库存ID',
    `store_id` BIGINT NOT NULL COMMENT '门店ID',
    `ingredient_id` BIGINT NOT NULL COMMENT '原料ID',
    `quantity` DECIMAL(10,3) NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    `unit` VARCHAR(16) NOT NULL COMMENT '单位',
    `warn_threshold` DECIMAL(10,3) DEFAULT NULL COMMENT '低库存预警阈值',
    `last_restock_time` DATETIME DEFAULT NULL COMMENT '最后补货时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_ingredient_id` (`ingredient_id`),
    UNIQUE KEY `uk_store_ingredient` (`store_id`, `ingredient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='门店原料库存表';
