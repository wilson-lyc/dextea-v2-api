/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : dextea_new

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 21/04/2026 14:58:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for staff_store_rel
-- ----------------------------
DROP TABLE IF EXISTS `staff_store_rel`;
CREATE TABLE `staff_store_rel` (
  `staff_id` bigint NOT NULL COMMENT '员工ID',
  `store_id` bigint NOT NULL COMMENT '门店ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`staff_id`,`store_id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工门店关联表';

SET FOREIGN_KEY_CHECKS = 1;
