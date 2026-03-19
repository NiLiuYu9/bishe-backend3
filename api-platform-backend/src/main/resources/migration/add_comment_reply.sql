-- API评论回复功能 - 数据库变更脚本
-- 执行时间: 2026-03-19

-- 修改 api_review 表，添加评论回复相关字段
ALTER TABLE `api_review`
ADD COLUMN `parent_id` bigint NULL DEFAULT NULL COMMENT '父评论ID（用于回复关系）' AFTER `reply_time`,
ADD COLUMN `reply_type` tinyint NULL DEFAULT 0 COMMENT '回复类型 0-原评论 1-上架者回复 2-评论者回复' AFTER `parent_id`,
ADD INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE;
