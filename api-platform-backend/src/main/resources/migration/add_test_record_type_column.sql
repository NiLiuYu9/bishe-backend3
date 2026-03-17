-- 为api_test_record表添加type字段，用于区分自动调用记录和手动保存记录
-- 执行此脚本前请确保数据库中没有api_test_record表的数据冲突

-- 添加type字段
ALTER TABLE `api_test_record` ADD COLUMN `type` tinyint NULL DEFAULT 0 COMMENT '记录类型 0-自动调用记录 1-手动保存记录' AFTER `status_code`;

-- 添加复合索引，优化查询性能
ALTER TABLE `api_test_record` ADD INDEX `idx_user_api_type`(`user_id` ASC, `api_id` ASC, `type` ASC);

-- 将现有数据标记为手动保存记录
UPDATE `api_test_record` SET `type` = 1 WHERE `type` IS NULL OR `type` = 0;
