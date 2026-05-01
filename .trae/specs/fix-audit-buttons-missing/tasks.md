# Tasks

- [x] Task 1: 修复 `detail.vue` 中审核按钮显示逻辑
  - [x] SubTask 1.1: 修改 `showAuditButtons` 计算属性，增加对 `isAdmin` 字段类型的兼容处理
  - [x] SubTask 1.2: 添加调试日志，确保三个条件都能正确判断
  - [ ] SubTask 1.3: 测试从仪表盘进入详情页时审核按钮是否正常显示

- [x] Task 2: 在 `apis.vue` 中增加跳转到详情页的功能
  - [x] SubTask 2.1: 修改 `viewApi` 函数，改为跳转到详情页面
  - [x] SubTask 2.2: 确保 `from=admin` 查询参数正确传递
  - [ ] SubTask 2.3: 测试从API管理页面进入详情页时审核按钮是否正常显示

# Task Dependencies
- Task 2 依赖 Task 1 完成
