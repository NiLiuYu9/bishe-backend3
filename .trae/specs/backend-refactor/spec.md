# 后端代码重构规范

## Why
后端代码存在多处设计缺陷、代码重复、潜在Bug和性能问题，需要进行系统性重构以提高代码质量、可维护性和系统稳定性。

## What Changes
- 统一Session用户获取方式，使用SessionUtils工具类
- 抽取重复的VO转换代码到公共工具类
- 统一异常处理方式
- 修复潜在Bug（空指针、并发安全、权限校验）
- 优化数据库查询性能

## Impact
- Affected specs: 所有Controller、部分Service实现
- Affected code: 
  - controller/*.java (15个文件)
  - service/impl/*.java (部分文件)
  - utils/SessionUtils.java (增强)
  - utils/VoConverterUtils.java (增强)

## ADDED Requirements

### Requirement: Controller职责单一原则
每个Controller应只负责一个业务领域的接口管理。

#### Scenario: 接口归属正确
- **WHEN** 开发者创建新接口
- **THEN** 接口应放置在对应的Controller中，路径与类名匹配

### Requirement: 统一Session用户获取
系统应使用SessionUtils工具类获取当前登录用户信息。

#### Scenario: 获取当前用户ID
- **WHEN** Controller需要获取当前登录用户ID
- **THEN** 应调用SessionUtils.getCurrentUserId(session)方法

### Requirement: 统一异常处理
系统应统一使用BusinessException进行业务异常处理，由GlobalExceptionHandler统一处理返回错误响应。

#### Scenario: 业务异常处理
- **WHEN** 业务逻辑出现异常情况
- **THEN** 应抛出BusinessException，由全局异常处理器统一处理

## MODIFIED Requirements

### Requirement: 统一VO转换
系统应使用VoConverterUtils进行VO转换，避免重复代码。

**修改内容**：
- ApiInvokeController中的convertToApiVO方法移至VoConverterUtils
- TestController中的convertToApiVO方法移至VoConverterUtils
- ApiInvokeController中的convertToApiInfo方法移至VoConverterUtils

## REMOVED Requirements

### Requirement: 冗余的Session检查代码
**Reason**: 已有SessionUtils工具类统一处理
**Migration**: 所有Controller中的Session检查代码替换为SessionUtils调用

---

## 详细问题清单

### 一、代码规范问题

| 文件 | 问题描述 | 修改建议 |
|-----|---------|---------|
| 多个Controller | Session用户获取代码重复 | 使用SessionUtils工具类 |
| ApiInvokeController | convertToApiVO/convertToApiInfo方法重复 | 移至VoConverterUtils |
| TestController | convertToApiVO方法重复 | 移至VoConverterUtils |

### 二、潜在Bug

| 文件 | 行号 | Bug类型 | 问题描述 | 修改建议 |
|-----|------|---------|---------|---------|
| ApiInvokeController | 62 | 空指针风险 | invokeDTO.getApiId()未校验null | 添加参数校验 |
| AccessKeyController | 43-55 | 并发安全 | regenerateAccessKey存在竞态条件 | 使用乐观锁或分布式锁 |
| ApiWhitelistController | 66-89 | 权限缺失 | getWhitelistList未校验操作权限 | 添加权限校验 |
| TestController | 287-297 | 权限缺失 | getRecords未校验记录归属 | 添加用户归属校验 |
| OrderInfoServiceImpl | 103-123 | 状态校验缺失 | updateOrderStatus未校验状态转换合法性 | 添加状态机校验 |

### 三、性能优化建议

| 文件 | 位置 | 问题描述 | 优化建议 |
|-----|------|---------|---------|
| AccessKeyController | 26-33 | 重复查询数据库 | 合并查询或使用缓存 |
| ApiWhitelistController | 66-89 | N+1查询问题 | 批量查询用户信息 |
| RequirementAfterSaleController | 120-137 | 多次查询afterSale | 缓存查询结果 |

### 四、异常处理不一致

| 文件 | 当前处理方式 | 建议处理方式 |
|-----|------------|------------|
| ApiInvokeController | throw BusinessException | 统一使用BusinessException |
| ApiReviewController | try-catch返回Result.failed | 抛出BusinessException |
| ApiWhitelistController | try-catch返回Result.failed | 抛出BusinessException |
| RequirementAfterSaleController | try-catch返回Result.failed | 抛出BusinessException |
| TestController | throw BusinessException | 保持一致 |

### 五、Controller接口位置问题（低优先级）

| 问题接口 | 当前位置 | 建议位置 | 原因 |
|---------|---------|---------|------|
| `/api/statistics/{apiId}` | ApiController | 新建ApiStatisticsController | 统计接口应独立管理 |
| `/api/statistics/my-invoke` | ApiController | 新建ApiStatisticsController | 统计接口应独立管理 |
| `/api/statistics/my-api-invoke` | ApiController | 新建ApiStatisticsController | 统计接口应独立管理 |
| `/invoke/quota/list` | ApiInvokeController | QuotaController | 配额查询应归配额管理 |
| `/invoke/quota/check` | ApiInvokeController | QuotaController | 配额检查应归配额管理 |
