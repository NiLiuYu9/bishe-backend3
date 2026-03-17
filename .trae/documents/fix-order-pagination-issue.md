# 前端订单管理页面分页切换问题分析与修复计划

## 问题定位

经过代码分析，发现 **管理员订单管理页面 (`admin/orders.vue`)** 的分页切换存在 bug。

### 问题代码位置

文件：[admin/orders.vue](file:///c:/Users/24551/Desktop/毕设项目/api-platform-frontend/src/views/admin/orders.vue)

### 问题原因

**缺少分页参数的 watch 监听**

在 `admin/orders.vue` 中：
- 第49-56行：分页组件绑定了 `v-model:current-page` 和 `v-model:page-size`
- 但是**没有**使用 `watch` 监听这些值的变化
- 只有在 `handleSearch` 和 `resetFilters` 函数中才会调用 `fetchOrders()`

这意味着：**当用户点击分页切换时，虽然 `pagination.page` 值变了，但不会触发数据刷新！**

### 对比正确实现

文件：[user/orders.vue](file:///c:/Users/24551/Desktop/毕设项目/api-platform-frontend/src/views/user/orders.vue)

用户订单页面有正确的 watch 监听（第138-145行）：
```typescript
watch(() => pagination.page, () => {
  fetchOrders()
})

watch(() => pagination.pageSize, () => {
  pagination.page = 1
  fetchOrders()
})
```

## 修复方案

在 `admin/orders.vue` 中添加 watch 监听，监听分页参数变化并自动刷新数据。

### 具体修改

1. 在 import 语句中添加 `watch`（第77行已有，无需修改）

2. 在 `resetFilters` 函数后添加 watch 监听代码：
```typescript
watch(() => pagination.page, () => {
  fetchOrders()
})

watch(() => pagination.pageSize, () => {
  pagination.page = 1
  fetchOrders()
})
```

## 验证方法

修复后验证：
1. 打开管理员订单管理页面
2. 点击分页的下一页/上一页按钮，确认数据能正确刷新
3. 修改每页显示条数，确认数据能正确刷新并重置到第一页
4. 结合筛选条件测试分页功能
