/*
 Navicat Premium Data Transfer

 Source Server         : bendi
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : api_platform

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 17/03/2026 17:20:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for api_favorite
-- ----------------------------
DROP TABLE IF EXISTS `api_favorite`;
CREATE TABLE `api_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `api_id` bigint NOT NULL COMMENT 'API ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_api`(`user_id` ASC, `api_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_api_id`(`api_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for api_info
-- ----------------------------
DROP TABLE IF EXISTS `api_info`;
CREATE TABLE `api_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type_id` bigint NOT NULL COMMENT '类型ID',
  `user_id` bigint NOT NULL COMMENT '开发者ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API名称',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'API描述',
  `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'GET' COMMENT '请求方法 GET/POST/PUT/DELETE',
  `endpoint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接口地址',
  `target_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标服务器地址(如http://localhost:1234)',
  `request_params` json NULL COMMENT '请求参数JSON',
  `response_params` json NULL COMMENT '响应参数JSON',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  `price_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'per_call' COMMENT '价格单位 per_call/per_month/per_year',
  `call_limit` int NOT NULL DEFAULT 0 COMMENT '调用限制 0表示无限制',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT '状态 pending/approved/rejected/offline',
  `doc_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文档地址',
  `rating` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '评分 0-5',
  `invoke_count` bigint NULL DEFAULT 0 COMMENT '总调用次数',
  `success_count` bigint NULL DEFAULT 0 COMMENT '成功次数',
  `fail_count` bigint NULL DEFAULT 0 COMMENT '失败次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type_id`(`type_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for api_invoke_daily
-- ----------------------------
DROP TABLE IF EXISTS `api_invoke_daily`;
CREATE TABLE `api_invoke_daily`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api_id` bigint NOT NULL COMMENT 'API ID',
  `api_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API名称（冗余存储，便于筛选）',
  `caller_id` bigint NOT NULL COMMENT '调用者用户ID',
  `api_owner_id` bigint NOT NULL COMMENT 'API所有者用户ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `total_count` bigint NOT NULL DEFAULT 0 COMMENT '总调用次数',
  `success_count` bigint NOT NULL DEFAULT 0 COMMENT '成功次数',
  `fail_count` bigint NOT NULL DEFAULT 0 COMMENT '失败次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_api_caller_date`(`api_id` ASC, `caller_id` ASC, `stat_date` ASC) USING BTREE,
  INDEX `idx_api_id`(`api_id` ASC) USING BTREE,
  INDEX `idx_api_name`(`api_name` ASC) USING BTREE,
  INDEX `idx_caller_id`(`caller_id` ASC) USING BTREE,
  INDEX `idx_api_owner_id`(`api_owner_id` ASC) USING BTREE,
  INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 840 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API调用每日统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for api_test_record
-- ----------------------------
DROP TABLE IF EXISTS `api_test_record`;
CREATE TABLE `api_test_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api_id` bigint NOT NULL COMMENT 'API ID',
  `api_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'API名称',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `params` json NULL COMMENT '请求参数JSON',
  `result` json NULL COMMENT '响应结果JSON',
  `success` tinyint NULL DEFAULT 0 COMMENT '是否成功 0-失败 1-成功',
  `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误信息',
  `response_time` int NULL DEFAULT NULL COMMENT '响应时间(毫秒)',
  `status_code` int NULL DEFAULT NULL COMMENT 'HTTP状态码',
  `type` tinyint NULL DEFAULT 0 COMMENT '记录类型 0-自动调用记录 1-手动保存记录',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_api_id`(`api_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_user_api_type`(`user_id` ASC, `api_id` ASC, `type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API测试记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for api_type
-- ----------------------------
DROP TABLE IF EXISTS `api_type`;
CREATE TABLE `api_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `api_id` bigint NOT NULL COMMENT 'API ID',
  `api_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API名称',
  `buyer_id` bigint NOT NULL COMMENT '买家ID',
  `buyer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '买家名称',
  `invoke_count` int NOT NULL DEFAULT 0 COMMENT '调用次数 -1表示无限',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT '订单状态 pending/paid/completed/refunded/cancelled',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除标识 0-未删除 1-已删除',
  `rating` decimal(2, 1) NULL DEFAULT NULL COMMENT '订单评分(0.5-5.0)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_api_id`(`api_id` ASC) USING BTREE,
  INDEX `idx_buyer_id`(`buyer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for requirement
-- ----------------------------
DROP TABLE IF EXISTS `requirement`;
CREATE TABLE `requirement`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `response_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `budget` decimal(10, 2) NOT NULL,
  `deadline` datetime NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'open',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for requirement_applicant
-- ----------------------------
DROP TABLE IF EXISTS `requirement_applicant`;
CREATE TABLE `requirement_applicant`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `requirement_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'pending',
  `apply_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `is_admin` tinyint(1) NOT NULL COMMENT '头像',
  `access_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'AccessKey',
  `secret_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SecretKey',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `freeze_reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '冻结原因',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_api_quota
-- ----------------------------
DROP TABLE IF EXISTS `user_api_quota`;
CREATE TABLE `user_api_quota`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `api_id` bigint NOT NULL COMMENT 'API ID',
  `total_count` int NOT NULL DEFAULT 0 COMMENT '总购买次数',
  `used_count` int NOT NULL DEFAULT 0 COMMENT '已使用次数',
  `remaining_count` int NOT NULL DEFAULT 0 COMMENT '剩余次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_api`(`user_id` ASC, `api_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_api_id`(`api_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户API配额表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
