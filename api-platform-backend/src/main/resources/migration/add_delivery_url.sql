-- 添加需求交付网址字段
ALTER TABLE `requirement` ADD COLUMN `delivery_url` VARCHAR(500) NULL DEFAULT NULL COMMENT '交付网址' AFTER `deleted`;
