-- 通知消息表
CREATE TABLE IF NOT EXISTS `notification_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `type` varchar(50) NOT NULL COMMENT '消息类型',
  `title` varchar(200) NOT NULL COMMENT '消息标题',
  `content` varchar(500) DEFAULT NULL COMMENT '消息内容',
  `related_id` bigint DEFAULT NULL COMMENT '关联业务ID',
  `related_type` varchar(50) DEFAULT NULL COMMENT '关联业务类型',
  `is_read` tinyint DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息表';
