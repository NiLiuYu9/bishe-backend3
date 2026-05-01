# 7天模拟测试数据生成 Spec

## Why
用户需要以 userid=1 的身份测试平台所有功能，需要7天的模拟测试数据覆盖各个业务模块，确保每个功能页面都有数据可展示和操作。

## What Changes
- 新增 `insert_test_data.sql` 文件，插入7天（2026-04-02 ~ 2026-04-08）的模拟测试数据
- 新增 `delete_test_data.sql` 文件，删除插入的测试数据
- 测试数据使用固定ID范围，确保可精确删除

## Impact
- Affected code: 项目根目录下新增2个SQL文件，不修改任何现有代码
- Affected database: 向以下表插入测试数据（使用ID范围避免与现有数据冲突）

## 数据覆盖范围

### 1. 订单数据 (order_info)
- 覆盖所有5种状态：pending / paid / completed / refunded / cancelled
- 用户1作为买家购买不同API
- ID范围：9001-9020

### 2. API调用每日统计 (api_invoke_daily)
- 7天内用户1调用多个API的每日统计
- 包含成功和失败次数
- ID范围：9001-9070

### 3. API测试记录 (api_test_record)
- 7天内用户1的API测试记录
- 包含自动(0)和手动(1)两种类型
- 包含成功和失败记录
- ID范围：9001-9030

### 4. API评价 (api_review)
- 对已完成订单的API评价
- 包含原评论(0)、上架者回复(1)、评论者回复(2)
- 不同评分等级
- ID范围：9001-9020

### 5. API收藏 (api_favorite)
- 用户1收藏多个API
- ID范围：9001-9010

### 6. API白名单 (api_whitelist)
- 用户1在启用白名单的API中的白名单记录
- ID范围：9001-9005

### 7. 用户API配额 (user_api_quota)
- 用户1购买API后的配额数据
- 包含已用完、部分使用、未使用等状态
- ID范围：9001-9015

### 8. 需求数据 (requirement)
- 覆盖所有5种状态：open / in_progress / delivered / completed / cancelled
- 用户1作为需求发布方
- ID范围：9001-9010

### 9. 需求申请人 (requirement_applicant)
- 对用户1发布的需求的申请
- ID范围：9001-9020

### 10. 需求标签 (requirement_tag)
- 需求关联的技术标签
- ID范围：9001-9030

### 11. 售后数据 (requirement_after_sale)
- 覆盖所有3种状态：pending / resolved / rejected
- 包含result: completed / refunded
- ID范围：9001-9005

### 12. 售后对话 (after_sale_message)
- 售后记录中的对话消息
- 包含applicant / developer / admin三种发送者类型
- ID范围：9001-9020

### 13. 通知消息 (notification_message)
- 用户1收到的各类通知
- 包含已读和未读
- ID范围：9001-9030

### 14. 用户标签 (user_tag)
- 用户1的技能标签
- ID范围：9001-9010

## ADDED Requirements

### Requirement: 插入测试数据SQL
系统 SHALL 提供 `insert_test_data.sql` 文件，包含7天模拟测试数据的INSERT语句，覆盖上述14张表的所有业务场景。

#### Scenario: 执行插入SQL
- **WHEN** 用户在MySQL中执行 `insert_test_data.sql`
- **THEN** 所有14张表中插入测试数据，数据时间范围为 2026-04-02 ~ 2026-04-08
- **AND** 所有测试数据ID使用9001以上的范围，不与现有数据冲突

### Requirement: 删除测试数据SQL
系统 SHALL 提供 `delete_test_data.sql` 文件，精确删除 `insert_test_data.sql` 插入的所有测试数据。

#### Scenario: 执行删除SQL
- **WHEN** 用户在MySQL中执行 `delete_test_data.sql`
- **THEN** 所有通过 `insert_test_data.sql` 插入的测试数据被精确删除
- **AND** 不影响任何原有数据

### Requirement: 数据业务完整性
测试数据 SHALL 满足以下业务完整性约束：

#### Scenario: 订单与评价关联
- 已完成(completed)状态的订单有关联评价
- 评价的order_id指向真实存在的订单

#### Scenario: 订单与配额关联
- paid/completed状态的订单有关联的配额记录
- 配额的total_count与订单的invoke_count一致

#### Scenario: 需求与申请人关联
- in_progress/delivered/completed状态的需求有对应的申请人
- 需求标签与需求ID正确关联

#### Scenario: 售后与需求关联
- 售后记录的requirement_id指向真实存在的需求
- 售后对话的after_sale_id指向真实存在的售后记录

#### Scenario: 调用统计与API关联
- api_invoke_daily中的api_id和api_name与api_info表一致
- api_owner_id与api_info中的user_id一致

## 数据设计原则

1. **时间分布**：7天数据均匀分布，每天有不同类型的活动
2. **状态覆盖**：每个业务实体的所有状态都有对应数据
3. **关联完整**：外键关联的数据保持一致
4. **ID安全**：使用9001+的ID范围，避免AUTO_INCREMENT冲突
5. **可逆删除**：删除SQL按依赖反序执行，先删子表后删主表
