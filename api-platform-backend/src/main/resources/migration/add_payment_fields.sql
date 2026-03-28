-- 为订单表添加支付相关字段
ALTER TABLE order_info ADD COLUMN pay_trade_no VARCHAR(64) COMMENT '支付流水号（支付宝交易号）' AFTER status;
ALTER TABLE order_info ADD COLUMN pay_method VARCHAR(20) DEFAULT 'alipay' COMMENT '支付方式' AFTER pay_trade_no;

-- 添加索引
ALTER TABLE order_info ADD INDEX idx_pay_trade_no (pay_trade_no);
