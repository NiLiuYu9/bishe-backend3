# API交易平台 本科毕业论文规划

## 一、论文基本信息

- **论文题目**：基于Spring Boot的API交易平台设计与实现
- **学校**：上海海事大学
- **论文类型**：本科毕业设计（理工类）
- **参考模板**：计算机专业_202010311007_林腾博_毕业大论文.pdf
- **格式规范**：上海海事大学本科毕业论文（设计）撰写规范

---

## 二、论文大纲（三级标题）

### 封面
### 原创性声明
### 版权授权书
### 中文摘要
### 英文摘要
### 目录

---

### 1 引言
#### 1.1 研究背景与意义
#### 1.2 国内外研究现状
#### 1.3 研究内容
#### 1.4 相关技术
##### 1.4.1 后端技术
##### 1.4.2 前端技术
##### 1.4.3 数据库技术
#### 1.5 本章小结

### 2 需求分析
#### 2.1 系统设计目标
#### 2.2 系统需求概述
#### 2.3 系统功能性需求分析
##### 2.3.1 用户管理模块
##### 2.3.2 API管理模块
##### 2.3.3 API调用模块
##### 2.3.4 订单交易模块
##### 2.3.5 需求匹配模块
##### 2.3.6 通知消息模块
##### 2.3.7 平台管理模块
#### 2.4 非功能性需求分析
#### 2.5 本章小结

### 3 系统概要设计
#### 3.1 系统总体架构设计
#### 3.2 系统数据库设计
##### 3.2.1 概念结构设计
##### 3.2.2 逻辑结构设计
##### 3.2.3 数据库表结构
#### 3.3 本章小结

### 4 系统详细设计与实现
#### 4.1 用户管理模块实现
##### 4.1.1 用户注册与登录
##### 4.1.2 AccessKey与SecretKey认证
#### 4.2 API管理模块实现
##### 4.2.1 API发布与审核
##### 4.2.2 API分类管理
##### 4.2.3 API收藏功能
#### 4.3 API调用模块实现
##### 4.3.1 API调用流程
##### 4.3.2 限流机制实现
##### 4.3.3 缓存机制实现
#### 4.4 订单交易模块实现
##### 4.4.1 订单创建与管理
##### 4.4.2 支付宝支付集成
#### 4.5 需求匹配模块实现
##### 4.5.1 需求发布与申请
##### 4.5.2 智能匹配推荐
#### 4.6 通知消息模块实现
##### 4.6.1 WebSocket实时推送
##### 4.6.2 消息管理
#### 4.7 本章小结

### 5 系统测试
#### 5.1 测试环境
#### 5.2 功能测试
#### 5.3 性能测试
#### 5.4 本章小结

### 6 结论与展望
#### 6.1 结论
#### 6.2 展望

### 参考文献
### 致谢
### 附录（可选）

---

## 三、章节-代码证据映射

### 第1章 引言

| 小节 | 内容要点 | 证据来源 |
|------|----------|----------|
| 1.1 研究背景 | API经济发展、数据共享需求 | 行业背景资料 |
| 1.2 国内外现状 | API市场平台、开放平台 | 文献调研 |
| 1.3 研究内容 | 系统功能概述 | 项目整体分析 |
| 1.4.1 后端技术 | Spring Boot、MyBatis Plus、Redis、WebSocket | pom.xml |
| 1.4.2 前端技术 | Vue3、TypeScript、Element Plus | package.json |
| 1.4.3 数据库技术 | MySQL、Redis | pom.xml + entity目录 |

### 第2章 需求分析

| 小节 | 内容要点 | 证据来源 |
|------|----------|----------|
| 2.3.1 用户管理 | 注册、登录、个人中心 | AuthController.java, UserServiceImpl.java |
| 2.3.2 API管理 | API发布、审核、分类 | ApiController.java, ApiInfoServiceImpl.java |
| 2.3.3 API调用 | 调用、限流、配额 | ApiInvokeController.java, RateLimiter.java |
| 2.3.4 订单交易 | 下单、支付、订单管理 | OrderController.java, AlipayServiceImpl.java |
| 2.3.5 需求匹配 | 需求发布、申请、推荐 | RequirementController.java, MatchingServiceImpl.java |
| 2.3.6 通知消息 | 实时通知、消息管理 | NotificationServiceImpl.java, WebSocketServer.java |
| 2.3.7 平台管理 | 用户管理、API审核、统计 | AdminStatisticsController.java |

### 第3章 系统概要设计

| 小节 | 内容要点 | 证据来源 |
|------|----------|----------|
| 3.1 总体架构 | B/S架构、前后端分离 | 项目结构分析 |
| 3.2.1 概念设计 | E-R图实体关系 | entity/*.java |
| 3.2.2 逻辑设计 | 数据库模式 | mapper/*.java |
| 3.2.3 表结构 | 数据库表设计 | api_platform.sql |

**核心实体清单（来自SQL文件）：**
- sys_user（用户表）→ 包含access_key、secret_key字段
- api_info（API信息表）→ 包含状态流转、白名单等字段
- api_type（API分类表）
- order_info（订单表）→ 包含支付信息
- requirement（需求表）
- requirement_applicant（需求申请人表）
- user_api_quota（用户配额表）
- notification_message（通知消息表）
- api_favorite（API收藏表）
- api_whitelist（API白名单表）
- api_review（API评价表）
- api_test_record（API测试记录表）
- api_invoke_daily（API调用每日统计表）
- user_tag（用户标签表）
- requirement_tag（需求标签表）
- requirement_after_sale（需求售后表）
- after_sale_message（售后消息表）

### 第4章 系统详细设计与实现

| 小节 | 内容要点 | 证据来源 |
|------|----------|----------|
| 4.1.1 注册登录 | Session管理、密码加密 | AuthController.java, UserServiceImpl.java |
| 4.1.2 AK/SK认证 | 密钥生成、签名验证 | AccessKeyServiceImpl.java |
| 4.2.1 API发布审核 | 状态流转、审核逻辑 | ApiInfoServiceImpl.java |
| 4.2.2 分类管理 | 分类CRUD | ApiTypeServiceImpl.java |
| 4.2.3 收藏功能 | 收藏/取消收藏 | ApiFavoriteServiceImpl.java |
| 4.3.1 调用流程 | 请求转发、响应处理 | ApiInvokeController.java |
| 4.3.2 限流机制 | 令牌桶算法、Lua脚本 | RateLimiter.java |
| 4.3.3 缓存机制 | Redis缓存、缓存更新 | ApiCacheServiceImpl.java |
| 4.4.1 订单管理 | 订单状态流转 | OrderInfoServiceImpl.java |
| 4.4.2 支付集成 | 支付宝沙箱、回调处理 | AlipayServiceImpl.java |
| 4.5.1 需求发布 | 需求CRUD、申请流程 | RequirementServiceImpl.java |
| 4.5.2 智能匹配 | 编辑距离算法、相似度计算 | MatchingServiceImpl.java |
| 4.6.1 WebSocket | 连接管理、消息推送 | WebSocketServer.java |
| 4.6.2 消息管理 | 消息存储、已读标记 | NotificationServiceImpl.java |

### 第5章 系统测试

| 小节 | 内容要点 | 证据来源 |
|------|----------|----------|
| 5.1 测试环境 | 硬件、软件环境 | 部署配置 |
| 5.2 功能测试 | 各模块测试用例 | 测试截图 |
| 5.3 性能测试 | 接口响应时间、并发测试 | 测试结果 |

---

## 四、图表计划

### 必须绘制的图表

| 图表编号 | 图表名称 | 章节 | 来源依据 |
|----------|----------|------|----------|
| 图3-1 | 系统整体功能模块图 | 3.1 | 项目模块分析 |
| 图3-2 | 系统架构图 | 3.1 | 技术架构分析 |
| 图3-3 | 数据库E-R图 | 3.2.1 | api_platform.sql |
| 图4-1 | 用户注册流程图 | 4.1.1 | AuthController.java |
| 图4-2 | AK/SK认证流程图 | 4.1.2 | AccessKeyServiceImpl.java |
| 图4-3 | API调用流程图 | 4.3.1 | ApiInvokeController.java |
| 图4-4 | 限流机制示意图 | 4.3.2 | RateLimiter.java |
| 图4-5 | 支付流程图 | 4.4.2 | AlipayServiceImpl.java |
| 图4-6 | WebSocket通信示意图 | 4.6.1 | WebSocketServer.java |
| 图4-7 | 智能匹配算法流程图 | 4.5.2 | MatchingServiceImpl.java |

### 数据库表（必须包含，来自SQL文件）

| 表格编号 | 表格名称 | 章节 | 来源依据 |
|----------|----------|------|----------|
| 表3-1 | 用户表(sys_user) | 3.2.3 | api_platform.sql |
| 表3-2 | API信息表(api_info) | 3.2.3 | api_platform.sql |
| 表3-3 | 订单表(order_info) | 3.2.3 | api_platform.sql |
| 表3-4 | 需求表(requirement) | 3.2.3 | api_platform.sql |
| 表3-5 | 用户配额表(user_api_quota) | 3.2.3 | api_platform.sql |
| 表3-6 | 通知消息表(notification_message) | 3.2.3 | api_platform.sql |

### 运行截图计划

| 截图编号 | 截图内容 | 章节 | 说明 |
|----------|----------|------|------|
| 图5-1 | 用户登录页面 | 5.2 | 前端运行截图 |
| 图5-2 | API市场列表页面 | 5.2 | 前端运行截图 |
| 图5-3 | API详情页面 | 5.2 | 前端运行截图 |
| 图5-4 | API测试页面 | 5.2 | 前端运行截图 |
| 图5-5 | 订单创建页面 | 5.2 | 前端运行截图 |
| 图5-6 | 支付页面 | 5.2 | 支付宝沙箱截图 |
| 图5-7 | 需求广场页面 | 5.2 | 前端运行截图 |
| 图5-8 | 管理后台仪表盘 | 5.2 | 前端运行截图 |

---

## 五、禁止声明的内容

以下内容**不得出现**在论文中，因为无法从代码中证实：

1. ❌ 虚构的用户规模数据（如"已有10000+用户"）
2. ❌ 虚构的API调用统计（如"日均调用量100万次"）
3. ❌ 虚构的性能测试数据（需实际运行测试）
4. ❌ 未实现的功能（如"AI智能推荐"、"大数据分析"）
5. ❌ 夸大的创新点（应保守描述为"工程实现"）
6. ❌ 未使用的第三方服务（如云服务器部署细节）

---

## 六、技术亮点（可安全声明）

以下内容有代码证据支持，可以写入论文：

1. ✅ **AK/SK签名认证机制**：AccessKeyServiceImpl.java 实现基于MD5的密钥生成与验证，使用分段锁保证并发安全
2. ✅ **令牌桶限流算法**：RateLimiter.java 使用 Lua 脚本实现分布式限流
3. ✅ **Redis缓存机制**：ApiCacheServiceImpl.java 实现 API 信息缓存与缓存穿透防护
4. ✅ **WebSocket实时通知**：WebSocketServer.java 实现消息推送
5. ✅ **编辑距离相似度匹配**：MatchingServiceImpl.java 实现智能推荐
6. ✅ **支付宝沙箱支付**：AlipayServiceImpl.java 集成支付功能
7. ✅ **API白名单机制**：ApiWhitelistService 实现访问控制
8. ✅ **Session + Redis会话管理**：分布式会话支持

---

## 七、格式要求摘要（上海海事大学规范）

### 正文格式
- 正文字数：理工类专业不少于12000字
- 页边距：上2.54cm、下2.54cm、左3.17cm、右3.17cm
- 纸张：A4纸打印装订

### 标题格式
- 标题字数：不宜超过20个字
- 标题层次：三级标题，可采用"1、1.1、1.1.1"或"一、（一）、1."两种编号方法
- 第一级标题：居中书写
- 第二级标题：序号顶格书写，空一格接写标题名
- 第三级标题：空两格书写序号，空一格书写标题名

### 摘要格式
- 中文摘要：300字左右
- 关键词：3-5个
- 需有英文摘要

### 参考文献格式
- 主要参考文献：不少于10篇
- 外文文献：不少于2篇
- 格式：按国家标准GB/T 7714-2015规定
- 序码：用方括号括起

### 图表格式
- 图序和图题：放在图位下方居中处
- 表序和表题：写在表格上方居中处
- 全文图表采用逐章编序

---

## 八、参考文献示例（参考学长论文格式）

以下文献格式参考学长论文，可根据实际引用调整：

```
[1] Spring Boot框架基础介绍 - 阿里云开发者社区 https://developer.aliyun.com/article/1213775 访问日期：2024年X月X日.
[2] mybatis – MyBatis 3 | 简介 https://mybatis.org/mybatis-3/zh_CN/index.html 访问时间：2024年X月X日.
[3] 简介 | Vue.js https://cn.vuejs.org/guide/introduction.html 访问日期：2024年X月X日.
[4] MySQL数据库简介-阿里云开发者社区 https://developer.aliyun.com/article/873542 访问日期：2024年X月X日.
[5] Redis官方文档 https://redis.io/docs/ 访问日期：2024年X月X日.
[6] WebSocket - MDN Web Docs https://developer.mozilla.org/zh-CN/docs/Web/API/WebSocket 访问日期：2024年X月X日.
[7] Element Plus - 基于 Vue 3.0 的组件库 https://element-plus.org/zh-CN/ 访问日期：2024年X月X日.
[8] 支付宝开放平台 - 沙箱环境 https://opendocs.alipay.com/common/02kkv7 访问日期：2024年X月X日.
[9] J. Arthur and S. Azadegan, "Spring framework for rapid open source J2EE Web application development: a case study," Sixth International Conference on Software Engineering, Artificial Intelligence, Networking and Parallel/Distributed Computing, Towson, MD, USA, 2005, pp. 90-95.
[10] MyBatis-Plus官方文档 https://baomidou.com/ 访问日期：2024年X月X日.
```

---

## 九、Handoff 包（给 academic-paper-composer）

### 输入文件清单
```
项目根目录: C:\Users\24551\Desktop\毕设项目
├── api-platform-backend/          # 后端代码
│   ├── pom.xml                    # Maven配置
│   ├── src/main/resources/api_platform.sql  # 数据库结构
│   └── src/main/java/com/api/platform/
│       ├── controller/            # 控制器层
│       ├── service/impl/          # 业务层实现
│       ├── entity/                # 实体类
│       ├── mapper/                # 数据访问层
│       ├── config/                # 配置类
│       ├── ratelimit/             # 限流组件
│       └── websocket/             # WebSocket
├── api-platform-frontend/         # 前端代码
│   ├── package.json               # 依赖配置
│   └── src/
│       ├── views/                 # 页面组件
│       ├── router/                # 路由配置
│       └── stores/                # 状态管理
├── 计算机专业_202010311007_林腾博_毕业大论文.pdf  # 参考模板
└── 毕业论文格式规范/              # 格式规范文件
    └── 附件2：上海海事大学本科毕业论文（设计）撰写规范.docx
```

### 输出要求
- 格式：Word文档（.docx）
- 遵循：上海海事大学本科论文格式
- 语言：中文
- 字数：正文不少于12000字
- 页数：预计60-80页

### 章节编写优先级
1. **高优先级**：第3章（概要设计）、第4章（详细实现）
2. **中优先级**：第2章（需求分析）、第5章（测试）
3. **低优先级**：第1章（引言）、第6章（结论）

### 特殊要求
1. 代码片段需从实际文件提取，不得虚构
2. 图表需有明确的数据来源
3. 数据库表结构需与 api_platform.sql 一致
4. 功能描述需与 controller/service 代码一致
5. 参考文献需引用真实存在的文献或网页

---

## 十、下一步行动

1. **启动 academic-paper-composer** 生成论文初稿
2. **启动 drawio** 绘制系统架构图、E-R图、流程图
3. **运行系统** 截取功能截图
4. **人工审阅** 确保内容准确

---

*规划生成时间：2026-03-29*
*规划工具：academic-paper-strategist*
*学校：上海海事大学*
