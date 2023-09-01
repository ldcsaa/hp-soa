/*
 Navicat Premium Data Transfer

 Source Server         : docker-mysql
 Source Server Type    : MySQL
 Source Server Version : 80033 (8.0.33)
 Source Host           : 192.168.56.23:3306
 Source Schema         : soa-demo_db

 Target Server Type    : MySQL
 Target Server Version : 80033 (8.0.33)
 File Encoding         : 65001

 Date: 01/09/2023 18:48:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_demo_event
-- ----------------------------
DROP TABLE IF EXISTS `t_demo_event`;
CREATE TABLE `t_demo_event`  (
  `id` bigint NOT NULL,
  `biz_id` bigint NOT NULL,
  `region_id` int NOT NULL,
  `domain_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `event_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `exchange` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `routing_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `msg_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `msg` json NOT NULL,
  `source_request_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `send_flag` int NOT NULL DEFAULT 0,
  `retries` int NOT NULL DEFAULT 0,
  `last_send_time` timestamp(3) NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` timestamp(3) NOT NULL,
  `update_time` timestamp(3) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_msg_id`(`msg_id` ASC) USING BTREE,
  INDEX `ix_id_send_flag`(`id` ASC, `send_flag` ASC) USING BTREE,
  INDEX `ix_last_send_time`(`last_send_time` ASC, `send_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
