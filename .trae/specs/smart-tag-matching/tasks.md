# Tasks

## 后端任务

- [x] Task 1: 创建数据库表结构
  - [x] SubTask 1.1: 创建用户标签表(user_tag)
  - [x] SubTask 1.2: 创建需求标签表(requirement_tag)

- [x] Task 2: 创建标签实体和Mapper
  - [x] SubTask 2.1: 创建UserTag实体类
  - [x] SubTask 2.2: 创建RequirementTag实体类
  - [x] SubTask 2.3: 创建UserTagMapper接口
  - [x] SubTask 2.4: 创建RequirementTagMapper接口

- [x] Task 3: 扩展用户标签功能
  - [x] SubTask 3.1: 修改User实体，添加tags字段（非持久化）
  - [x] SubTask 3.2: 创建UserTagService接口和实现类
  - [x] SubTask 3.3: 在UserService中添加用户标签查询方法
  - [x] SubTask 3.4: 创建UserTagController控制器

- [x] Task 4: 扩展需求标签功能
  - [x] SubTask 4.1: 修改Requirement实体，添加tags字段（非持久化）
  - [x] SubTask 4.2: 创建RequirementTagService接口和实现类
  - [x] SubTask 4.3: 修改RequirementService，支持创建/更新需求时处理标签
  - [x] SubTask 4.4: 修改RequirementController，添加标签相关接口

- [x] Task 5: 实现智能匹配算法
  - [x] SubTask 5.1: 创建MatchingService接口和实现类
  - [x] SubTask 5.2: 实现编辑距离算法
  - [x] SubTask 5.3: 实现用户-需求匹配算法
  - [x] SubTask 5.4: 创建MatchingController控制器

## 前端任务

- [x] Task 6: 创建标签输入组件
  - [x] SubTask 6.1: 创建TagInput.vue组件（支持自定义输入和标签显示删除）

- [x] Task 7: 创建标签API接口
  - [x] SubTask 7.1: 创建tag.ts API文件

- [x] Task 8: 修改用户个人资料页面
  - [x] SubTask 8.1: 在profile.vue中添加技能标签管理区域
  - [x] SubTask 8.2: 实现标签输入和保存功能

- [x] Task 9: 修改需求发布页面
  - [x] SubTask 9.1: 在my-requirements.vue中添加技术标签输入区域
  - [x] SubTask 9.2: 实现发布需求时保存标签

- [x] Task 10: 修改需求列表页面
  - [x] SubTask 10.1: 在list.vue中添加智能推荐按钮
  - [x] SubTask 10.2: 实现智能推荐需求列表展示
  - [x] SubTask 10.3: 显示匹配度百分比

# Task Dependencies
- Task 2 依赖 Task 1
- Task 3 依赖 Task 2
- Task 4 依赖 Task 2
- Task 5 依赖 Task 3 和 Task 4
- Task 8 依赖 Task 6 和 Task 7
- Task 9 依赖 Task 6 和 Task 7
- Task 10 依赖 Task 5 和 Task 7
