---
name: "code-refactorer"
description: "Reviews and refactors Java/Spring Boot code for enterprise standards, controller organization, bug detection, and performance optimization. Invoke when user asks for code refactoring, code review, or improving code quality."
---

# Code Refactorer Skill

代码重构技能，用于检查和改进项目代码质量，确保符合企业开发规范。

## 适用场景

- 用户请求重构代码
- 用户请求代码审查
- 用户请求改进代码质量
- 用户请求检查接口位置是否合理
- 用户请求检查潜在bug
- 用户请求检查性能优化点

## 重构检查清单

### 一、企业开发规范检查

#### 1.1 接口设计规范
- [ ] 接口是否符合单一职责原则
- [ ] 接口是否做到高内聚低耦合
- [ ] 列表查询接口是否实现分页
- [ ] 接口路径是否体现操作类型（如update、delete）
- [ ] 是否避免使用RESTFUL风格（根据项目规则）
- [ ] 接口命名是否清晰、符合驼峰命名规范

#### 1.2 代码规范
- [ ] 类、方法、变量命名是否规范
- [ ] 是否有重复代码需要抽取
- [ ] 异常处理是否完善
- [ ] 日志记录是否合理
- [ ] 是否有硬编码需要提取为常量
- [ ] 是否有魔法数字需要定义常量

#### 1.3 注释规范
- [ ] 类是否有文档注释
- [ ] 公共方法是否有注释说明
- [ ] 复杂逻辑是否有行内注释
- [ ] 注释是否准确描述代码功能

### 二、Controller接口位置检查

#### 2.1 接口归属检查
- [ ] 检查每个Controller的职责是否单一
- [ ] 检查接口是否放置在正确的Controller中
- [ ] 检查是否存在跨领域调用
- [ ] 检查Controller命名是否与业务领域匹配
- [ ] 检查Controller路径是否与类名一致

#### 2.2 Controller命名与路径规范

| Controller类名 | 推荐路径 | 职责范围 |
|---------------|---------|---------|
| AuthController | /auth | 登录、注册、登出、密码修改 |
| UserController | /user | 用户信息查询、修改、用户列表 |
| ApiController | /api | API列表、详情、创建、更新、删除 |
| OrderController | /order | 订单创建、查询、状态更新 |
| RequirementController | /requirement | 需求管理相关接口 |
| QuotaController | /quota | 配额查询、管理 |
| AccessKeyController | /accessKey | AK/SK管理 |
| AdminController | /admin | 管理员相关接口（用户管理、API审核等） |
| AdminStatisticsController | /admin/statistics | 管理员统计接口 |

#### 2.3 常见接口位置问题

**问题1：Controller类名与路径不匹配**
- 错误示例：`UserController` 映射到 `/auth` 路径
- 正确做法：类名应与路径对应，如 `AuthController` 对应 `/auth`

**问题2：接口放置在错误的Controller**
- 错误示例：配额接口 `/quota/list` 放在 `ApiInvokeController`
- 正确做法：应放在 `QuotaController`

**问题3：管理员接口与用户接口混用**
- 错误示例：管理员审核接口放在普通用户Controller
- 正确做法：管理员接口统一放在 `/admin` 路径下的Controller

**问题4：统计接口位置不统一**
- 错误示例：用户统计接口放在 `ApiController`
- 正确做法：按角色区分，管理员统计放 `AdminStatisticsController`，用户统计可单独建 `UserStatisticsController`

#### 2.4 接口移动原则
- 按业务领域划分
- 按用户角色划分（管理员/普通用户）
- 按功能模块划分
- 保持接口路径的一致性
- 移动后需同步更新前端API调用路径

### 三、潜在Bug检查

#### 3.1 空指针检查
- [ ] 对象调用前是否进行null检查
- [ ] 集合操作前是否检查null和empty
- [ ] 字符串操作前是否检查null和empty
- [ ] 数据库查询结果是否检查null
- [ ] Session获取的属性是否检查null

#### 3.2 参数校验
- [ ] 必填参数是否校验
- [ ] 参数范围是否校验
- [ ] 参数格式是否校验
- [ ] 是否使用@Valid或@Validated注解
- [ ] 路径参数是否校验有效性

#### 3.3 并发安全
- [ ] 是否存在线程安全问题
- [ ] 共享变量是否正确同步
- [ ] 数据库事务是否正确配置
- [ ] 是否存在竞态条件

#### 3.4 资源管理
- [ ] 数据库连接是否正确关闭
- [ ] 文件流是否正确关闭
- [ ] 是否存在内存泄漏风险
- [ ] HTTP连接是否正确管理

#### 3.5 业务逻辑
- [ ] 条件判断是否完整
- [ ] 边界条件是否处理
- [ ] 异常情况是否处理
- [ ] 状态机转换是否正确
- [ ] 权限校验是否完整

#### 3.6 常见Bug模式

**空指针风险示例**
```java
// 错误：未检查null
User user = userMapper.selectById(userId);
return user.getUsername(); // 可能NPE

// 正确：检查null
User user = userMapper.selectById(userId);
if (user == null) {
    throw new BusinessException("用户不存在");
}
return user.getUsername();
```

**状态转换校验示例**
```java
// 错误：未校验状态转换合法性
apiInfo.setStatus(newStatus);
updateById(apiInfo);

// 正确：校验状态转换
if (!isValidStatusTransition(currentStatus, newStatus)) {
    throw new BusinessException("状态转换不合法");
}
apiInfo.setStatus(newStatus);
updateById(apiInfo);
```

### 四、性能优化检查

#### 4.1 数据库优化
- [ ] 是否使用MyBatis Plus进行查询
- [ ] 是否存在N+1查询问题
- [ ] 是否需要添加索引
- [ ] 分页查询是否高效
- [ ] 是否避免SELECT *
- [ ] 批量操作是否使用batch
- [ ] 是否存在循环内查询数据库

#### 4.2 缓存优化
- [ ] 热点数据是否考虑缓存
- [ ] 缓存穿透/击穿/雪崩是否处理
- [ ] 缓存更新策略是否合理

#### 4.3 代码优化
- [ ] 循环中是否有不必要的数据库查询
- [ ] 是否存在重复计算
- [ ] 是否可以使用延迟加载
- [ ] 集合操作是否高效
- [ ] 是否存在重复代码可抽取

#### 4.4 接口优化
- [ ] 是否存在接口响应过慢
- [ ] 是否需要异步处理
- [ ] 是否需要接口合并
- [ ] 返回数据是否精简

#### 4.5 常见性能问题

**N+1查询问题**
```java
// 错误：循环内查询
List<ApiVO> result = apis.stream().map(api -> {
    ApiType type = apiTypeMapper.selectById(api.getTypeId()); // N次查询
    api.setTypeName(type.getName());
    return api;
}).collect(Collectors.toList());

// 正确：批量查询
List<Long> typeIds = apis.stream().map(Api::getTypeId).distinct().collect(Collectors.toList());
Map<Long, String> typeMap = apiTypeMapper.selectBatchIds(typeIds)
    .stream().collect(Collectors.toMap(ApiType::getId, ApiType::getName));
List<ApiVO> result = apis.stream().map(api -> {
    api.setTypeName(typeMap.get(api.getTypeId()));
    return api;
}).collect(Collectors.toList());
```

**重复代码抽取**
```java
// 错误：重复的VO构建代码
public ApiVO getApiDetailById(Long id) {
    ApiInfo apiInfo = getById(id);
    ApiVO apiVO = new ApiVO();
    BeanUtils.copyProperties(apiInfo, apiVO);
    // ... 大量重复代码
}

// 正确：抽取公共方法
private ApiVO convertToVO(ApiInfo apiInfo) {
    ApiVO apiVO = new ApiVO();
    BeanUtils.copyProperties(apiInfo, apiVO);
    // ... 公共转换逻辑
    return apiVO;
}
```

### 五、前后端一致性检查

#### 5.1 接口定义
- [ ] 前端请求路径与后端是否一致
- [ ] 请求参数名称是否一致
- [ ] 返回数据结构是否一致
- [ ] 错误码处理是否一致

#### 5.2 数据类型
- [ ] 日期格式是否一致
- [ ] 数字精度是否一致
- [ ] 枚举值是否一致

#### 5.3 前后端对照检查

**前端API文件位置**
- `src/api/auth.ts` - 认证相关
- `src/api/api.ts` - API管理相关
- `src/api/admin.ts` - 管理员相关
- `src/api/quota.ts` - 配额相关
- `src/api/requirement.ts` - 需求相关
- `src/api/trade.ts` - 交易/订单相关
- `src/api/test.ts` - 测试相关
- `src/api/accessKey.ts` - AK/SK相关

**检查步骤**
1. 读取前端API定义文件
2. 对比后端Controller接口
3. 标识路径、参数、返回值不一致的地方

### 六、代码重复检测

#### 6.1 重复代码模式
- [ ] VO/DTO转换代码重复
- [ ] 分页查询代码重复
- [ ] 权限校验代码重复
- [ ] Session获取用户ID代码重复
- [ ] 日期格式化代码重复

#### 6.2 可抽取的公共代码

**Session用户获取**
```java
// 重复代码
Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
if (userId == null) {
    return Result.failed("请先登录");
}

// 建议抽取为工具方法
public static Long getCurrentUserId(HttpSession session) {
    Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
    if (userId == null) {
        throw new UnauthorizedException("请先登录");
    }
    return userId;
}
```

**分页结果构建**
```java
// 重复代码
return Result.success(PageResultVO.of(page.getRecords(), page.getTotal()));

// 可保持，已足够简洁
```

### 七、HTTP方法规范检查

#### 7.1 方法使用规范
| 操作类型 | 推荐方法 | 示例路径 |
|---------|---------|---------|
| 查询（单个） | GET | /api/detail/{id} |
| 查询（列表） | GET | /api/list |
| 创建 | POST | /api/create |
| 更新 | PUT | /api/update/{id} |
| 删除 | DELETE | /api/delete/{id} |
| 状态变更 | PUT | /api/updateStatus/{id} |


## 执行步骤

### 步骤1：项目结构分析
1. 扫描所有Controller文件
2. 分析每个Controller的职责和接口
3. 识别接口归属问题
4. 检查Controller命名与路径是否匹配

### 步骤2：代码规范检查
1. 检查命名规范
2. 检查代码重复
3. 检查异常处理
4. 检查注释完整性
5. 检查HTTP方法使用

### 步骤3：Bug检测
1. 空指针风险检测
2. 参数校验检测
3. 并发安全检测
4. 业务逻辑检测

### 步骤4：性能分析
1. 数据库查询分析
2. 缓存使用分析
3. 代码性能分析
4. 重复代码检测

### 步骤5：前后端一致性
1. 对比前端API定义
2. 对比后端接口实现
3. 识别不一致之处

### 步骤6：生成重构报告
1. 汇总所有问题
2. 按优先级排序
3. 提供具体修改建议
4. 提供代码示例

## 输出格式

重构完成后，输出以下格式的报告：

```markdown
# 代码重构报告

## 一、Controller接口位置问题
| 问题接口 | 当前位置 | 建议位置 | 原因 |
|---------|---------|---------|------|
| ... | ... | ... | ... |

## 二、Controller命名问题
| 当前类名 | 当前路径 | 问题描述 | 修改建议 |
|---------|---------|---------|---------|
| ... | ... | ... | ... |

## 三、代码规范问题
| 文件 | 行号 | 问题描述 | 修改建议 |
|-----|------|---------|---------|
| ... | ... | ... | ... |

## 四、潜在Bug
| 文件 | 行号 | Bug类型 | 问题描述 | 修改建议 |
|-----|------|---------|---------|---------|
| ... | ... | ... | ... | ... |

## 五、性能优化建议
| 文件 | 位置 | 问题描述 | 优化建议 |
|-----|------|---------|---------|
| ... | ... | ... | ... |

## 六、代码重复问题
| 文件 | 重复代码位置 | 问题描述 | 优化建议 |
|-----|------------|---------|---------|
| ... | ... | ... | ... |

## 七、前后端不一致
| 接口 | 前端定义 | 后端实现 | 问题描述 |
|-----|---------|---------|---------|
| ... | ... | ... | ... |

## 八、重构优先级
### 高优先级（影响功能/安全）
1. ...
2. ...

### 中优先级（影响性能/规范）
1. ...
2. ...

### 低优先级（优化建议）
1. ...
2. ...
```

## 注意事项

1. 重构前确保有代码备份或版本控制
2. 每次重构只修改一个方面，便于测试
3. 重构后必须进行测试验证
4. 保持向后兼容性
5. 记录重构原因和修改内容
6. 移动接口时需同步更新前端调用
7. 修改Controller路径时需检查拦截器配置

## 项目特定规则

根据项目实际情况，需特别注意：

1. **不使用RESTFUL风格**：接口路径需明确体现操作类型
2. **分页查询必须分页**：所有列表查询接口需支持分页
3. **优先使用MyBatis Plus**：数据库查询尽量使用MP完成
4. **接口复用**：能复用接口的情况下优先复用
5. **路径命名规范**：update、delete需在路径中体现
