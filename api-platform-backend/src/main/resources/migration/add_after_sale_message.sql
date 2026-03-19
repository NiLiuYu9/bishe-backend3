-- 售后对话记录表
CREATE TABLE IF NOT EXISTS `after_sale_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `after_sale_id` bigint NOT NULL COMMENT '售后ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `sender_type` varchar(20) NOT NULL COMMENT '发送者类型 applicant/developer/admin',
  `content` varchar(1000) NOT NULL COMMENT '消息内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_after_sale_id` (`after_sale_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后对话记录表';
