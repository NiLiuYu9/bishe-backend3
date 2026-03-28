# 支付宝沙箱支付功能 Spec

## Why
当前订单系统采用模拟支付方式，用户点击"立即付款"直接将订单状态改为已完成，缺乏真实的支付流程和支付验证。需要接入支付宝沙箱支付，实现完整的支付闭环，为后续生产环境支付功能奠定基础。

## What Changes
- 后端新增支付宝沙箱配置和支付服务
- 后端新增支付相关接口（支付、支付回调、支付状态查询）
- 前端订单页面集成支付宝支付流程
- 数据库订单表新增支付相关字段

## Impact
- Affected specs: 无
- Affected code:
  - 后端: OrderController, OrderInfoService, OrderInfoServiceImpl, OrderInfo实体, application.yml, pom.xml
  - 前端: orders.vue, trade.ts, trade.ts(types)

## ADDED Requirements

### Requirement: 支付宝沙箱配置
系统SHALL支持支付宝沙箱环境配置，包括应用ID、私钥、支付宝公钥、网关地址等。

#### Scenario: 配置加载成功
- **WHEN** 应用启动时
- **THEN** 支付宝配置正确加载，支付服务可用

### Requirement: 发起支付
系统SHALL支持用户发起支付宝支付请求，生成支付表单或支付链接。

#### Scenario: 发起支付成功
- **WHEN** 用户对待支付订单点击"立即付款"
- **THEN** 系统生成支付宝支付表单，跳转至支付宝沙箱支付页面

#### Scenario: 订单状态校验
- **WHEN** 用户对非待支付状态的订单发起支付
- **THEN** 系统拒绝支付请求，返回错误提示

### Requirement: 支付回调处理
系统SHALL支持接收支付宝异步通知，验证支付结果并更新订单状态。

#### Scenario: 支付成功回调
- **WHEN** 支付宝异步通知支付成功
- **THEN** 系统验证签名通过，更新订单状态为已支付，增加用户API调用配额

#### Scenario: 签名验证失败
- **WHEN** 支付宝回调签名验证失败
- **THEN** 系统记录日志，返回失败响应，不更新订单状态

### Requirement: 支付状态同步查询
系统SHALL支持主动查询支付宝订单支付状态，用于处理回调丢失的情况。

#### Scenario: 主动查询支付状态
- **WHEN** 用户或系统主动查询订单支付状态
- **THEN** 系统调用支付宝查询接口，同步支付结果

### Requirement: 前端支付流程
前端SHALL支持支付宝支付流程，包括支付跳转和支付结果展示。

#### Scenario: 支付跳转
- **WHEN** 用户确认支付
- **THEN** 前端跳转至支付宝支付页面

#### Scenario: 支付结果展示
- **WHEN** 用户支付完成后返回订单页面
- **THEN** 前端显示支付结果，更新订单状态

## MODIFIED Requirements

### Requirement: 订单实体扩展
订单实体SHALL新增支付流水号(payTradeNo)、支付方式(payMethod)字段，用于记录支付宝交易信息。

### Requirement: 订单状态流转
订单状态流转规则修改为：
- pending(待支付) -> paid(已支付): 通过支付宝支付成功
- pending(待支付) -> cancelled(已取消): 用户取消或超时取消
- paid(已支付) -> completed(已完成): 系统自动或手动完成

## REMOVED Requirements
无
