# Tasks

## 后端任务

- [x] Task 1: 添加支付宝SDK依赖
  - [x] SubTask 1.1: 在pom.xml中添加支付宝SDK依赖(alipay-sdk-java)

- [x] Task 2: 数据库订单表扩展
  - [x] SubTask 2.1: 创建数据库迁移脚本，添加pay_trade_no(支付流水号)、pay_method(支付方式)字段

- [x] Task 3: 支付宝配置类
  - [x] SubTask 3.1: 创建AlipayConfig配置类，读取支付宝沙箱配置
  - [x] SubTask 3.2: 在application.yml中添加支付宝沙箱配置项

- [x] Task 4: 支付服务实现
  - [x] SubTask 4.1: 创建AlipayService接口，定义支付相关方法
  - [x] SubTask 4.2: 创建AlipayServiceImpl实现类，实现支付、回调、查询功能
  - [x] SubTask 4.3: 实现签名验证逻辑

- [x] Task 5: 支付接口开发
  - [x] SubTask 5.1: 在OrderController中添加支付接口(/pay/{orderId})
  - [x] SubTask 5.2: 在OrderController中添加支付回调接口(/pay/notify)
  - [x] SubTask 5.3: 在OrderController中添加支付状态查询接口(/pay/query/{orderId})

- [x] Task 6: 订单实体和服务更新
  - [x] SubTask 6.1: 更新OrderInfo实体，添加支付相关字段
  - [x] SubTask 6.2: 更新OrderInfoService，添加支付成功后的处理逻辑

## 前端任务

- [x] Task 7: 前端支付API
  - [x] SubTask 7.1: 在trade.ts中添加支付相关API调用方法

- [x] Task 8: 前端支付流程
  - [x] SubTask 8.1: 修改orders.vue中的handlePay方法，调用支付接口
  - [x] SubTask 8.2: 添加支付结果轮询或WebSocket通知
  - [x] SubTask 8.3: 添加支付中状态的UI展示

## 验证任务

- [x] Task 9: 功能测试
  - [x] SubTask 9.1: 测试支付流程完整性
  - [x] SubTask 9.2: 测试支付回调处理
  - [x] SubTask 9.3: 测试异常情况处理

# Task Dependencies
- [Task 2] 依赖 [Task 1] - 数据库迁移需要在依赖添加后执行
- [Task 3] 可以与 [Task 1] 并行执行
- [Task 4] 依赖 [Task 1, Task 3] - 支付服务需要SDK和配置
- [Task 5] 依赖 [Task 4] - 接口依赖服务实现
- [Task 6] 可以与 [Task 4] 并行执行
- [Task 7] 可以与 [Task 5] 并行执行
- [Task 8] 依赖 [Task 7] - 前端流程依赖API
- [Task 9] 依赖 [Task 5, Task 8] - 测试依赖所有功能完成
