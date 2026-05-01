# 注释可读性审查与修复 Spec

## Why
项目已有中文注释覆盖，但质量参差不齐。后端均分4.0/5、前端均分3.2/5，存在注释语义错误、关键方法零注释、魔法数字无解释等问题，1.4w月薪后端开发者在阅读部分核心代码时仍有理解障碍。

## What Changes
- 修复后端3个低分文件的注释问题（AuthFilter 3分、OrderController 3.5分、OrderVO 3.5分）
- 修复前端3个低分文件的注释问题（types/api.ts 2分、api.ts(API请求) 2.5分、detail.vue 3分）
- 消除前后端共性问题：魔法数字、注释与代码不一致、关键业务逻辑缺行内注释
- 不修改任何业务逻辑代码，仅修复和补充注释

## Impact
- Affected code: 后端6-8个Java文件、前端6-8个Vue/TS文件
- 不影响任何功能，纯注释修复

## ADDED Requirements

### Requirement: 后端核心文件注释修复
系统 SHALL 修复以下后端文件的注释问题：

#### Scenario: AuthFilter.filter() 方法行内注释
- **WHEN** 打开 AuthFilter.java 的 filter 方法
- **THEN** 每个逻辑步骤（白名单判断、请求头提取、AK查询、用户状态校验、nonce校验、timestamp校验、签名校验）上方应有行内注释，与类级注释的5步流程对应

#### Scenario: AuthFilter nonce 校验逻辑说明
- **WHEN** 查看 nonce 校验代码 `Long.parseLong(nonce) > 10000L`
- **THEN** 应有注释说明该校验的业务含义（nonce作为请求唯一标识的范围校验）

#### Scenario: AuthFilter 白名单路径说明
- **WHEN** 查看白名单路径列表
- **THEN** 每个路径应有注释说明为什么不需要鉴权

#### Scenario: OrderController 支付方法 Javadoc
- **WHEN** 打开 payOrder 或 handlePayNotify 方法
- **THEN** 应有完整 Javadoc，说明业务流程、参数含义、返回值含义

#### Scenario: OrderVO.invokeCount 注释语义修正
- **WHEN** 查看 OrderVO 的 invokeCount 字段
- **THEN** 注释应为"购买的调用次数配额（-1表示无限次）"，而非"已调用次数"

#### Scenario: OrderVO 时间字段类型说明
- **WHEN** 查看 OrderVO 的时间字段（createTime/payTime/completeTime）
- **THEN** 注释应说明为什么用 String 类型（ServiceImpl中做了格式化）

#### Scenario: OrderInfoServiceImpl.deleteOrder 注释与代码一致性
- **WHEN** 查看 deleteOrder 方法
- **THEN** 注释应与实际校验逻辑一致（如有校验则说明，如无校验则标注风险）

### Requirement: 前端核心文件注释修复
系统 SHALL 修复以下前端文件的注释问题：

#### Scenario: types/api.ts 字段级注释
- **WHEN** 查看 api.ts 类型定义的任意接口字段
- **THEN** 应有中文注释说明字段含义，枚举值字段应列出所有取值及含义

#### Scenario: api.ts(API请求) 方法级注释
- **WHEN** 查看 api.ts 的任意 API 方法
- **THEN** 应有 JSDoc 注释说明功能和参数，易混淆方法（如 getMyInvokeStatistics vs getMyApiInvokeStatistics）应有区分说明

#### Scenario: detail.vue 关键逻辑注释
- **WHEN** 查看 detail.vue 的折扣计算或审核按钮逻辑
- **THEN** 应有注释说明业务规则（折扣阶梯、审核条件组合原因）

### Requirement: 注释与代码一致性
系统 SHALL 确保所有注释与代码逻辑一致：

#### Scenario: 路由守卫注释修正
- **WHEN** 查看 router/index.ts 的文件级注释
- **THEN** "普通用户访问管理员页面"的结果应与代码一致（跳转首页，非404）

#### Scenario: 限流注释修正
- **WHEN** 查看 OrderController 的 createOrder 方法注释
- **THEN** 如已移除 @RateLimit 注解，注释中不应再提及限流

### Requirement: 魔法数字消除
系统 SHALL 对关键魔法数字添加注释说明：

#### Scenario: replyType 魔法数字
- **WHEN** 查看 ReviewThread.vue 中 replyType === 1 或 replyType === 2
- **THEN** 应有注释说明 1=开发者回复、2=评论者回复

#### Scenario: invokeCount === -1
- **WHEN** 查看订单相关代码中 invokeCount === -1
- **THEN** 应有注释说明 -1 表示无限次调用

#### Scenario: isAdmin 类型不一致
- **WHEN** 查看 isAdmin 相关判断代码
- **THEN** 应有注释说明后端返回 number(0/1) 但前端可能同时存在 boolean 的情况

## MODIFIED Requirements
无

## REMOVED Requirements
无
