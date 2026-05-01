# 修复仪表盘进入API详情页审核按钮不显示问题 Spec

## Why
管理员从仪表盘点击"审核"按钮进入API详情页面时，"通过"和"拒绝"的审核按钮不显示，导致无法在详情页面进行审核操作。

## What Changes
- 修复 `detail.vue` 中 `showAuditButtons` 计算属性的判断逻辑
- 确保 `apis.vue` 管理页面也能正确跳转到详情页进行审核

## Impact
- Affected specs: API审核功能
- Affected code: 
  - `api-platform-frontend/src/views/api/detail.vue`
  - `api-platform-frontend/src/views/admin/apis.vue`

## ADDED Requirements

### Requirement: 审核按钮显示条件优化
系统 SHALL 在满足以下条件时显示审核按钮：
1. 当前用户是管理员（`isAdmin === 1`）
2. 从管理后台进入（`route.query.from === 'admin'`）
3. API状态为待审核（`status === 'pending'`）

#### Scenario: 管理员从仪表盘进入待审核API详情页
- **WHEN** 管理员在仪表盘点击待审核API的"审核"按钮
- **THEN** 系统跳转到API详情页，并在用户评价区域上方显示"通过"和"拒绝"按钮

#### Scenario: 管理员从API管理页面进入待审核API详情页
- **WHEN** 管理员在API管理页面点击待审核API的"查看详情"按钮
- **THEN** 系统跳转到API详情页，并在用户评价区域上方显示"通过"和"拒绝"按钮

## Root Cause Analysis

经过代码分析，发现问题可能出在以下几个方面：

### 1. `apis.vue` 缺少跳转到详情页的功能
当前 `apis.vue` 中的 `viewApi` 函数只是打开一个对话框显示API信息，而不是跳转到详情页面：
```javascript
const viewApi = (api: ApiItem) => {
  currentApi.value = api
  apiDialogVisible.value = true
}
```

### 2. `detail.vue` 中的 `showAuditButtons` 逻辑
当前 `showAuditButtons` 计算属性：
```javascript
const showAuditButtons = computed(() => {
  const isAdmin = userStore.userInfo?.isAdmin === 1
  const isFromAdmin = route.query.from === 'admin'
  const isPending = api.value.status === 'pending'
  return isAdmin && isFromAdmin && isPending
})
```

可能的问题：
- `userStore.userInfo` 可能为 `null` 或 `undefined`
- `isAdmin` 字段可能是布尔值 `true` 而不是数字 `1`
- `api.value.status` 可能不是字符串 `'pending'`

## MODIFIED Requirements

### Requirement: API管理页面增加跳转详情页功能
`apis.vue` 页面 SHALL 增加"查看详情"按钮，点击后跳转到API详情页面并带上 `from=admin` 查询参数。

### Requirement: 审核按钮显示逻辑增强
`detail.vue` 中的 `showAuditButtons` 计算属性 SHALL 增加对 `isAdmin` 字段类型的兼容处理，支持布尔值和数字类型的判断。
