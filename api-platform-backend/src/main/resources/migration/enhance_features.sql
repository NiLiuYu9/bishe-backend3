-- API平台功能完善 - 数据库变更脚本
-- 执行时间: 2026-03-19

-- 1. 新增 api_whitelist 表（API白名单）
CREATE TABLE `api_whitelist` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api_id` bigint NOT NULL COMMENT 'API ID',
  `user_id` bigint NOT NULL COMMENT '白名单用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_api_user`(`api_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_api_id`(`api_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API白名单表' ROW_FORMAT = Dynamic;

-- 2. 修改 api_info 表（添加白名单启用字段）
ALTER TABLE `api_info`
ADD COLUMN `whitelist_enabled` tinyint NULL DEFAULT 0 COMMENT '是否启用白名单 0-否 1-是' AFTER `call_limit`;

-- 3. 新增 api_review 表（API评价回复）
CREATE TABLE `api_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `api_id` bigint NOT NULL COMMENT 'API ID',
  `user_id` bigint NOT NULL COMMENT '评价用户ID',
  `rating` decimal(2, 1) NOT NULL COMMENT '评分(0.5-5.0)',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `reply` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开发者回复',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_api_id`(`api_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API评价表' ROW_FORMAT = Dynamic;

-- 4. 新增 requirement_after_sale 表（需求售后申请）
CREATE TABLE `requirement_after_sale` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `requirement_id` bigint NOT NULL COMMENT '需求ID',
  `applicant_id` bigint NOT NULL COMMENT '申请人ID（需求发布方）',
  `developer_id` bigint NOT NULL COMMENT '开发者ID',
  `reason` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '售后原因',
  `unimplemented_features` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '未实现的功能点',
  `developer_response` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开发者回应',
  `developer_response_time` datetime NULL DEFAULT NULL COMMENT '开发者回应时间',
  `admin_id` bigint NULL DEFAULT NULL COMMENT '裁定管理员ID',
  `admin_decision` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员裁定说明',
  `admin_decision_time` datetime NULL DEFAULT NULL COMMENT '裁定时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT '状态 pending/resolved/rejected',
  `result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '裁定结果 completed/refunded（仅status=resolved时有效）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_requirement_id`(`requirement_id` ASC) USING BTREE,
  INDEX `idx_applicant_id`(`applicant_id` ASC) USING BTREE,
  INDEX `idx_developer_id`(`developer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '需求售后申请表' ROW_FORMAT = Dynamic;
