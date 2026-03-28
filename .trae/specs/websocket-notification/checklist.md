# Checklist

## 后端检查项

- [x] WebSocket依赖已正确添加到pom.xml
- [x] WebSocketConfig配置类正确配置了端点和拦截器
- [x] WebSocket握手拦截器能正确获取用户身份
- [x] WebSocket处理器能正确处理连接、断开、消息事件
- [x] NotificationType枚举包含所有消息类型（含售后相关类型）
- [x] Notification实体与数据库表字段对应正确
- [x] NotificationMapper正确实现CRUD操作
- [x] NotificationService接口方法定义完整
- [x] NotificationServiceImpl正确实现消息发送、查询、标记已读功能
- [x] NotificationHandler接口设计合理，各类型处理器实现正确
- [x] NotificationController接口路径符合规范，分页查询已实现
- [x] 数据库迁移脚本正确创建了notification_message表
- [x] RequirementServiceImpl正确集成了消息推送
- [x] AfterSaleMessageServiceImpl正确集成了消息推送
- [x] RequirementAfterSaleServiceImpl正确集成了消息推送
- [x] ApiReviewServiceImpl正确集成了消息推送
- [x] 消息推送不会影响原有业务逻辑的正确执行

## 前端检查项

- [x] WebSocket连接管理工具类正确实现连接、断开、重连功能
- [x] 心跳保活机制正常工作
- [x] 导航栏消息图标正确显示红点徽标
- [x] 未读消息计数实时更新
- [x] 点击图标能正确展开消息列表
- [x] 消息列表正确显示消息内容
- [x] 消息按类型筛选功能正常
- [x] 消息分页加载功能正常
- [x] 点击消息能正确跳转到对应页面
- [x] 标记已读功能正常工作
- [x] 新消息实时弹窗提醒正常显示

## 功能验证项

- [ ] 需求选择接单者后，接单者能收到通知
- [ ] 需求交付后，发布者能收到通知
- [ ] 需求确认交付后，接单者能收到通知
- [ ] 需求状态变更后，相关用户能收到通知
- [ ] 售后对话新消息，申请人和开发者能收到通知
- [ ] 售后开发者回应后，申请人能收到通知
- [ ] 售后管理员裁定后，申请人和开发者能收到通知
- [ ] API新评价后，开发者能收到通知
- [ ] API评价被回复后，原评论用户能收到通知
- [ ] 用户追评后，开发者能收到通知
- [ ] 未读消息计数准确
- [ ] 消息已读状态正确更新
- [ ] WebSocket断开后能自动重连
- [ ] 多设备登录时消息推送正常

## 代码质量检查项

- [x] 消息模块设计符合单一职责原则
- [x] 消息处理器接口设计具有良好的扩展性
- [x] 代码复用性良好，无重复代码
- [x] 异常处理完善，不影响主业务流程
- [x] 接口路径体现update、delete操作
- [x] 列表查询接口已实现分页
- [x] 使用MyBatis Plus完成数据库查询
