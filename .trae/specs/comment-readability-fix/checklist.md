# 注释可读性修复 Checklist

## 后端注释修复检查

- [ ] AuthFilter.filter() 方法的每个逻辑步骤有行内注释
- [ ] AuthFilter nonce 校验逻辑有业务含义注释
- [ ] AuthFilter 白名单路径每个路径有注释说明
- [ ] AuthFilter timestamp 校验的 300 有单位说明（秒/5分钟）
- [ ] AuthFilter handleNoAuth 有 Javadoc 说明返回 401 的原因
- [ ] AuthFilter isWhitePath 有 Javadoc
- [ ] OrderController.payOrder 有完整 Javadoc（业务流程+参数+返回值）
- [ ] OrderController.handlePayNotify 有完整 Javadoc
- [ ] OrderController.createOrder 注释中过时的限流描述已移除
- [ ] OrderVO.invokeCount 注释语义正确（"购买配额"而非"已调用次数"）
- [ ] OrderVO 时间字段有 String 类型原因说明
- [ ] OrderVO review 字段有关联关系说明
- [ ] OrderVO status 枚举值有中文含义
- [ ] OrderInfoServiceImpl.updateOrderStatus 兜底逻辑有注释
- [ ] OrderInfoServiceImpl.deleteOrder 注释与代码一致
- [ ] OrderInfoServiceImpl.convertToVO 的 replyType=0 有说明
- [ ] OrderInfo.invokeCount 有"-1表示无限次"说明
- [ ] OrderInfo.rating 取值范围精确（0.5-5.0步长0.5）
- [ ] ApiInfo.requestParams/responseParams 有 JSON 结构规范说明

## 前端注释修复检查

- [ ] types/api.ts ApiItem 所有字段有中文注释
- [ ] types/api.ts 枚举值字段列出所有取值及中文含义
- [ ] types/api.ts ApiStatistics 的 prev* 字段有时间范围说明
- [ ] types/api.ts ApiListParams.sortBy 有排序维度含义说明
- [ ] api.ts(API请求) 所有方法有 JSDoc 注释
- [ ] api.ts getMyInvokeStatistics vs getMyApiInvokeStatistics 有区分说明
- [ ] api.ts getTypes vs getApiTypes 有区分说明
- [ ] detail.vue getDiscount 有折扣阶梯规则注释
- [ ] detail.vue showAuditButtons 有条件组合原因注释
- [ ] detail.vue purchaseForm.countOption 魔法字符串有注释
- [ ] detail.vue 遗留 console.log 已删除
- [ ] orders.vue 支付宝支付表单提交逻辑有详细注释
- [ ] orders.vue invokeCount === -1 有注释说明
- [ ] ReviewThread.vue replyType 魔法数字有注释
- [ ] router/index.ts 文件级注释与代码一致（跳转首页非404）
- [ ] router/index.ts isAdmin 类型不一致有注释说明
- [ ] request.ts 双成功码有原因注释

## 编译验证检查

- [ ] 后端 `mvn clean compile` 编译通过
- [ ] 前端 `npm run build` 构建通过
