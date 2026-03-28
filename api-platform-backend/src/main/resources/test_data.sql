/*
 测试数据生成脚本
 数据规模：30个用户
 生成日期：2026-03-27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 第一步：清空所有表数据（按依赖关系顺序删除）
-- =============================================

DELETE FROM `after_sale_message`;
DELETE FROM `requirement_after_sale`;
DELETE FROM `requirement_tag`;
DELETE FROM `requirement_applicant`;
DELETE FROM `requirement`;
DELETE FROM `api_review`;
DELETE FROM `api_invoke_daily`;
DELETE FROM `api_test_record`;
DELETE FROM `api_favorite`;
DELETE FROM `api_whitelist`;
DELETE FROM `user_api_quota`;
DELETE FROM `order_info`;
DELETE FROM `notification_message`;
DELETE FROM `user_tag`;
DELETE FROM `api_info`;
DELETE FROM `api_type`;
DELETE FROM `sys_user`;

-- =============================================
-- 第二步：插入用户数据（30个用户）
-- =============================================

INSERT INTO `sys_user` (`id`, `username`, `password`, `email`, `phone`, `is_admin`, `access_key`, `secret_key`, `status`, `create_time`, `update_time`, `deleted`, `freeze_reason`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@api-platform.com', '13800000001', 1, 'AK_ADMIN_001_SECRET', 'SK