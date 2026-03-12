-- 测试数据
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. API类型数据
INSERT INTO `api_type` (`id`, `name`, `description`, `create_time`, `update_time`, `deleted`) VALUES
(1, '天气', '天气相关API', '2026-02-11 10:00:00', '2026-02-11 10:00:00', 0),
(2, '地图', '地图相关API', '2026-02-12 10:00:00', '2026-02-12 10:00:00', 0),
(3, '支付', '支付相关API', '2026-02-13 10:00:00', '2026-02-13 10:00:00', 0),
(4, '短信', '短信相关API', '2026-02-14 10:00:00', '2026-02-14 10:00:00', 0),
(5, '翻译', '翻译相关API', '2026-02-15 10:00:00', '2026-02-15 10:00:00', 0);

-- 2. API信息数据 (假设用户ID为1,2,3，对应注册的三个用户)
INSERT INTO `api_info` (`id`, `type_id`, `user_id`, `name`, `description`, `method`, `endpoint`, `request_params`, `response_params`, `price`, `price_unit`, `call_limit`, `status`, `doc_url`, `rating`, `invoke_count`, `success_count`, `fail_count`, `create_time`, `update_time`, `deleted`) VALUES
(1, 1, 2, '实时天气查询', '根据城市名称查询实时天气', 'GET', '/api/weather/realtime', '[{"name":"city","type":"string","required":true,"description":"城市名称","example":"北京"}]', '[{"name":"temperature","type":"number","required":true,"description":"温度","example":"25"},{"name":"humidity","type":"number","required":true,"description":"湿度","example":"60"},{"name":"wind","type":"string","required":true,"description":"风向风力","example":"东北风3级"}]', 0.01, 'per_call', 1000, 'approved', 'https://example.com/docs/weather', 4.5, 120, 115, 5, '2026-02-15 10:00:00', '2026-03-01 10:00:00', 0),
(2, 1, 2, '天气预报', '根据城市名称查询未来7天天气预报', 'GET', '/api/weather/forecast', '[{"name":"city","type":"string","required":true,"description":"城市名称","example":"上海"}]', '[{"name":"forecast","type":"array","required":true,"description":"天气预报列表","example":"[{\"date\":\"2026-03-12\",\"temperature\":26}]"}]', 0.02, 'per_call', 500, 'approved', 'https://example.com/docs/weather-forecast', 4.2, 80, 75, 5, '2026-02-16 10:00:00', '2026-03-02 10:00:00', 0),
(3, 2, 3, '地理编码', '根据地址获取经纬度', 'GET', '/api/map/geocode', '[{"name":"address","type":"string","required":true,"description":"地址","example":"北京市海淀区"}]', '[{"name":"latitude","type":"number","required":true,"description":"纬度","example":"39.9042"},{"name":"longitude","type":"number","required":true,"description":"经度","example":"116.4074"}]', 0.03, 'per_call', 2000, 'approved', 'https://example.com/docs/geocode', 4.8, 200, 195, 5, '2026-02-17 10:00:00', '2026-03-03 10:00:00', 0),
(4, 2, 3, '路径规划', '根据起点和终点规划路径', 'POST', '/api/map/route', '[{"name":"origin","type":"string","required":true,"description":"起点地址","example":"北京"},{"name":"destination","type":"string","required":true,"description":"终点地址","example":"上海"}]', '[{"name":"distance","type":"number","required":true,"description":"距离(米)","example":"1200000"},{"name":"duration","type":"number","required":true,"description":"时长(秒)","example":"36000"}]', 0.05, 'per_call', 1000, 'approved', 'https://example.com/docs/route', 4.6, 150, 145, 5, '2026-02-18 10:00:00', '2026-03-04 10:00:00', 0),
(5, 3, 2, '支付创建', '创建支付订单', 'POST', '/api/pay/create', '[{"name":"amount","type":"number","required":true,"description":"金额","example":"100.00"},{"name":"orderId","type":"string","required":true,"description":"订单ID","example":"ORD123456"}]', '[{"name":"paymentUrl","type":"string","required":true,"description":"支付链接","example":"https://pay.example.com/xxx"},{"name":"orderNo","type":"string","required":true,"description":"支付单号","example":"PAY123456"}]', 0.10, 'per_call', 500, 'approved', 'https://example.com/docs/pay-create', 4.7, 90, 85, 5, '2026-02-19 10:00:00', '2026-03-05 10:00:00', 0),
(6, 4, 3, '短信发送', '发送短信验证码', 'POST', '/api/sms/send', '[{"name":"phone","type":"string","required":true,"description":"手机号","example":"13800138000"},{"name":"code","type":"string","required":true,"description":"验证码","example":"123456"}]', '[{"name":"success","type":"boolean","required":true,"description":"是否成功","example":"true"},{"name":"message","type":"string","required":true,"description":"消息","example":"发送成功"}]', 0.08, 'per_call', 3000, 'approved', 'https://example.com/docs/sms-send', 4.3, 250, 240, 10, '2026-02-20 10:00:00', '2026-03-06 10:00:00', 0),
(7, 5, 2, '文本翻译', '将文本从一种语言翻译为另一种语言', 'POST', '/api/translate/text', '[{"name":"text","type":"string","required":true,"description":"待翻译文本","example":"Hello"},{"name":"source","type":"string","required":true,"description":"源语言","example":"en"},{"name":"target","type":"string","required":true,"description":"目标语言","example":"zh"}]', '[{"name":"translated","type":"string","required":true,"description":"翻译结果","example":"你好"}]', 0.04, 'per_call', 1500, 'approved', 'https://example.com/docs/translate', 4.4, 180, 175, 5, '2026-02-21 10:00:00', '2026-03-07 10:00:00', 0),
(8, 1, 2, '空气质量查询', '查询城市空气质量', 'GET', '/api/weather/air', '[{"name":"city","type":"string","required":true,"description":"城市名称","example":"北京"}]', '[{"name":"aqi","type":"number","required":true,"description":"空气质量指数","example":"50"},{"name":"level","type":"string","required":true,"description":"空气质量等级","example":"优"}]', 0.01, 'per_call', 2000, 'pending', 'https://example.com/docs/air-quality', 0.0, 0, 0, 0, '2026-03-10 10:00:00', '2026-03-10 10:00:00', 0);

-- 3. 订单数据 (假设买家ID为1,2，对应注册的用户)
INSERT INTO `order_info` (`id`, `order_no`, `api_id`, `api_name`, `buyer_id`, `buyer_name`, `invoke_count`, `price`, `status`, `pay_time`, `complete_time`, `create_time`, `update_time`, `deleted`) VALUES
(1, 'ORD20260228001', 1, '实时天气查询', 1, 'user1', 100, 1.00, 'completed', '2026-02-28 10:00:00', '2026-02-28 10:00:00', '2026-02-28 10:00:00', '2026-02-28 10:00:00', 0),
(2, 'ORD20260301002', 2, '天气预报', 1, 'user1', 50, 1.00, 'completed', '2026-03-02 10:00:00', '2026-03-02 10:00:00', '2026-03-01 10:00:00', '2026-03-02 10:00:00', 0),
(3, 'ORD20260305003', 3, '地理编码', 2, 'user2', 200, 6.00, 'completed', '2026-03-06 10:00:00', '2026-03-06 10:00:00', '2026-03-05 10:00:00', '2026-03-06 10:00:00', 0),
(4, 'ORD20260308004', 5, '支付创建', 2, 'user2', 100, 10.00, 'paid', '2026-03-09 10:00:00', NULL, '2026-03-08 10:00:00', '2026-03-09 10:00:00', 0),
(5, 'ORD20260310005', 6, '短信发送', 1, 'user1', 500, 40.00, 'pending', NULL, NULL, '2026-03-10 10:00:00', '2026-03-10 10:00:00', 0);

-- 4. 用户API配额数据 (假设用户ID为1,2，对应注册的用户)
INSERT INTO `user_api_quota` (`id`, `user_id`, `api_id`, `total_count`, `used_count`, `remaining_count`, `create_time`, `update_time`) VALUES
(1, 1, 1, 100, 30, 70, '2026-02-28 10:00:00', '2026-03-10 10:00:00'),
(2, 1, 2, 50, 10, 40, '2026-03-02 10:00:00', '2026-03-10 10:00:00'),
(3, 2, 3, 200, 50, 150, '2026-03-06 10:00:00', '2026-03-10 10:00:00'),
(4, 2, 5, 100, 0, 100, '2026-03-09 10:00:00', '2026-03-09 10:00:00');

-- 5. API调用每日统计表数据 (假设调用者ID为1,2，API所有者ID为2,3)
INSERT INTO `api_invoke_daily` (`id`, `api_id`, `api_name`, `caller_id`, `api_owner_id`, `stat_date`, `total_count`, `success_count`, `fail_count`, `create_time`, `update_time`) VALUES
(1, 1, '实时天气查询', 1, 2, '2026-03-01', 20, 18, 2, '2026-03-01 23:59:59', '2026-03-01 23:59:59'),
(2, 1, '实时天气查询', 1, 2, '2026-03-02', 10, 10, 0, '2026-03-02 23:59:59', '2026-03-02 23:59:59'),
(3, 2, '天气预报', 1, 2, '2026-03-02', 5, 5, 0, '2026-03-02 23:59:59', '2026-03-02 23:59:59'),
(4, 3, '地理编码', 2, 3, '2026-03-06', 30, 28, 2, '2026-03-06 23:59:59', '2026-03-06 23:59:59'),
(5, 1, '实时天气查询', 1, 2, '2026-03-10', 5, 5, 0, '2026-03-10 23:59:59', '2026-03-10 23:59:59'),
(6, 3, '地理编码', 2, 3, '2026-03-10', 20, 19, 1, '2026-03-10 23:59:59', '2026-03-10 23:59:59');

-- 6. API测试记录数据 (假设用户ID为1,2，对应注册的用户)
INSERT INTO `api_test_record` (`id`, `api_id`, `api_name`, `user_id`, `params`, `result`, `success`, `error_msg`, `response_time`, `status_code`, `create_time`) VALUES
(1, 1, '实时天气查询', 1, '{"city": "北京"}', '{"temperature": 25, "humidity": 60, "wind": "东北风3级"}', 1, NULL, 120, 200, '2026-03-01 10:00:00'),
(2, 1, '实时天气查询', 1, '{"city": "上海"}', '{"temperature": 28, "humidity": 70, "wind": "东南风2级"}', 1, NULL, 100, 200, '2026-03-01 10:30:00'),
(3, 2, '天气预报', 1, '{"city": "北京"}', '{"forecast": [{"date": "2026-03-12", "temperature": 26}, {"date": "2026-03-13", "temperature": 27}]}', 1, NULL, 150, 200, '2026-03-02 10:00:00'),
(4, 3, '地理编码', 2, '{"address": "北京市海淀区"}', '{"latitude": 39.9042, "longitude": 116.4074}', 1, NULL, 90, 200, '2026-03-06 10:00:00'),
(5, 1, '实时天气查询', 2, '{"city": "广州"}', '{"temperature": 30, "humidity": 80, "wind": "南风1级"}', 1, NULL, 110, 200, '2026-03-10 10:00:00'),
(6, 5, '支付创建', 2, '{"amount": 100, "orderId": "TEST123"}', '{"paymentUrl": "https://example.com/pay/TEST123", "orderNo": "PAY20260310001"}', 1, NULL, 200, 200, '2026-03-10 10:30:00'),
(7, 6, '短信发送', 1, '{"phone": "13800138000", "code": "123456"}', '{"success": true, "message": "短信发送成功"}', 1, NULL, 300, 200, '2026-03-10 11:00:00'),
(8, 7, '文本翻译', 1, '{"text": "Hello", "source": "en", "target": "zh"}', '{"translated": "你好"}', 1, NULL, 180, 200, '2026-03-10 11:30:00');

-- 7. 需求数据 (假设用户ID为1,2，对应注册的用户)
INSERT INTO `requirement` (`id`, `user_id`, `title`, `description`, `request_params`, `response_params`, `budget`, `deadline`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, 1, '智能推荐API', '根据用户行为数据推荐相关内容', '[{"name":"userId","type":"string","required":true,"description":"用户ID","example":"user123"},{"name":"page","type":"number","required":false,"description":"页码","example":"1"},{"name":"size","type":"number","required":false,"description":"每页数量","example":"10"}]', '[{"name":"recommendations","type":"array","required":true,"description":"推荐列表","example":"[{\"id\":\"1\",\"title\":\"商品A\",\"score\":0.95}]"}]', 500.00, '2026-03-17 23:59:59', 'open', '2026-03-08 10:00:00', '2026-03-08 10:00:00', 0),
(2, 2, '图像识别API', '识别图片中的物体和场景', '[{"name":"imageUrl","type":"string","required":true,"description":"图片URL","example":"https://example.com/image.jpg"}]', '[{"name":"objects","type":"array","required":true,"description":"识别结果列表","example":"[{\"name\":\"猫\",\"confidence\":0.98}]"}]', 800.00, '2026-03-20 23:59:59', 'open', '2026-03-09 10:00:00', '2026-03-09 10:00:00', 0),
(3, 1, '股票行情API', '获取实时股票行情数据', '[{"name":"symbol","type":"string","required":true,"description":"股票代码","example":"000001"}]', '[{"name":"price","type":"number","required":true,"description":"当前价格","example":"10.50"},{"name":"change","type":"number","required":true,"description":"涨跌幅","example":"0.05"},{"name":"volume","type":"number","required":true,"description":"成交量","example":"1000000"}]', 300.00, '2026-03-15 23:59:59', 'closed', '2026-03-06 10:00:00', '2026-03-10 10:00:00', 0);

-- 8. 需求申请数据 (假设用户ID为2,3，对应注册的用户)
INSERT INTO `requirement_applicant` (`id`, `requirement_id`, `user_id`, `description`, `status`, `apply_time`) VALUES
(1, 1, 2, '我有丰富的推荐系统开发经验，可以为您提供高质量的智能推荐API', 'pending', '2026-03-09 10:00:00'),
(2, 1, 3, '我团队专注于机器学习算法，可以开发精准的推荐系统', 'pending', '2026-03-10 10:00:00'),
(3, 2, 2, '我们公司有专业的图像识别团队，可以提供高精度的图像识别API', 'pending', '2026-03-10 10:30:00');

SET FOREIGN_KEY_CHECKS = 1;
