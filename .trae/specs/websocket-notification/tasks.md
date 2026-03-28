# Tasks

## 后端任务

- [x] Task 1: WebSocket基础设施搭建
  - [x] SubTask 1.1: 添加WebSocket依赖到pom.xml（spring-boot-starter-websocket）
  - [x] SubTask 1.2: 创建WebSocket配置类WebSocketConfig
  - [x] SubTask 1.3: 创建WebSocket握手拦截器WebSocketHandshakeInterceptor
  - [x] SubTask 1.4: 创建WebSocket处理器WebSocketServer

- [x] Task 2: 消息通知核心模块开发
  - [x] SubTask 2.1: 创建消息类型枚举NotificationType
  - [x] SubTask 2.2: 创建消息实体Notification
  - [x] SubTask 2.3: 创建消息Mapper NotificationMapper
  - [x] SubTask 2.4: 创建消息DTO（NotificationQueryDTO、NotificationCreateDTO）
  - [x] SubTask 2.5: 创建消息VO（NotificationVO）
  - [x] SubTask 2.6: 创建消息服务接口NotificationService
  - [x] SubTask 2.7: 创建消息服务实现NotificationServiceImpl
  - [x] SubTask 2.8: 创建消息处理器接口NotificationHandler及各类型实现
  - [x] SubTask 2.9: 创建消息控制器NotificationController

- [x] Task 3: 数据库迁移脚本
  - [x] SubTask 3.1: 创建notification_message表的SQL迁移脚本

- [x] Task 4: 需求模块消息推送集成
  - [x] SubTask 4.1: 在RequirementServiceImpl中注入NotificationService
  - [x] SubTask 4.2: selectApplicant方法添加消息推送（通知被选中的接单者）
  - [x] SubTask 4.3: deliver方法添加消息推送（通知需求发布者）
  - [x] SubTask 4.4: confirmDelivery方法添加消息推送（通知接单者）
  - [x] SubTask 4.5: updateStatus方法添加消息推送（状态变更通知）

- [x] Task 5: 售后模块消息推送集成
  - [x] SubTask 5.1: 在AfterSaleMessageServiceImpl中注入NotificationService
  - [x] SubTask 5.2: sendMessageWithPermissionCheck方法添加消息推送（通知申请人、开发者）
  - [x] SubTask 5.3: 在RequirementAfterSaleServiceImpl中注入NotificationService
  - [x] SubTask 5.4: respondAfterSale方法添加消息推送（通知申请人）
  - [x] SubTask 5.5: decideAfterSale方法添加消息推送（通知申请人和开发者）

- [x] Task 6: API评价模块消息推送集成
  - [x] SubTask 6.1: 在ApiReviewServiceImpl中注入NotificationService
  - [x] SubTask 6.2: createReview方法添加消息推送（通知API开发者）
  - [x] SubTask 6.3: userReplyReview方法添加消息推送（通知API开发者）
  - [x] SubTask 6.4: publisherReplyReview方法添加消息推送（通知评价用户）

## 前端任务

- [x] Task 7: WebSocket客户端实现
  - [x] SubTask 7.1: 创建WebSocket连接管理工具类
  - [x] SubTask 7.2: 实现自动重连机制
  - [x] SubTask 7.3: 实现心跳保活机制

- [x] Task 8: 导航栏消息提醒组件
  - [x] SubTask 8.1: 创建消息图标组件，显示红点徽标
  - [x] SubTask 8.2: 实现未读消息计数显示
  - [x] SubTask 8.3: 实现点击图标展开消息列表

- [x] Task 9: 消息列表组件
  - [x] SubTask 9.1: 创建消息列表组件
  - [x] SubTask 9.2: 实现消息按类型筛选
  - [x] SubTask 9.3: 实现消息分页加载
  - [x] SubTask 9.4: 实现点击消息跳转功能
  - [x] SubTask 9.5: 实现标记已读功能

- [x] Task 10: 消息提醒弹窗/浮层
  - [x] SubTask 10.1: 创建消息浮层组件
  - [x] SubTask 10.2: 实现新消息实时弹窗提醒

# Task Dependencies

- [Task 2] 依赖 [Task 1] - 消息服务需要WebSocket基础设施
- [Task 3] 可以与 [Task 1] 并行执行
- [Task 4] 依赖 [Task 2] - 需要消息服务完成后才能集成
- [Task 5] 依赖 [Task 2] - 需要消息服务完成后才能集成
- [Task 6] 依赖 [Task 2] - 需要消息服务完成后才能集成
- [Task 4, 5, 6] 可以并行执行 - 三个业务模块集成互不依赖
- [Task 7] 可以与 [Task 1-2] 并行执行
- [Task 8] 依赖 [Task 7] - 需要WebSocket客户端
- [Task 9] 依赖 [Task 8] - 需要导航栏组件
- [Task 10] 依赖 [Task 7] - 需要WebSocket客户端
