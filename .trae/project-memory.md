# API 开放平台 - 项目记忆文档

> 本文档用于每次新任务时快速定位项目信息，每次代码改动后需同步更新。

---

## ⚠️ 重要：项目目录定位

**前端项目不在 Git 仓库根目录下，容易被遗漏！**

| 模块 | 绝对路径 | 说明 |
|------|---------|------|
| 后端 | `C:\Users\24551\Desktop\毕设项目\api-platform-backend` | 主后端服务 |
| **前端** | **`C:\Users\24551\Desktop\毕设项目\api-platform-frontend`** | **Vue3 前端项目，独立目录，非 Git 子目录** |
| 云模块 | `C:\Users\24551\Desktop\毕设项目\api-platform-cloud` | 微服务模块 |

> **为什么每次新对话找不到前端？** 前端项目 `api-platform-frontend` 虽然在毕设项目目录下，但它不在后端项目的 Git 仓库中（后端 `.gitignore` 未包含它），且 Glob 搜索时可能因文件数量限制被截断。**每次新任务务必使用绝对路径 `C:\Users\24551\Desktop\毕设项目\api-platform-frontend` 直接访问前端目录。**

---

## 一、项目概述

**项目名称**：基于 Spring Boot 的 API 开放平台（API Marketplace）
**项目定位**：API 开发者上架接口、调用者购买并调用 API、需求方发布定制需求、开发者接单交付的完整 API 交易与定制化开发市场
**Java 版本**：1.8
**Spring Boot 版本**：2.7.18

---

## 二、目录结构速查

```
毕设项目/
├── api-platform-backend/          # 主后端服务（端口 8080，context-path: /api）
│   └── src/main/java/com/api/platform/
│       ├── annotation/            # 自定义注解（@RateLimit）
│       ├── common/                # 统一响应封装（Result, ResultCode）
│       ├── config/                # 配置类（Redis, MybatisPlus, WebSocket, 支付宝等）
│       ├── constants/             # 常量类
│       ├── controller/            # 控制器层（17个Controller）
│       ├── dto/                   # 数据传输对象
│       ├── entity/                # 数据库实体（17个Entity）
│       ├── exception/             # 异常处理（BusinessException, GlobalExceptionHandler）
│       ├── interceptor/           # 拦截器（Session, RateLimit）
│       ├── mapper/                # MyBatis-Plus Mapper接口
│       ├── ratelimit/             # 限流器实现
│       ├── service/               # 服务层
│       │   ├── dubbo/             # Dubbo内部服务实现（3个）
│       │   └── impl/              # 业务服务实现（21个）
│       ├── utils/                 # 工具类（SessionUtils, VoConverterUtils）
│       ├── vo/                    # 视图对象
│       └── websocket/             # WebSocket服务
│   └── src/main/resources/
│       ├── mapper/                # Mapper XML（9个，仅3个有复杂SQL）
│       ├── migration/             # 数据库迁移SQL
│       ├── api_platform.sql       # 完整建库脚本
│       └── application.yml        # 应用配置
│
├── api-platform-frontend/         # ⭐ 前端项目（端口 3000，独立目录）
│   └── src/
│       ├── api/                   # API 请求模块（13个，按业务分模块）
│       ├── components/            # 公共组件（9个 + statistics子组件3个）
│       ├── config/                # 配置（baseURL、apiEndpoints）
│       ├── layouts/               # 布局组件（3个：Main/Admin/User）
│       ├── router/                # 路由配置（含守卫）
│       ├── stores/                # Pinia 状态管理（3个store）
│       ├── types/                 # TypeScript 类型定义（7个文件）
│       ├── utils/                 # 工具函数（request/websocket/format/status）
│       ├── views/                 # 页面视图
│       │   ├── admin/             # 管理后台页面（8个）
│       │   ├── api/               # API相关页面（4个）
│       │   ├── auth/              # 登录注册页面（2个）
│       │   ├── error/             # 错误页面（404）
│       │   ├── home/              # 首页
│       │   ├── requirement/       # 需求页面（2个）
│       │   └── user/              # 用户中心页面（7个）
│       ├── App.vue
│       ├── main.ts
│       └── style.css
│   └── package.json / vite.config.ts / tsconfig.json
│
├── api-platform-cloud/            # 微服务模块
│   ├── api-platform-common/       # 公共模块（常量/DTO/VO/Dubbo接口/签名工具）
│   ├── api-platform-gateway/      # API网关（端口 9000，6个过滤器）
│   ├── api-platform-mock-api/     # 模拟API服务1（端口 8081，19个Mock接口：图像6/文本8/语音5）
│   ├── api-platform-mock-api-2/   # 模拟API服务2（端口 8082，20个Mock接口：数据6/地图5/支付4/短信5）
│   ├── api-platform-mock-api-3/   # 模拟API服务3（端口 8083，人脸6/OCR8/翻译7=21个Mock接口）
│   └── api-platform-client-sdk/   # 客户端SDK（ApiClient + SignUtils）
│
└── .trae/                         # Trae IDE 配置
    ├── documents/                 # 文档
    ├── rules/                     # 项目规则
    ├── specs/                     # 功能规格说明（6个已完成spec）
    └── project-memory.md          # 本文件
```

---

## 三、技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 2.7.18 |
| 微服务 | Spring Cloud | 2021.0.8 |
| 微服务 | Spring Cloud Alibaba | 2021.0.5.0 |
| RPC | Apache Dubbo | 3.0.9 |
| 注册中心 | Nacos | localhost:8848 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.0 |
| 连接池 | Druid | 1.2.8 |
| 缓存 | Redis | localhost:6379 |
| 会话 | Spring Session + Redis | - |
| 网关 | Spring Cloud Gateway | - |
| 支付 | 支付宝沙箱 SDK | 4.38.157.ALL |
| 工具 | Hutool | 5.8.25 |
| 工具 | Lombok | 1.18.20 |
| 导出 | Apache POI | 5.2.3 |
| **前端框架** | **Vue 3** | **3.x（Composition API）** |
| **前端构建** | **Vite** | **7.3.1** |
| **前端语言** | **TypeScript** | **5.9.3** |
| **UI 库** | **Element Plus** | **2.13.3** |
| **状态管理** | **Pinia** | **3.0.4** |
| **路由** | **Vue Router** | **4.6.4** |
| **HTTP** | **Axios** | **1.13.6** |
| **图表** | **ECharts** | **6.0.0** |

---

## 四、核心业务模块与 Controller 路由

| 模块 | Controller | 路由前缀 | 核心功能 |
|------|-----------|---------|---------|
| 用户认证 | AuthController | `/auth` | 注册/登录/登出/用户信息/改密 |
| API管理 | ApiController | `/api` | API CRUD/上下架/分类查询 |
| API调用 | ApiInvokeController | `/invoke` | AK/SK调用/配额查询 |
| API测试 | TestController | `/test` | 在线测试/记录管理 |
| 订单 | OrderController | `/order` | 下单/支付/评分 |
| 评价 | ApiReviewController | `/review` | 评价/嵌套回复 |
| 需求 | RequirementController | `/requirement` | 需求发布/申请/接单/交付 |
| 售后 | RequirementAfterSaleController | `/requirement/after-sale` | 售后申请/裁定/对话 |
| 管理 | ManagerController | `/admin` | 用户/API/订单/需求管理 |
| 统计 | AdminStatisticsController | `/admin` | 平台统计 |
| API统计 | ApiStatisticsController | `/api/statistics` | 调用统计 |
| 密钥 | AccessKeyController | `/user/accessKey` | AK/SK获取/重新生成 |
| 收藏 | ApiFavoriteController | `/favorite` | 收藏/取消/列表 |
| 白名单 | ApiWhitelistController | `/whitelist` | 白名单增删/启停 |
| 通知 | NotificationController | `/notification` | 未读/列表/标记已读 |
| 配额 | QuotaController | `/quota` | 配额列表 |
| 标签 | UserTagController | `/user-tag` | 用户标签管理 |
| 匹配 | MatchingController | `/matching` | 智能推荐 |
| 内部鉴权 | InternalAuthController | `/internal/auth` | 网关调用AK/SK校验 |
| 图像API | ImageApiController | `/api/v1/image` | 识别/分类/质量/风格/增强/水印（6个） |
| 文本API | TextApiController | `/api/v1/text` | 情感/关键词/摘要/分类/NER/纠错/相似度/过滤（8个） |
| 语音API | VoiceApiController | `/api/v1/voice` | 转写/合成/声纹/情感/降噪（5个） |
| 人脸API | FaceApiController | `/api/v1/face` | 检测/比对/搜索/活体/属性/注册（6个） |
| OCR API | OcrApiController | `/api/v1/ocr` | 通用/身份证/银行卡/驾驶证/行驶证/营业执照/车牌/发票（8个） |
| 翻译API | TranslateApiController | `/api/v1/translate` | 通用/文档/图片/语音/专业/批量/实时（7个） |

---

## 五、数据库表速查（17张表，来源：api_platform.sql）

| 表名 | 说明 | 关键字段 | AUTO_INCREMENT |
|------|------|---------|----------------|
| `sys_user` | 用户表 | id, username(唯一), password, email, phone, is_admin, access_key, secret_key, status(0禁/1启), freeze_reason | 102 |
| `api_info` | API信息表 | id, type_id, user_id, name, method(GET/POST/PUT/DELETE), endpoint, target_url, request_params(json), response_params(json), price, price_unit, call_limit, whitelist_enabled, status(pending/approved/rejected/offline), rating, invoke_count, success_count, fail_count | 61 |
| `api_type` | API分类表 | id, name(唯一), description | 11 |
| `api_favorite` | API收藏表 | id, user_id, api_id（联合唯一uk_user_api） | 101 |
| `api_whitelist` | API白名单表 | id, api_id, user_id（联合唯一uk_api_user） | 16 |
| `api_review` | API评价表 | id, order_id, api_id, user_id, rating(0.5-5.0), content, reply, reply_time, parent_id, reply_type(0原评论/1上架者回复/2评论者回复) | 61 |
| `api_invoke_daily` | 每日调用统计表 | id, api_id, api_name, caller_id, api_owner_id, stat_date（联合唯一uk_api_caller_date）, total_count, success_count, fail_count | 295 |
| `api_test_record` | 测试记录表 | id, api_id, api_name, user_id, params(json), result(json), success, error_msg, response_time, status_code, type(0自动/1手动) | 101 |
| `order_info` | 订单表 | id, order_no(唯一), api_id, api_name, buyer_id, buyer_name, invoke_count(-1无限), price, status(pending/paid/completed/refunded/cancelled), pay_trade_no, pay_method, pay_time, complete_time, rating | 151 |
| `requirement` | 需求表 | id, user_id, title, description, request_params(text), response_params(text), budget, deadline, status(open/in_progress/delivered/completed/cancelled), delivery_url | 31 |
| `requirement_applicant` | 需求申请人表 | id, requirement_id, user_id, description, status, apply_time | 46 |
| `requirement_tag` | 需求技术标签表 | id, requirement_id, tag_name | 61 |
| `requirement_after_sale` | 需求售后表 | id, requirement_id, applicant_id, developer_id, reason, unimplemented_features, developer_response, developer_response_time, admin_id, admin_decision, admin_decision_time, status(pending/resolved/rejected), result(completed/refunded) | 10 |
| `after_sale_message` | 售后对话记录表 | id, after_sale_id, sender_id, sender_type(applicant/developer/admin), content | 33 |
| `notification_message` | 通知消息表 | id, user_id, type, title, content, related_id, related_type, is_read(0未读/1已读) | 65 |
| `user_api_quota` | 用户API配额表 | id, user_id, api_id（联合唯一uk_user_api）, total_count, used_count, remaining_count | 121 |
| `user_tag` | 用户技能标签表 | id, user_id, tag_name | 151 |

> **注意**：api_platform.sql 为完整建库 DDL（含索引定义），位于 `api-platform-backend/src/main/resources/api_platform.sql`。api_info.sql 包含 60 条模拟 API 数据，位于项目根目录。

---

## 六、核心状态流转

### API 状态
`pending` → `approved` / `rejected` → `offline`

### 订单状态
`pending` → `paid` → `completed` / `refunded` / `cancelled`

### 需求状态
`open` → `in_progress` → `delivered` → `completed` / `cancelled`

### 售后状态
`pending` → `resolved` / `rejected`（result: `completed` / `refunded`）

---

## 七、网关过滤器链（按执行顺序）

| Order | 过滤器 | 功能 |
|-------|--------|------|
| -1 | AccessControlFilter | IP 白名单控制 |
| 0 | AuthFilter | AK/SK 鉴权（nonce/时间戳/签名校验） |
| 1 | InterfaceValidateFilter | 接口存在性/审核状态/配额校验 |
| 2 | DynamicRouteFilter | 动态路由到目标URL |
| 2 | RateLimitFilter | 令牌桶限流（Redis + Lua，固定capacity=2/refillRate=2，未识别用户返回401） |
| 2 | ResponseLogFilter | 响应日志 & 调用次数更新 |

---

## 八、关键技术实现

| 功能 | 实现方式 | 关键文件 |
|------|---------|---------|
| AK/SK签名 | SHA256(body + "." + secretKey) | common/utils/SignUtils.java |
| 限流 | @RateLimit注解 + 令牌桶(Redis+Lua) | annotation/RateLimit.java, ratelimit/RateLimiter.java |
| 缓存 | Redis API详情缓存 + 空值缓存防穿透 | service/impl/ApiCacheServiceImpl.java |
| 缓存预热 | ApplicationRunner启动时加载 | config/CacheWarmUpRunner.java |
| 智能匹配 | Levenshtein编辑距离算法 | service/impl/MatchingServiceImpl.java |
| 支付 | 支付宝沙箱（创建/回调/查询） | service/impl/AlipayServiceImpl.java |
| 实时通知 | WebSocket + 站内消息 | websocket/WebSocketServer.java |
| 会话管理 | Spring Session + Redis | config/WebMvcConfig.java, interceptor/SessionInterceptor.java |
| 嵌套评价 | parentId + replyType(0/1/2) | entity/ApiReview.java |
| 统计同步 | 定时任务 Redis→MySQL | service/impl/StatisticsSyncServiceImpl.java |
| 用户导出 | Apache POI Excel | ManagerController |

---

## 九、Dubbo 内部服务

| 服务接口 | 实现类 | 功能 |
|---------|--------|------|
| InnerUserService | InnerUserServiceImpl | 按accessKey查询用户 |
| InnerInterfaceInfoService | InnerInterfaceInfoServiceImpl | 按路径/ID查询接口信息 |
| InnerUserInterfaceInfoService | InnerUserInterfaceInfoServiceImpl | 更新调用次数/检查配额 |

---

## 十二、前端项目详情

### 12.1 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.x | 前端框架（Composition API + `<script setup>`） |
| TypeScript | 5.9.3 | 类型安全 |
| Vite | 7.3.1 | 构建工具 |
| Element Plus | 2.13.3 | UI 组件库 |
| Pinia | 3.0.4 | 状态管理 |
| Vue Router | 4.6.4 | 路由 |
| Axios | 1.13.6 | HTTP 请求 |
| ECharts | 6.0.0 | 数据可视化图表 |

### 12.2 前端配置

- **开发端口**：3000（vite.config.ts）
- **API 基地址**：`http://localhost:8080/api`（src/config/index.ts）
- **路径别名**：`@` → `src/`
- **请求封装**：Axios 实例 + 拦截器（src/utils/request.ts），withCredentials: true
- **响应约定**：code === 0 或 200 为成功，其他弹 ElMessage.error

### 12.3 前端路由结构

| 路由路径 | 页面 | 布局 | 需要登录 | 需要管理员 |
|---------|------|------|---------|-----------|
| `/` | 首页 | MainLayout | 否 | 否 |
| `/api` | API市场 | MainLayout | 否 | 否 |
| `/api/:id` | API详情 | MainLayout | 否 | 否 |
| `/api/test/:id` | API测试 | MainLayout | 是 | 否 |
| `/api/doc/:id` | 技术文档 | MainLayout | 否 | 否 |
| `/requirement` | 需求广场 | MainLayout | 否 | 否 |
| `/requirement/:id` | 需求详情 | MainLayout | 否 | 否 |
| `/user/my-apis` | 我的API | UserLayout | 是 | 否 |
| `/user/favorites` | 我的收藏 | UserLayout | 是 | 否 |
| `/user/orders` | 我的订单 | UserLayout | 是 | 否 |
| `/user/quota` | 我的调用次数 | UserLayout | 是 | 否 |
| `/user/my-requirements` | 我的需求 | UserLayout | 是 | 否 |
| `/user/statistics` | 统计分析 | UserLayout | 是 | 否 |
| `/user/profile` | 个人资料 | UserLayout | 是 | 否 |
| `/login` | 登录 | 无布局 | 否 | 否 |
| `/register` | 注册 | 无布局 | 否 | 否 |
| `/admin` | 仪表盘 | AdminLayout | 是 | 是 |
| `/admin/users` | 用户管理 | AdminLayout | 是 | 是 |
| `/admin/apis` | API管理 | AdminLayout | 是 | 是 |
| `/admin/api-types` | API分类管理 | AdminLayout | 是 | 是 |
| `/admin/orders` | 订单管理 | AdminLayout | 是 | 是 |
| `/admin/requirements` | 需求管理 | AdminLayout | 是 | 是 |
| `/admin/after-sales` | 售后管理 | AdminLayout | 是 | 是 |
| `/admin/statistics` | 平台统计 | AdminLayout | 是 | 是 |

### 12.4 前端 API 请求模块（src/api/）

| 文件 | 对应后端模块 | 核心接口 |
|------|------------|---------|
| auth.ts | AuthController | 登录/注册/登出/用户信息 |
| api.ts | ApiController | API列表/详情/创建/更新 |
| accessKey.ts | AccessKeyController | AK/SK获取/重新生成 |
| trade.ts | OrderController | 下单/支付/订单管理 |
| test.ts | TestController | API测试/记录管理 |
| requirement.ts | RequirementController | 需求CRUD/申请/接单 |
| afterSale.ts | RequirementAfterSaleController | 售后申请/裁定/消息 |
| review.ts | ApiReviewController | 评价/回复 |
| whitelist.ts | ApiWhitelistController | 白名单管理 |
| quota.ts | QuotaController | 配额查询 |
| admin.ts | ManagerController | 后台管理 |
| tag.ts | UserTagController + MatchingController | 标签/推荐 |
| notification.ts | NotificationController | 通知消息 |

### 12.5 前端状态管理（src/stores/）

| Store | 文件 | 功能 |
|-------|------|------|
| user | user.ts | 用户登录态/信息/Session验证（localStorage持久化） |
| notification | notification.ts | 未读消息数/消息列表（WebSocket驱动） |
| app | app.ts | 全局应用状态 |

### 12.6 前端公共组件（src/components/）

| 组件 | 功能 |
|------|------|
| Sidebar.vue | 侧边栏导航 |
| NotificationBell.vue | 通知铃铛（导航栏） |
| NotificationPanel.vue | 通知面板浮层 |
| ReviewThread.vue | 评价回复线程（嵌套回复） |
| ApiCreateDialog.vue | API创建对话框 |
| MethodTag.vue | HTTP方法标签 |
| StatusTag.vue | 状态标签 |
| PriceDisplay.vue | 价格显示 |
| ParamTable.vue | 参数表格 |
| TagInput.vue | 标签输入 |
| statistics/StatsCard.vue | 统计卡片 |
| statistics/IndicatorSelector.vue | 指标选择器 |
| statistics/TimeRangeSelector.vue | 时间范围选择器 |

### 12.7 前端工具函数（src/utils/）

| 文件 | 功能 |
|------|------|
| request.ts | Axios 封装，统一拦截器（401跳登录/403无权限/500服务器错误） |
| websocket.ts | WebSocket 客户端（自动重连/心跳/ping-pong/最大5次重连） |
| format.ts | 格式化工具（日期/金额等） |
| status.ts | 状态映射工具（API/订单/需求状态→中文/颜色） |

### 12.8 前端 TypeScript 类型（src/types/）

| 文件 | 核心类型 |
|------|---------|
| auth.ts | UserInfo, LoginParams, RegisterParams, LoginResult |
| api.ts | ApiItem, ApiParam, ApiCreateParams, ApiListParams, ApiStatistics, ApiType |
| trade.ts | 订单/交易相关类型 |
| test.ts | 测试记录相关类型 |
| requirement.ts | 需求/申请/售后相关类型 |
| notification.ts | 通知消息相关类型 |
| index.ts | User, PlatformStatistics（汇总导出） |

### 12.9 前端布局体系

| 布局 | 文件 | 适用场景 |
|------|------|---------|
| MainLayout.vue | 主布局 | 首页/API市场/需求广场等公开页面 |
| UserLayout.vue | 用户中心布局 | 我的API/订单/收藏/配额等 |
| AdminLayout.vue | 管理后台布局 | 仪表盘/用户管理/API管理等 |

### 12.10 前端与后端接口对应关系

前端 `src/config/index.ts` 中的 `apiEndpoints` 定义了所有后端接口路径，与后端 Controller 路由一一对应。修改后端接口时，需同步更新此文件。

### 12.11 前端构建命令

```bash
cd C:\Users\24551\Desktop\毕设项目\api-platform-frontend
npm run dev      # 开发服务器（端口 3000）
npm run build    # 生产构建（tsc && vite build）
npm run preview  # 预览生产构建
```

---

## 十三、变更记录

| 日期 | 变更内容 | 影响范围 |
|------|---------|---------|
| 2026-04-09 | 重构 mock-api：删除 MockApiController，新增 ImageApiController(6端点)、TextApiController(8端点)、VoiceApiController(5端点)，共19个POST Mock接口 | api-platform-mock-api |
| 2026-04-09 | 重构 mock-api-2：删除 MockApiController，新增 DataApiController(6GET端点)、MapApiController(5GET端点)、PayApiController(3POST+1GET端点)、SmsApiController(5POST端点)，共20个Mock接口 | api-platform-mock-api-2 |
| 2026-04-09 | 重构 mock-api-3：删除 MockApiController，新增 FaceApiController(6端点)、OcrApiController(8端点)、TranslateApiController(7端点)，共21个POST Mock接口 | api-platform-mock-api-3 |
| 2026-04-09 | 生成 SQL 更新脚本 update_api_target_url.sql，将60条API的target_url修正为对应mock服务地址(id1-19→8081, id20-39→8082, id40-60→8083) | 数据库api_info表 |
| 2026-04-09 | 更新项目记忆文件第五节，补充api_platform.sql完整表结构信息（含字段详情和AUTO_INCREMENT） | .trae/project-memory.md |
| 2026-04-09 | 为后端VO层全部23个Java文件添加中文Javadoc注释（类级注释+字段级注释） | api-platform-backend/vo |
| 2026-04-09 | 为后端Entity层全部17个Java文件添加中文Javadoc注释（类级注释+字段级注释+特殊注解说明） | api-platform-backend/entity |
| 2026-04-09 | 为后端DTO层全部40个Java文件添加中文Javadoc注释（类级注释+字段级注释，含校验规则说明） | api-platform-backend/dto |
| 2026-04-09 | 为后端Controller层全部19个Java文件添加中文Javadoc注释（类级注释+方法级注释，含路由前缀、业务流程、参数含义、返回值说明） | api-platform-backend/controller |
| 2026-04-09 | 为后端Service接口层全部21个Java文件添加中文Javadoc注释（类级注释+方法级注释，含核心职责、所属业务模块、参数含义、返回值说明） | api-platform-backend/service |
| 2026-04-09 | 为后端Service实现层全部21个Java文件添加中文Javadoc注释（类级注释，含核心职责、业务流程、状态流转、缓存策略等说明） | api-platform-backend/service/impl |
| 2026-04-09 | 为后端Dubbo/Mapper/配置/工具/异常/拦截器/注解/常量/WebSocket层全部36个Java文件添加中文Javadoc注释 | api-platform-backend（dubbo/mapper/config/utils/exception/interceptor/annotation/ratelimit/constants/websocket/common） |
| 2026-04-09 | 为前端API请求模块13个文件添加中文注释（文件级+方法级，含对应后端Controller说明） | api-platform-frontend/src/api |
| 2026-04-09 | 为前端Store模块3个文件、工具函数4个文件添加中文注释（文件级+方法级，含持久化策略、拦截器逻辑说明） | api-platform-frontend/src/stores + src/utils |
| 2026-04-09 | 为前端类型定义7个文件添加中文注释（文件级+接口级+字段级） | api-platform-frontend/src/types |
| 2026-04-09 | 为前端布局组件3个、公共组件13个添加中文注释（HTML注释，说明组件功能和适用场景） | api-platform-frontend/src/layouts + src/components |
| 2026-04-25 | 修复订单相关文件中文注释：OrderController补充payOrder/handlePayNotify Javadoc并移除createOrder过时限流描述；OrderVO修正invokeCount语义、补充时间字段String原因、review关联说明、status枚举中文；OrderInfoServiceImpl补充updateOrderStatus兜底逻辑注释、deleteOrder状态校验说明、convertToVO的replyType含义；OrderInfo补充invokeCount无限次说明、rating修正为0.5-5.0步长0.5；ApiInfo补充requestParams/responseParams JSON结构规范 | api-platform-backend（controller/vo/service/impl/entity） |
| 2026-04-09 | 为前端页面视图25个文件添加中文注释（HTML注释，说明页面功能） | api-platform-frontend/src/views |
| 2026-04-09 | 为前端入口/路由/配置4个文件添加中文注释（文件级+关键配置注释） | api-platform-frontend/src（main.ts/App.vue/router/config） |
| 2026-04-09 | 为云模块全部35个Java文件添加中文Javadoc注释（网关过滤器含执行顺序、Dubbo接口含消费者方、SDK含使用方式） | api-platform-cloud（gateway/common/client-sdk/mock-api） |
| 2026-04-09 | 创建7天模拟测试数据SQL脚本insert_test_data.sql和delete_test_data.sql，覆盖14张表（order_info/api_invoke_daily/api_test_record/api_review/api_favorite/api_whitelist/user_api_quota/requirement/requirement_applicant/requirement_tag/requirement_after_sale/after_sale_message/notification_message/user_tag），ID范围9001+，测试用户user_id=1，时间范围2026-04-02~2026-04-08 | 数据库测试数据 |
| 2026-04-10 | 重新生成7天模拟测试数据SQL脚本，基于实际mock API控制器重新创建api_info(60条)，修正endpoint/target_url/request_params/response_params，user_id=1拥有10个API，覆盖所有状态和功能场景，时间范围2026-04-03~2026-04-09 | 数据库api_info表+测试数据 |
| 2026-04-19 | 修复限流逻辑：修复令牌桶refillRate=capacity致命Bug（限流形同虚设）；Lua脚本升级为毫秒级精度+正确时间推进；两层限流固定capacity=2/refillRate=2（每秒2次）；TestController新增限流；移除OrderController/ApiController的@RateLimit注解；网关未识别用户返回401；DefaultRedisScript改为类常量；Redis Key前缀隔离（biz_rate_limit:/gateway_rate_limit:） | api-platform-backend(ratelimit/controller) + api-platform-gateway(filter/ratelimit) |
