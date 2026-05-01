# Mock API 全量接口实现与数据库路径修正 Spec

## Why

数据库 `api_info` 表中有 60 条 API 记录，其 `target_url` 指向 localhost:8001~8096 等不存在的端口，而现有的 3 个 mock-api 项目（8081/8082/8083）仅各有一个 `GET /weather` 端点，与数据库中的 endpoint 完全不匹配。导致通过网关调用任何 API 都会失败，前后端无法跑通。

## What Changes

- 重写 3 个 mock-api 项目的 Controller，实现全部 60 个 API 的模拟接口
- 按 API 类别分配到 3 个 mock 服务：
  - **mock-api (8081)**：图像识别(1-6) + 文本处理(7-14) + 语音处理(15-19) = 19 个接口
  - **mock-api-2 (8082)**：数据服务(20-25) + 地图服务(26-30) + 支付服务(31-34) + 短信服务(35-39) = 20 个接口
  - **mock-api-3 (8083)**：人脸识别(40-45) + OCR识别(46-53) + 翻译服务(54-60) = 21 个接口
- 生成 SQL 更新脚本，将 `target_url` 修正为对应的 mock 服务地址
- 更新项目记忆文件，补充 `api_platform.sql` 表结构信息

## Impact

- Affected code:
  - `api-platform-cloud/api-platform-mock-api/src/main/java/com/api/platform/mock/controller/MockApiController.java`
  - `api-platform-cloud/api-platform-mock-api-2/src/main/java/com/api/platform/mock/controller/MockApiController.java`
  - `api-platform-cloud/api-platform-mock-api-3/src/main/java/com/api/platform/mock/controller/MockApiController.java`
  - `.trae/project-memory.md`（更新表结构信息）
- 数据库 `api_info` 表的 `target_url` 字段需要批量更新（通过 SQL 脚本）

## ADDED Requirements

### Requirement: Mock API 全量接口实现

系统 SHALL 在 3 个 mock-api 项目中实现全部 60 个 API 的模拟接口，每个接口返回符合 `response_params` 定义的模拟数据。

#### Scenario: 通过网关调用图像识别 API
- **WHEN** 用户通过网关调用 `POST /api/v1/image/recognition`
- **THEN** 请求被路由到 `http://localhost:8081/api/v1/image/recognition`
- **AND** 返回包含 `label` 和 `confidence` 字段的 JSON 响应

#### Scenario: 通过网关调用天气查询 API
- **WHEN** 用户通过网关调用 `GET /api/v1/data/weather?city=北京`
- **THEN** 请求被路由到 `http://localhost:8082/api/v1/data/weather`
- **AND** 返回包含天气信息的 JSON 响应

#### Scenario: 通过网关调用通用翻译 API
- **WHEN** 用户通过网关调用 `POST /api/v1/translate/general`
- **THEN** 请求被路由到 `http://localhost:8083/api/v1/translate/general`
- **AND** 返回包含 `translated` 字段的 JSON 响应

### Requirement: 数据库 target_url 修正

系统 SHALL 提供 SQL 脚本将 `api_info` 表中所有 `target_url` 更新为对应的 mock 服务地址：
- id 1-19 → `http://localhost:8081`
- id 20-39 → `http://localhost:8082`
- id 40-60 → `http://localhost:8083`

#### Scenario: 执行 SQL 后网关可路由到正确的 mock 服务
- **WHEN** 用户执行提供的 SQL 更新脚本
- **THEN** 所有 approved 状态的 API 的 `target_url` 指向实际运行的 mock 服务
- **AND** 通过网关调用可正常返回模拟数据

### Requirement: 项目记忆文件更新

系统 SHALL 将 `api_platform.sql` 的完整表结构信息写入 `.trae/project-memory.md` 的"五、数据库表速查"章节。

## MODIFIED Requirements

### Requirement: Mock API Controller 重写

现有的 3 个 `MockApiController` 仅提供 `GET /weather` 端点，需替换为按类别分组的全量接口实现。每个接口需：
- 匹配数据库中定义的 HTTP 方法（GET/POST）
- 匹配数据库中定义的 endpoint 路径
- 返回符合 `response_params` 定义的模拟数据
- 使用 `com.api.platform.common.Result` 统一封装响应
