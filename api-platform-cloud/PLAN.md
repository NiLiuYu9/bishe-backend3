# API开放平台架构改造计划

## 一、项目模块规划

### 最终目录结构
```
毕设项目/
├── api-platform-backend/          # 主项目（后台管理服务 + Dubbo服务提供者）
├── api-platform-cloud/            # Spring Cloud项目
│   ├── api-platform-common/       # 公共模块（VO类 + Dubbo接口定义）
│   ├── api-platform-gateway/      # API网关（过滤器链）
│   ├── api-platform-interface/    # 业务接口实现（模拟API服务）
│   └── api-platform-client-sdk/   # 客户端SDK
└── api-platform-frontend/         # 前端项目
```

---

## 二、改造任务清单

### 阶段一：公共模块改造

#### 1.1 新增签名工具类
- 创建 `SignUtils.java` - SHA256签名生成
- 位置: `com.api.platform.common.utils.SignUtils`

#### 1.2 新增Dubbo服务接口
- 创建 `InnerUserService.java` - 用户查询接口
- 创建 `InnerInterfaceInfoService.java` - 接口信息查询
- 创建 `InnerUserInterfaceInfoService.java` - 调用统计接口
- 位置: `com.api.platform.common.service`

#### 1.3 新增VO类（网关需要的简化实体）
- 创建 `InvokeUserVO.java` - 包含 id, accessKey, secretKey, status
- 创建 `InterfaceInfoVO.java` - 包含 id, path, method, status
- 位置: `com.api.platform.common.vo`

#### 1.4 新增请求常量
- 更新 `AuthConstants.java` - 添加 nonce、timestamp、sign、body 等Header常量

---

### 阶段二：主项目改造

#### 2.1 添加Dubbo依赖
- pom.xml 添加 spring-cloud-starter-dubbo
- pom.xml 添加 api-platform-common 依赖

#### 2.2 实现Dubbo服务
- 创建 `InnerUserServiceImpl.java` - 用户查询实现
- 创建 `InnerInterfaceInfoServiceImpl.java` - 接口信息实现
- 创建 `InnerUserInterfaceInfoServiceImpl.java` - 调用统计实现
- 位置: `com.api.platform.service.dubbo`

#### 2.3 改造密钥生成逻辑
- 修改 `AccessKeyServiceImpl.java`
- 使用 MD5 生成 accessKey 和 secretKey
- 用户注册时自动生成密钥

#### 2.4 配置Dubbo
- application.yml 添加 Dubbo 配置
- 添加 @DubboService 注解

---

### 阶段三：网关模块改造

#### 3.1 添加Dubbo依赖
- pom.xml 添加 spring-cloud-starter-dubbo
- pom.xml 添加 api-platform-common 依赖

#### 3.2 实现过滤器链（按Order顺序）

**过滤器1: IP白名单过滤器 (Order=-1)**
- 文件: `AccessControlFilter.java`
- 功能: 检查请求来源IP是否在白名单

**过滤器2: 鉴权过滤器 (Order=0)**
- 文件: `AuthFilter.java` (改造现有)
- 功能:
  1. 提取请求头
  2. 通过Dubbo调用获取用户信息
  3. 验证随机数 nonce
  4. 验证时间戳 (5分钟有效期)
  5. 验证签名 SHA256(body + "." + secretKey)
  6. 存储用户ID到exchange

**过滤器3: 接口验证过滤器 (Order=1)**
- 文件: `InterfaceValidateFilter.java`
- 功能: 验证请求的接口是否存在且可用

**过滤器4: 响应处理过滤器 (Order=2)**
- 文件: `ResponseLogFilter.java`
- 功能: 响应成功后更新调用次数

#### 3.3 删除旧过滤器
- 删除 `AuthFilterGatewayFilterFactory.java`
- 删除 `AuthService.java` 和 `AuthServiceImpl.java`

---

### 阶段四：接口模块创建

#### 4.1 创建模块
- pom.xml 配置
- 注册到Nacos

#### 4.2 实现模拟API
- 创建 `MockApiController.java`
- 提供 `/weather`, `/random`, `/user/info` 等接口

---

### 阶段五：客户端SDK创建

#### 5.1 创建模块
- pom.xml 配置（不依赖Spring Boot）

#### 5.2 实现SDK核心类
- 创建 `ApiClient.java` - 主客户端类
- 创建 `SignUtils.java` - 签名工具
- 创建 `RequestConfig.java` - 请求配置

#### 5.3 实现请求逻辑
- 自动生成签名
- 自动添加请求头
- 支持同步/异步请求

---

## 三、详细实现规范

### 3.1 密钥生成规则
```java
// 用户注册时生成
String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
```

### 3.2 签名算法
```java
public static String genSign(String body, String secretKey) {
    Digester digester = new Digester(DigestAlgorithm.SHA256);
    String content = body + "." + secretKey;
    return digester.digestHex(content);
}
```

### 3.3 请求头规范
| Header | 说明 | 示例 |
|--------|------|------|
| accessKey | 用户标识 | abc123... |
| nonce | 随机数(防重放) | 1234 |
| timestamp | 时间戳(秒级) | 1700000000 |
| body | 请求体内容 | {"city":"北京"} |
| sign | 签名 | sha256hash... |

### 3.4 验证规则
- **随机数验证**: nonce <= 10000
- **时间戳验证**: currentTime - timestamp < 300秒(5分钟)
- **签名验证**: 客户端签名 == 服务端签名

---

## 四、配置变更

### 4.1 主项目 application.yml
```yaml
dubbo:
  application:
    name: api-platform-backend
  protocol:
    name: dubbo
    port: 20880
  registry:
    address: nacos://localhost:8848
  scan:
    base-packages: com.api.platform.service.dubbo
```

### 4.2 网关 application.yml
```yaml
dubbo:
  application:
    name: api-platform-gateway
  registry:
    address: nacos://localhost:8848
  consumer:
    check: false
```

---

## 五、执行顺序

1. **阶段一**: 公共模块改造 → 新增VO类 → 新增常量 → 新增Dubbo接口

2. **阶段二**: 主项目改造 → 实现Dubbo服务 → 改造密钥生成 → 配置Dubbo

3. **阶段三**: 网关改造 → 实现过滤器链 → 删除旧代码

4. **阶段四**: 接口模块创建（可复用现有mock-api）

5. **阶段五**: 客户端SDK创建

---

## 六、注意事项

1. **secretKey永不传输**: 只用于签名计算，不在网络中传递
2. **兼容性**: 保留原有的Session认证方式，供前端管理后台使用
3. **网关路由**: 需要调整路由配置，区分管理后台和API调用
4. **测试验证**: 每个阶段完成后进行单元测试

---

## 七、预计工作量

| 阶段 | 任务数 | 预计时间 |
|------|--------|----------|
| 阶段一 | 8个文件 | 30分钟 |
| 阶段二 | 6个文件 | 30分钟 |
| 阶段三 | 6个文件 | 40分钟 |
| 阶段四 | 3个文件 | 15分钟 |
| 阶段五 | 5个文件 | 20分钟 |
| **总计** | **28个文件** | **约2小时** |
