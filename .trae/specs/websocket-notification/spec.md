# WebSocket消息提醒功能 Spec

## Why
当前系统缺乏实时消息提醒机制，用户无法及时获知需求模块的新消息、需求状态变更、售后消息以及API评价相关动态，需要手动刷新页面查看，用户体验较差。通过WebSocket实现实时消息推送，可以显著提升用户交互体验。

## What Changes
- 新增WebSocket依赖和配置
- 创建统一的消息通知模块（抽象设计，支持扩展）
- 实现需求模块消息提醒（新消息、状态更新）
- 实现售后模块消息提醒（新消息）
- 实现API评价模块消息提醒（回复通知、新评论通知）
- 前端导航栏红点提示功能
- 消息列表查看和跳转功能

## Impact
- Affected specs: 需求模块、售后模块、API评价模块
- Affected code:
  - 新增: websocket配置、消息实体、消息服务、消息控制器
  - 修改: RequirementServiceImpl、AfterSaleMessageServiceImpl、ApiReviewServiceImpl（集成消息推送）

## ADDED Requirements

### Requirement: WebSocket连接管理
系统应提供WebSocket连接管理能力，支持用户建立长连接并保持会话。

#### Scenario: 用户建立WebSocket连接
- **WHEN** 用户登录系统后
- **THEN** 前端自动建立WebSocket连接，后端维护用户ID与WebSocket会话的映射关系

#### Scenario: 用户断开连接
- **WHEN** 用户退出登录或关闭页面
- **THEN** 系统自动清理该用户的WebSocket会话

### Requirement: 消息类型抽象设计
系统应设计可扩展的消息类型体系，支持多种业务场景的消息通知。

#### Scenario: 消息类型定义
- **WHEN** 需要新增一种消息类型
- **THEN** 只需实现消息处理器接口，无需修改核心消息推送逻辑

### Requirement: 需求模块新消息提醒
当需求模块有新消息时，系统应实时推送通知给相关用户。

#### Scenario: 需求对话新消息
- **WHEN** 用户在需求对话中发送新消息
- **THEN** 系统向需求发布者和接单者推送消息提醒
- **AND** 前端导航栏显示红点提示

#### Scenario: 点击消息跳转
- **WHEN** 用户点击某条需求消息
- **THEN** 跳转到对应需求的对话页面

### Requirement: 需求状态更新提醒
当需求状态发生变更时，系统应实时通知相关用户。

#### Scenario: 需求状态变更
- **WHEN** 需求状态从open变为in_progress、delivered、completed等
- **THEN** 系统向需求发布者和接单者推送状态变更通知
- **AND** 前端导航栏显示红点提示

### Requirement: 售后模块新消息提醒
当售后对话中有新消息时，系统应实时推送通知给相关用户。

#### Scenario: 售后对话新消息
- **WHEN** 用户在售后对话中发送新消息
- **THEN** 系统向售后申请人和开发者推送消息提醒
- **AND** 如果有管理员参与，也向管理员推送
- **AND** 前端导航栏显示红点提示

#### Scenario: 点击售后消息跳转
- **WHEN** 用户点击某条售后消息
- **THEN** 跳转到对应售后的对话页面

#### Scenario: 售后状态变更提醒
- **WHEN** 售后状态变更（如开发者回应、管理员裁定）
- **THEN** 系统向相关用户推送状态变更通知

### Requirement: API评价回复提醒
当API评价被回复时，系统应通知原评论用户。

#### Scenario: 开发者回复评价
- **WHEN** API开发者回复用户的评价
- **THEN** 系统向评价用户推送回复通知

#### Scenario: 用户追评回复
- **WHEN** 用户对开发者的回复进行追评
- **THEN** 系统向API开发者推送追评通知

### Requirement: API新评论提醒
当用户在自己开发的API下发表新评论时，系统应通知API开发者。

#### Scenario: 新增API评论
- **WHEN** 用户在API下发表新评价
- **THEN** 系统向API开发者推送新评论通知

### Requirement: 消息列表查询
系统应提供消息列表查询接口，支持分页和按类型筛选。

#### Scenario: 查询未读消息
- **WHEN** 用户请求未读消息列表
- **THEN** 返回按时间倒序排列的未读消息，支持分页

#### Scenario: 标记消息已读
- **WHEN** 用户查看消息详情或点击消息
- **THEN** 系统将该消息标记为已读状态

### Requirement: 未读消息计数
系统应提供实时未读消息计数功能。

#### Scenario: 获取未读数量
- **WHEN** 用户建立WebSocket连接后
- **THEN** 系统推送当前未读消息总数
- **AND** 每次有新消息时更新计数

## MODIFIED Requirements

### Requirement: 需求服务集成消息推送
RequirementServiceImpl需要在关键业务操作后触发消息推送。

**修改内容**：
- selectApplicant方法：选择接单者后通知被选中的用户
- deliver方法：交付后通知需求发布者
- confirmDelivery方法：确认交付后通知接单者
- updateStatus方法：状态变更时通知相关用户

### Requirement: 售后服务集成消息推送
AfterSaleMessageServiceImpl和RequirementAfterSaleServiceImpl需要在关键业务操作后触发消息推送。

**修改内容**：
- sendMessageWithPermissionCheck方法：发送售后消息后通知相关用户（申请人、开发者、管理员）
- respondAfterSale方法：开发者回应售后申请后通知申请人
- decideAfterSale方法：管理员裁定后通知申请人和开发者

### Requirement: API评价服务集成消息推送
ApiReviewServiceImpl需要在关键业务操作后触发消息推送。

**修改内容**：
- createReview方法：新评价后通知API开发者
- userReplyReview方法：用户追评后通知API开发者
- publisherReplyReview方法：开发者回复后通知评价用户

## REMOVED Requirements

无移除的需求。

---

## 技术设计

### 消息类型枚举
```
REQUIREMENT_NEW_MESSAGE     - 需求新消息
REQUIREMENT_STATUS_UPDATE   - 需求状态更新
AFTER_SALE_NEW_MESSAGE      - 售后新消息
AFTER_SALE_STATUS_UPDATE    - 售后状态更新
API_REVIEW_REPLY            - API评价回复
API_NEW_REVIEW              - API新评论
```

### 数据库设计

#### notification_message 表
| 字段 | 类型 | 说明 |
|-----|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 接收用户ID |
| type | VARCHAR(50) | 消息类型 |
| title | VARCHAR(200) | 消息标题 |
| content | VARCHAR(500) | 消息内容 |
| related_id | BIGINT | 关联业务ID |
| related_type | VARCHAR(50) | 关联业务类型 |
| is_read | TINYINT | 是否已读 0未读 1已读 |
| create_time | DATETIME | 创建时间 |

### 核心接口设计

#### NotificationService（消息服务接口）
```java
void sendNotification(Long userId, NotificationType type, String title, String content, Long relatedId, String relatedType);
void sendNotificationBatch(List<Long> userIds, NotificationType type, String title, String content, Long relatedId, String relatedType);
void markAsRead(Long userId, Long notificationId);
void markAllAsRead(Long userId, NotificationType type);
IPage<NotificationVO> getUnreadList(Long userId, NotificationQueryDTO queryDTO);
IPage<NotificationVO> getAllList(Long userId, NotificationQueryDTO queryDTO);
Long getUnreadCount(Long userId);
```

#### NotificationHandler（消息处理器接口）
```java
void handle(Notification notification);
NotificationVO buildVO(Notification notification);
```

### WebSocket消息格式
```json
{
  "type": "notification",
  "data": {
    "id": 1,
    "type": "AFTER_SALE_NEW_MESSAGE",
    "title": "新消息提醒",
    "content": "您有一条新消息",
    "relatedId": 123,
    "relatedType": "after_sale",
    "createTime": "2024-01-01 12:00:00"
  }
}
```

### 前端交互设计
1. 导航栏图标：显示红点数字徽标
2. 点击图标：弹出消息列表浮层
3. 消息列表：按时间倒序，支持按类型筛选
4. 点击消息：跳转到对应页面并标记已读
