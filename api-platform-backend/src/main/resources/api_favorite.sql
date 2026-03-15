-- ----------------------------
-- Table structure for api_favorite
-- ----------------------------
DROP TABLE IF EXISTS `api_favorite`;
CREATE TABLE `api_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `api_id` bigint NOT NULL COMMENT 'API ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_api`(`user_id`, `api_id`),
  INDEX `idx_user_id`(`user_id`),
  INDEX `idx_api_id`(`api_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API收藏表';
