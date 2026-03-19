# Tasks

## 高优先级任务（影响功能/安全）

- [x] Task 1: 统一Session用户获取方式
  - [x] SubTask 1.1: 增强SessionUtils工具类，添加getCurrentUser方法
  - [x] SubTask 1.2: 重构AccessKeyController，使用SessionUtils
  - [x] SubTask 1.3: 重构ApiController，使用SessionUtils
  - [x] SubTask 1.4: 重构ApiFavoriteController，使用SessionUtils
  - [x] SubTask 1.5: 重构ApiReviewController，使用SessionUtils
  - [x] SubTask 1.6: 重构ApiWhitelistController，使用SessionUtils
  - [x] SubTask 1.7: 重构OrderController，使用SessionUtils
  - [x] SubTask 1.8: 重构QuotaController，使用SessionUtils
  - [x] SubTask 1.9: 重构RequirementController，使用SessionUtils
  - [x] SubTask 1.10: 重构RequirementAfterSaleController，使用SessionUtils
  - [x] SubTask 1.11: 重构TestController，使用SessionUtils

- [x] Task 2: 统一异常处理方式
  - [x] SubTask 2.1: 重构ApiReviewController，移除try-catch，使用BusinessException
  - [x] SubTask 2.2: 重构ApiWhitelistController，移除try-catch，使用BusinessException
  - [x] SubTask 2.3: 重构RequirementAfterSaleController，移除try-catch，使用BusinessException
  - [x] SubTask 2.4: 重构OrderController，移除try-catch，使用BusinessException

- [x] Task 3: 修复潜在Bug
  - [x] SubTask 3.1: ApiInvokeController添加参数校验
  - [x] SubTask 3.2: AccessKeyController修复regenerateAccessKey并发安全问题
  - [x] SubTask 3.3: ApiWhitelistController添加权限校验（已在Service层实现）
  - [x] SubTask 3.4: TestController添加记录归属校验（已在Service层实现）
  - [x] SubTask 3.5: OrderInfoServiceImpl添加状态转换校验

## 中优先级任务（影响性能/规范）

- [x] Task 4: 抽取重复VO转换代码
  - [x] SubTask 4.1: 增强VoConverterUtils，添加convertToApiInfo方法
  - [x] SubTask 4.2: 重构ApiInvokeController，使用VoConverterUtils
  - [x] SubTask 4.3: 重构TestController，使用VoConverterUtils

- [x] Task 5: 性能优化
  - [x] SubTask 5.1: AccessKeyController优化重复数据库查询
  - [x] SubTask 5.2: ApiWhitelistController优化N+1查询问题（已使用批量查询）
  - [x] SubTask 5.3: RequirementAfterSaleController优化重复查询

## 低优先级任务（优化建议）

- [x] Task 6: Controller接口位置调整
  - [x] SubTask 6.1: 将/api/statistics相关接口移至新的ApiStatisticsController
  - [x] SubTask 6.2: 将/invoke/quota相关接口保留在ApiInvokeController（使用accessKey认证，与Session认证不同）
  - [x] SubTask 6.3: 更新WebMvcConfig拦截器配置（已配置/api/statistics/**排除）

# Task Dependencies

- [Task 2] 依赖 [Task 1] - 异常处理重构需要先统一Session获取方式
- [Task 4] 可以与 [Task 1] 并行执行
- [Task 5] 可以与 [Task 1] 并行执行
- [Task 6] 需要在 [Task 1-5] 完成后执行，因为涉及接口路径变更
