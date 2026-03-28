# 支付宝沙箱支付功能 Checklist

## 后端实现检查

- [x] pom.xml中已添加支付宝SDK依赖
- [x] application.yml中已配置支付宝沙箱参数
- [x] AlipayConfig配置类正确读取配置
- [x] OrderInfo实体已添加pay_trade_no和pay_method字段
- [x] 数据库迁移脚本已执行，字段已添加
- [x] AlipayService接口定义完整
- [x] AlipayServiceImpl实现支付功能
- [x] AlipayServiceImpl实现回调验签功能
- [x] AlipayServiceImpl实现支付查询功能
- [x] OrderController支付接口(/pay/{orderId})实现正确
- [x] OrderController回调接口(/pay/notify)实现正确
- [x] OrderController查询接口(/pay/query/{orderId})实现正确
- [x] 支付成功后正确增加用户API配额

## 前端实现检查

- [x] trade.ts中已添加支付API方法
- [x] orders.vue中handlePay方法已修改为调用支付接口
- [x] 支付跳转逻辑正确
- [x] 支付结果展示正确
- [x] 支付中状态UI展示正确

## 功能测试检查

- [ ] 待支付订单可以正常发起支付
- [ ] 支付页面正确跳转到支付宝沙箱
- [ ] 支付成功后订单状态正确更新
- [ ] 支付成功后用户配额正确增加
- [ ] 支付回调签名验证正确
- [ ] 非待支付订单无法发起支付
- [ ] 支付失败有正确提示

## 安全检查

- [x] 支付回调接口不需要Session认证
- [x] 支付回调签名验证严格
- [x] 支付金额与订单金额一致校验
- [x] 敏感配置不在代码中硬编码（使用环境变量）
