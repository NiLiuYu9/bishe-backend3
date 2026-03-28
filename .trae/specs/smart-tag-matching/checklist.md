# Checklist

## 数据库
- [x] 用户标签表(user_tag)创建成功，包含id、user_id、tag_name、create_time字段
- [x] 需求标签表(requirement_tag)创建成功，包含id、requirement_id、tag_name、create_time字段

## 后端 - 用户标签
- [x] UserTag实体类创建正确
- [x] UserTagMapper接口创建正确
- [x] UserTagService实现用户标签关联管理
- [x] UserService能正确查询用户标签
- [x] UserTagController提供用户标签管理接口
- [x] 用户可以添加/删除自己的技能标签

## 后端 - 需求标签
- [x] RequirementTag实体类创建正确
- [x] RequirementTagMapper接口创建正确
- [x] RequirementTagService实现需求标签关联管理
- [x] RequirementService创建需求时能正确处理标签
- [x] RequirementService更新需求时能正确处理标签
- [x] RequirementController提供需求标签相关接口
- [x] 查询需求详情时返回标签列表

## 后端 - 智能匹配
- [x] MatchingService实现编辑距离算法
- [x] 编辑距离算法计算正确
- [x] MatchingService实现用户-需求匹配算法
- [x] 匹配度计算正确（对每个用户标签找需求标签中相似度最高的，取平均）
- [x] MatchingController提供智能推荐接口
- [x] 推荐结果按匹配度降序排列
- [x] 推荐接口支持分页

## 前端 - 标签组件
- [x] TagInput组件支持自定义输入标签
- [x] TagInput组件正确显示标签列表
- [x] TagInput组件支持删除标签

## 前端 - API接口
- [x] tag.ts正确封装标签相关API
- [x] API接口与后端Controller对应

## 前端 - 用户资料页面
- [x] profile.vue显示技能标签管理区域
- [x] 用户可以输入自定义技能标签
- [x] 用户可以删除技能标签
- [x] 标签保存后正确显示

## 前端 - 需求发布页面
- [x] my-requirements.vue显示技术标签输入区域
- [x] 发布需求时可以输入自定义标签
- [x] 编辑需求时可以修改标签
- [x] 标签正确保存到后端

## 前端 - 需求列表页面
- [x] list.vue显示智能推荐按钮
- [x] 点击智能推荐显示匹配的需求列表
- [x] 每个需求显示匹配度百分比
- [x] 推荐列表支持分页

## 集成测试
- [x] 用户设置技能标签后，能在需求列表看到智能推荐
- [x] 编辑距离算法匹配准确
- [x] 前后端数据一致
