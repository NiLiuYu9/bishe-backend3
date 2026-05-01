# API 开放平台 - 项目规则

## 1. 项目记忆优先原则

每次执行任务前，必须先读取项目记忆文件 `.trae/project-memory.md`，从中获取：
- 项目目录结构和技术栈信息
- 业务模块划分和 Controller 路由
- 数据库表结构和状态流转规则
- 已知问题和待修复项
- 历史变更记录

如果记忆文件中的信息不足以完成任务，再通过搜索代码库补充。

## 2. 代码改动安全原则

每次代码改动必须确保不影响原有功能，具体规则：

### 2.1 后端改动规则
- **不修改现有接口签名**：如需修改 Controller 方法参数或返回值，必须保持向后兼容，新增参数给默认值
- **不删除现有字段**：Entity/DTO/VO 字段只能新增，不能删除或重命名（除非明确要求）
- **不修改数据库表结构**：如需修改表结构，必须通过 migration SQL 文件新增，不能直接修改 `api_platform.sql`
- **不改变状态流转逻辑**：API/订单/需求/售后的状态流转规则是核心业务逻辑，修改前必须确认影响范围
- **不修改 Dubbo 接口定义**：`api-platform-common` 中的 Dubbo 服务接口被多个模块依赖，修改需同步更新所有消费方
- **不修改网关过滤器顺序**：过滤器的 Order 值决定了执行链，不能随意调整
- **不修改签名算法**：AK/SK 签名机制（SHA256）被 SDK 和网关同时依赖，修改会导致调用失败

### 2.2 新增代码规则
- Controller 放在 `controller/` 目录，路由前缀与业务模块对应
- Service 接口放在 `service/`，实现放在 `service/impl/`
- DTO 用于接收请求参数，VO 用于返回响应数据，Entity 与数据库表一一对应
- 新增数据库字段必须同时更新 Entity 和对应的 DTO/VO
- 新增 Mapper XML 放在 `resources/mapper/` 目录
- 新增配置项必须写在 `application.yml` 中，不能硬编码

### 2.3 修改代码规则
- 修改 Service 方法时，检查所有调用方是否受影响
- 修改 Entity 字段时，检查对应的 Mapper XML 是否有显式 SQL 引用该字段
- 修改 Controller 接口时，检查前端是否调用该接口（前端项目如存在）
- 修改配置时，检查是否有其他模块依赖该配置

## 3. 任务完成后更新记忆文档

每次任务完成后，必须更新 `.trae/project-memory.md`，更新内容包括：

### 3.1 必须更新
- **变更记录**：在"十二、变更记录"表格中新增一行，记录日期、变更内容、影响范围

### 3.2 按需更新
- 如果新增了 Controller/Service/Entity，更新对应的速查表
- 如果新增了数据库表或字段，更新"五、数据库表速查"
- 如果新增了 API 端点，更新"四、核心业务模块与 Controller 路由"
- 如果修改了状态流转逻辑，更新"六、核心状态流转"
- 如果新增了技术实现，更新"八、关键技术实现"
- 如果修复了已知问题，从"十一、已知问题与待修复"中移除
- 如果发现了新的问题，添加到"十一、已知问题与待修复"

## 4. 代码风格规范

- 使用 Lombok 注解（@Data, @Builder 等）简化 POJO
- 统一使用 `BusinessException` 抛出业务异常，由 `GlobalExceptionHandler` 统一处理
- 统一使用 `Result` 封装响应，不直接返回裸数据
- 统一使用 `SessionUtils` 获取当前登录用户，不从 HttpSession 直接取值
- 统一使用 `VoConverterUtils` 进行 Entity→VO 转换，不在 Controller 中写转换逻辑
- DTO 参数校验使用 JSR-303 注解（@NotNull, @NotBlank, @Size 等）
- 不在代码中添加中文注释（除非用户明确要求）

## 5. 构建与验证

- 后端构建命令：`cd api-platform-backend && mvn clean compile`
- 云模块构建命令：`cd api-platform-cloud && mvn clean compile`
- 修改后端代码后，执行 `mvn clean compile` 确保编译通过
- 修改云模块代码后，执行对应模块的 `mvn clean compile` 确保编译通过
- 修改数据库相关代码后，检查 Entity 字段与数据库表是否一致

## 6. 敏感信息保护

- 不提交支付宝私钥、公钥等敏感信息到公开仓库
- 不在日志中打印用户密码、secretKey 等敏感字段
- application.yml 中的数据库密码、支付宝密钥等属于开发环境配置，不应出现在生产环境
