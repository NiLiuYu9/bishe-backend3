-- 用户技能标签表
CREATE TABLE IF NOT EXISTS `user_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_tag_name`(`tag_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户技能标签表';

-- 需求技术标签表
CREATE TABLE IF NOT EXISTS `requirement_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `requirement_id` bigint NOT NULL COMMENT '需求ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_requirement_id`(`requirement_id`) USING BTREE,
  INDEX `idx_tag_name`(`tag_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='需求技术标签表';
