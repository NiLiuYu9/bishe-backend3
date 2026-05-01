# Tasks

## 后端注释修复任务

- [ ] Task 1: 修复 AuthFilter.java 注释（当前评分3/5 → 目标4.5/5）
  - [ ] SubTask 1.1: 为 filter() 方法的每个逻辑步骤添加行内注释（白名单判断、请求头提取、AK查询、用户状态校验、nonce校验、timestamp校验、签名校验）
  - [ ] SubTask 1.2: 为 nonce 校验逻辑 `Long.parseLong(nonce) > 10000L` 添加业务含义注释
  - [ ] SubTask 1.3: 为白名单路径列表每个路径添加注释说明为什么不需要鉴权
  - [ ] SubTask 1.4: 为 timestamp 校验中的 300 添加注释说明单位是秒（5分钟）
  - [ ] SubTask 1.5: 为 handleNoAuth 方法添加 Javadoc，说明为什么返回 401 而非 403
  - [ ] SubTask 1.6: 为 isWhitePath 方法添加 Javadoc

- [ ] Task 2: 修复 OrderController.java 注释（当前评分3.5/5 → 目标4.5/5）
  - [ ] SubTask 2.1: 为 payOrder 方法添加完整 Javadoc（业务流程、参数含义、返回值含义）
  - [ ] SubTask 2.2: 为 handlePayNotify 方法添加完整 Javadoc（回调流程、参数解析逻辑、返回值含义）
  - [ ] SubTask 2.3: 修正 createOrder 方法注释中"限流：10次/分钟"的过时描述（@RateLimit已移除）

- [ ] Task 3: 修复 OrderVO.java 注释（当前评分3.5/5 → 目标4.5/5）
  - [ ] SubTask 3.1: 修正 invokeCount 字段注释语义（"已调用次数"→"购买的调用次数配额（-1表示无限次）"）
  - [ ] SubTask 3.2: 为时间字段（createTime/payTime/completeTime）添加 String 类型原因说明
  - [ ] SubTask 3.3: 为 reviewContent/reviewId 字段添加关联关系说明
  - [ ] SubTask 3.4: 为 status 字段各枚举值添加中文含义

- [ ] Task 4: 修复 OrderInfoServiceImpl.java 注释问题
  - [ ] SubTask 4.1: 为 updateOrderStatus 中"非paid直接变completed"的兜底逻辑添加注释说明
  - [ ] SubTask 4.2: 修正 deleteOrder 方法注释，与实际校验逻辑保持一致
  - [ ] SubTask 4.3: 为 convertToVO 中 replyType=0 添加业务含义注释

- [ ] Task 5: 修复其他后端文件注释问题
  - [ ] SubTask 5.1: OrderInfo.java 的 invokeCount 字段补充"-1表示无限次"说明
  - [ ] SubTask 5.2: OrderInfo.java 的 rating 字段修正取值范围为"0.5-5.0，步长0.5"
  - [ ] SubTask 5.3: ApiInfo.java 的 requestParams/responseParams 补充 JSON 结构规范说明

## 前端注释修复任务

- [ ] Task 6: 修复 types/api.ts 注释（当前评分2/5 → 目标4/5）
  - [ ] SubTask 6.1: 为 ApiItem 接口所有字段添加中文注释（约20个字段）
  - [ ] SubTask 6.2: 为 ApiParam 接口字段添加注释
  - [ ] SubTask 6.3: 为 ApiStatistics 接口字段添加注释（特别是 prev* 字段的时间范围含义）
  - [ ] SubTask 6.4: 为 ApiListParams 字段添加注释（特别是 sortBy 的排序维度含义）
  - [ ] SubTask 6.5: 为枚举值字段（status/priceUnit/method）列出所有取值及中文含义

- [ ] Task 7: 修复 api.ts(API请求) 注释（当前评分2.5/5 → 目标4/5）
  - [ ] SubTask 7.1: 为所有 API 方法添加 JSDoc 注释
  - [ ] SubTask 7.2: 为 getMyInvokeStatistics vs getMyApiInvokeStatistics 添加区分说明
  - [ ] SubTask 7.3: 为 getTypes vs getApiTypes 添加区分说明
  - [ ] SubTask 7.4: 为 apiFavorite.check 方法添加返回值含义说明

- [ ] Task 8: 修复 detail.vue 注释（当前评分3/5 → 目标4/5）
  - [ ] SubTask 8.1: 为 getDiscount 方法添加折扣阶梯规则注释
  - [ ] SubTask 8.2: 为 showAuditButtons 计算属性添加条件组合原因注释
  - [ ] SubTask 8.3: 为 purchaseForm.countOption 的魔法字符串添加注释
  - [ ] SubTask 8.4: 删除遗留的 console.log 调试代码

- [ ] Task 9: 修复其他前端文件注释问题
  - [ ] SubTask 9.1: orders.vue 为支付宝支付表单提交逻辑添加详细注释
  - [ ] SubTask 9.2: orders.vue 为 invokeCount === -1 添加注释说明
  - [ ] SubTask 9.3: ReviewThread.vue 为 replyType 魔法数字添加注释（1=开发者回复，2=评论者回复）
  - [ ] SubTask 9.4: router/index.ts 修正文件级注释（"跳转404"→"跳转首页"）
  - [ ] SubTask 9.5: router/index.ts 为 isAdmin 类型不一致问题添加注释
  - [ ] SubTask 9.6: request.ts 为双成功码（code===0||code===200）添加原因注释

## 验证任务

- [ ] Task 10: 编译验证
  - [ ] SubTask 10.1: 执行 `cd api-platform-backend && mvn clean compile` 确保编译通过
  - [ ] SubTask 10.2: 执行 `cd api-platform-frontend && npm run build` 确保构建通过

# Task Dependencies
- Task 10 依赖 Task 1-9 全部完成
- Task 1-9 之间无依赖，可并行执行
