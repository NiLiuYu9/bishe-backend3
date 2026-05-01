# Tasks

- [x] Task 1: 创建 insert_test_data.sql 文件
  - [x] 1.1 编写订单数据(order_info) INSERT，覆盖5种状态，ID 9001-9020
  - [x] 1.2 编写API调用每日统计(api_invoke_daily) INSERT，7天×多API，ID 9001-9070
  - [x] 1.3 编写API测试记录(api_test_record) INSERT，含自动/手动类型，ID 9001-9030
  - [x] 1.4 编写API评价(api_review) INSERT，含嵌套回复，ID 9001-9020
  - [x] 1.5 编写API收藏(api_favorite) INSERT，ID 9001-9010
  - [x] 1.6 编写API白名单(api_whitelist) INSERT，ID 9001-9004
  - [x] 1.7 编写用户API配额(user_api_quota) INSERT，ID 9001-9015
  - [x] 1.8 编写需求数据(requirement) INSERT，覆盖5种状态，ID 9001-9010
  - [x] 1.9 编写需求申请人(requirement_applicant) INSERT，ID 9001-9020
  - [x] 1.10 编写需求标签(requirement_tag) INSERT，ID 9001-9030
  - [x] 1.11 编写售后数据(requirement_after_sale) INSERT，覆盖3种状态，ID 9001-9005
  - [x] 1.12 编写售后对话(after_sale_message) INSERT，含3种发送者类型，ID 9001-9020
  - [x] 1.13 编写通知消息(notification_message) INSERT，含已读/未读，ID 9001-9030
  - [x] 1.14 编写用户标签(user_tag) INSERT，ID 9001-9010

- [x] Task 2: 创建 delete_test_data.sql 文件
  - [x] 2.1 按依赖反序编写DELETE语句，精确删除ID 9001+范围的测试数据

# Task Dependencies
- Task 2 depends on Task 1 (需要知道插入的ID范围才能编写删除语句)
