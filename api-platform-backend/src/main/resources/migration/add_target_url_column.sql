-- 动态路由功能数据库迁移脚本
-- 执行此脚本前请确保已备份数据库

-- 添加 target_url 字段到 api_info 表
ALTER TABLE `api_info` ADD COLUMN `target_url` VARCHAR(255) NULL COMMENT '目标服务器地址(如http://localhost:1234)' AFTER `endpoint`;

-- 示例：更新现有API的target_url（根据实际情况修改）
-- UPDATE `api_info` SET `target_url` = 'http://localhost:8081' WHERE `endpoint` LIKE '/%';
