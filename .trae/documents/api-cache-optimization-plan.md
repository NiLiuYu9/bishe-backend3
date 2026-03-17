# API信息表缓存优化方案

## 一、现状分析

### 1.1 当前数据访问情况

| 接口/服务 | 访问频率 | 数据来源 | 存在问题 |
|----------|---------|---------|---------|
| API详情查询 `/api/detail/{id}` | 高频 | 数据库 | 每次查询都访问数据库 |
| API列表查询 `/api/list` | 高频 | 数据库 | 分页查询效率低 |
| API调用验证 `InnerInterfaceInfoService` | 极高频 | 数据库 | 每次API调用都查数据库 |
| API调用 `ApiInvokeController.invokeApi` | 极高频 | 数据库 | 每次调用都查API信息 |
| 管理后台API列表 | 中频 | 数据库 | 无缓存 |

### 1.2 性能瓶颈

1. **API调用场景**：每次API调用都需要查询数据库验证接口信息，是最大的性能瓶颈
2. **API详情查询**：用户浏览API详情页时频繁查询数据库
3. **API列表查询**：首页展示、搜索等场景频繁查询

---

## 二、缓存优化方案

### 2.1 缓存策略设计

#### 2.1.1 缓存数据分类

| 缓存类型 | Key格式 | 数据结构 | 过期时间 | 适用场景 |
|---------|--------|---------|---------|---------|
| API详情缓存 | `api:info:{id}` | Hash | **永不过期** | API详情查询、API调用验证 |
| API路径映射缓存 | `api:path:{endpoint}:{method}` | String(id) | **永不过期** | API网关调用验证（通过路径查询，存储对应的API ID） |
| API列表缓存 | `api:list:{queryHash}` | String(JSON) | 5分钟 | API列表查询（列表查询条件多变，不适合永不过期） |
| 空值缓存 | `api:null:{id}` | String | 2分钟 | 防止缓存穿透 |

**缓存设计说明**：

```
为什么只需要API详情缓存和路径映射缓存？

1. API详情缓存 `api:info:{id}`
   - 存储内容：完整的ApiVO（id, name, description, endpoint, method, status, targetUrl, 统计信息等）
   - 查询方式：通过API ID查询
   - 使用场景：
     a. 前端API详情页查询
     b. API调用验证（通过ID）
     c. 内部服务获取API信息

2. API路径映射缓存 `api:path:{endpoint}:{method}`
   - 存储内容：API ID（只是一个Long类型的ID）
   - 查询方式：通过endpoint + method查询
   - 使用场景：API网关调用验证
   - 查询流程：先通过路径映射获取ID，再通过ID获取API详情

这样设计的好处：
1. 避免数据冗余：API信息只存储一份
2. 更新方便：更新API时只需更新一份缓存
3. 简化维护：减少缓存一致性问题
```

#### 2.1.2 缓存过期时间策略

提供两种策略可选：

**策略一：永不过期 + 写时更新（推荐）**

```
优点：
1. 完全避免缓存雪崩（没有过期时间）
2. 完全避免缓存击穿（没有过期瞬间）
3. 缓存命中率100%（除非数据变更）
4. 实现简单，不需要考虑过期时间随机化

缺点：
1. 需要确保所有写操作都正确更新缓存
2. 内存占用不会自动释放（需要监控）

适用场景：
- 读多写少的数据（API信息表非常适合）
- 数据变更可控的场景
```

**策略二：随机过期时间**

```
基础过期时间 + 随机偏移量(0-5分钟)
例如：30分钟 + Random(0, 300秒)

优点：
1. 自动释放内存
2. 防止缓存雪崩

缺点：
1. 仍可能发生缓存击穿
2. 缓存命中率不是100%
```

**结论**：API信息表是典型的"读多写少"场景，推荐采用**永不过期+写时更新**策略。

### 2.2 缓存穿透防护方案

**问题**：查询不存在的API时，请求直接穿透到数据库

**解决方案**：

1. **空值缓存**：对于不存在的API ID，缓存空值（设置较短过期时间2分钟）
2. **布隆过滤器**：可选方案，适用于数据量大且查询频繁的场景

```
// 伪代码
public ApiVO getApiDetailById(Long id) {
    // 1. 检查空值缓存
    if (redisTemplate.hasKey("api:info:null:" + id)) {
        return null;
    }
    
    // 2. 查询缓存
    ApiVO cached = getFromCache(id);
    if (cached != null) {
        return cached;
    }
    
    // 3. 查询数据库
    ApiVO api = getFromDatabase(id);
    
    // 4. 缓存结果（包括空值）
    if (api == null) {
        cacheNullValue(id, 2); // 缓存空值2分钟
    } else {
        cacheApiInfo(id, api);
    }
    
    return api;
}
```

### 2.3 缓存击穿防护方案

**问题**：热点API的缓存过期瞬间，大量请求同时打到数据库

**解决方案（采用永不过期策略后自动解决）**：

由于采用"永不过期+写时更新"策略，缓存不会因过期而失效，因此**不存在缓存击穿问题**。

**备选方案（如果采用有过期时间的策略）**：

1. **互斥锁**：使用Redis分布式锁，只允许一个线程重建缓存
2. **逻辑过期**：缓存永不过期，后台异步更新

```
// 互斥锁方案伪代码（备选）
public ApiVO getApiDetailWithLock(Long id) {
    // 1. 查询缓存
    ApiVO cached = getFromCache(id);
    if (cached != null) {
        return cached;
    }
    
    // 2. 获取分布式锁
    String lockKey = "api:lock:" + id;
    try {
        boolean locked = tryLock(lockKey, 10); // 10秒超时
        if (locked) {
            // 双重检查
            cached = getFromCache(id);
            if (cached != null) {
                return cached;
            }
            
            // 查询数据库并缓存
            ApiVO api = getFromDatabase(id);
            cacheApiInfo(id, api);
            return api;
        } else {
            // 等待并重试获取缓存
            Thread.sleep(50);
            return getApiDetailWithLock(id);
        }
    } finally {
        unlock(lockKey);
    }
}
```

### 2.4 缓存雪崩防护方案

**问题**：大量缓存同时过期，数据库压力骤增

**解决方案（采用永不过期策略后自动解决）**：

由于采用"永不过期+写时更新"策略，缓存不会因过期而失效，因此**不存在缓存雪崩问题**。

**备选方案（如果采用有过期时间的策略）**：

1. **随机过期时间**：基础时间 + 随机偏移
2. **多级缓存**：本地缓存(Caffeine) + Redis缓存
3. **熔断降级**：数据库压力过大时返回降级数据

```
// 随机过期时间（备选）
private long getRandomExpireTime(long baseSeconds) {
    Random random = new Random();
    return baseSeconds + random.nextInt(300); // 增加0-300秒随机偏移
}
```

### 2.5 永不过期+写时更新策略详解

**核心思想**：缓存永不过期，数据变更时主动更新缓存

```
// 读操作伪代码 - API详情查询（通过ID）
public ApiVO getApiDetailById(Long id) {
    // 1. 检查空值缓存
    if (apiCacheService.isNullValueCached(id)) {
        return null;
    }
    
    // 2. 查询缓存
    ApiVO cached = apiCacheService.getApiDetailFromCache(id);
    if (cached != null) {
        return cached;
    }
    
    // 3. 缓存未命中，查询数据库并写入缓存
    ApiVO api = getFromDatabase(id);
    if (api != null) {
        apiCacheService.cacheApiDetail(id, api); // 永不过期
        // 同时缓存路径映射
        apiCacheService.cachePathMapping(api.getEndpoint(), api.getMethod(), id);
    } else {
        apiCacheService.cacheNullValue(id); // 空值缓存，短时间过期
    }
    return api;
}

// 读操作伪代码 - API网关调用验证（通过路径）
public InterfaceInfoVO getInterfaceInfo(String endpoint, String method) {
    // 1. 先通过路径映射获取API ID
    Long apiId = apiCacheService.getApiIdByPath(endpoint, method);
    
    // 2. 如果路径映射缓存未命中，查询数据库
    if (apiId == null) {
        ApiInfo apiInfo = apiInfoMapper.selectOne(
            new LambdaQueryWrapper<ApiInfo>()
                .eq(ApiInfo::getEndpoint, endpoint)
                .eq(ApiInfo::getMethod, method)
        );
        if (apiInfo == null) {
            return null;
        }
        apiId = apiInfo.getId();
        // 缓存路径映射
        apiCacheService.cachePathMapping(endpoint, method, apiId);
    }
    
    // 3. 通过ID获取API详情，转换为InterfaceInfoVO
    ApiVO apiVO = getApiDetailById(apiId);
    return convertToInterfaceInfoVO(apiVO);
}

// 写操作伪代码 - 更新API
@Transactional
public void updateApi(Long id, ApiCreateDTO dto) {
    // 1. 查询旧的API信息（用于处理endpoint变更）
    ApiInfo oldApiInfo = getById(id);
    String oldEndpoint = oldApiInfo.getEndpoint();
    String oldMethod = oldApiInfo.getMethod();
    
    // 2. 更新数据库
    ApiInfo apiInfo = updateDatabase(id, dto);
    
    // 3. 更新缓存
    ApiVO vo = convertToApiVO(apiInfo);
    apiCacheService.cacheApiDetail(id, vo);
    
    // 4. 处理路径映射变更
    if (!oldEndpoint.equals(apiInfo.getEndpoint()) || !oldMethod.equals(apiInfo.getMethod())) {
        // 删除旧的路径映射
        apiCacheService.deletePathMapping(oldEndpoint, oldMethod);
    }
    // 添加新的路径映射
    apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), id);
    
    // 5. 清除列表缓存
    clearListCache();
}

// 写操作伪代码 - 删除API
@Transactional
public void deleteApi(Long id) {
    ApiInfo apiInfo = getById(id);
    // 1. 删除数据库
    removeById(id);
    // 2. 删除所有缓存
    apiCacheService.deleteApiDetailCache(id);
    apiCacheService.deletePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod());
    clearListCache();
}
```

**需要更新缓存的场景**：

| 操作 | 缓存处理 |
|-----|---------|
| 创建API | 写入API详情缓存、路径映射缓存 |
| 更新API | 更新API详情缓存，处理路径映射变更 |
| 更新API状态 | 更新API详情缓存 |
| 审核API | 更新API详情缓存 |
| 删除API | 删除API详情缓存、路径映射缓存 |
| 同步统计数据 | 更新缓存中的统计字段 |

---

## 三、具体实现方案

### 3.1 需要缓存的方法

#### 3.1.1 高优先级（API调用场景）

| 方法 | 文件位置 | 缓存策略 |
|-----|---------|---------|
| `getInterfaceInfo(path, method)` | InnerInterfaceInfoServiceImpl | 路径映射缓存 + API详情缓存 |
| `getInterfaceInfoById(id)` | InnerInterfaceInfoServiceImpl | API详情缓存 |
| `invokeApi` 中的API查询 | ApiInvokeController | 使用API详情缓存 |

#### 3.1.2 中优先级（用户浏览场景）

| 方法 | 文件位置 | 缓存策略 |
|-----|---------|---------|
| `getApiDetailById(id)` | ApiInfoServiceImpl | API详情缓存 + 空值缓存 |
| `getApis(queryDTO)` | ApiInfoServiceImpl | 查询条件Hash + 短时缓存 |

#### 3.1.3 低优先级（管理场景）

| 方法 | 文件位置 | 缓存策略 |
|-----|---------|---------|
| 管理后台API列表 | ManagerController | 可不缓存或短时缓存 |

### 3.2 缓存更新策略

#### 3.2.1 写操作时主动更新缓存（推荐）

采用"永不过期+写时更新"策略，写操作时主动更新缓存：

```
// 更新API时
@Transactional
public ApiVO updateApi(Long userId, Long apiId, ApiCreateDTO updateDTO) {
    // 1. 查询旧的API信息（用于处理endpoint变更）
    ApiInfo oldApiInfo = getById(apiId);
    
    // 2. 更新数据库
    ApiInfo apiInfo = updateDatabase(apiId, updateDTO);
    
    // 3. 更新缓存
    updateAllApiCache(apiInfo, oldApiInfo);
    
    return vo;
}

// 更新所有API缓存方法
private void updateAllApiCache(ApiInfo apiInfo, ApiInfo oldApiInfo) {
    Long id = apiInfo.getId();
    
    // 更新API详情缓存
    ApiVO vo = convertToApiVO(apiInfo);
    apiCacheService.cacheApiDetail(id, vo);
    
    // 处理路径映射变更
    if (oldApiInfo != null) {
        if (!oldApiInfo.getEndpoint().equals(apiInfo.getEndpoint()) 
            || !oldApiInfo.getMethod().equals(apiInfo.getMethod())) {
            // 删除旧的路径映射
            apiCacheService.deletePathMapping(oldApiInfo.getEndpoint(), oldApiInfo.getMethod());
        }
    }
    // 添加新的路径映射
    apiCacheService.cachePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod(), id);
    
    // 清除空值缓存（如果存在）
    redisTemplate.delete(API_NULL_KEY + id);
    
    // 清除列表缓存
    clearListCache();
}

// 删除API时
@Transactional
public void deleteApi(Long id) {
    ApiInfo apiInfo = getById(id);
    // 1. 删除数据库
    removeById(id);
    // 2. 删除所有缓存
    apiCacheService.deleteApiDetailCache(id);
    apiCacheService.deletePathMapping(apiInfo.getEndpoint(), apiInfo.getMethod());
    clearListCache();
}
```

#### 3.2.2 缓存一致性保证

采用 **Write-Through Pattern**（写穿透模式）：
- **读**：先读缓存，缓存没有则读数据库并写入缓存（永不过期）
- **写**：先更新数据库，再更新缓存

**优势**：
1. 缓存永远是最新的
2. 不会出现缓存击穿和雪崩
3. 读操作性能最优

---

## 四、新增代码文件

### 4.1 缓存常量类

**文件路径**: `com.api.platform.constant.ApiCacheConstant`

```java
public class ApiCacheConstant {
    // 缓存Key前缀
    public static final String API_INFO_KEY = "api:info:";     // API详情缓存（包含所有API信息）
    public static final String API_PATH_KEY = "api:path:";     // API路径映射缓存
    public static final String API_LIST_KEY = "api:list:";     // API列表缓存
    public static final String API_NULL_KEY = "api:null:";     // 空值缓存
    
    // 过期时间（仅用于列表缓存和空值缓存）
    public static final long API_LIST_EXPIRE = 300;  // 5分钟
    public static final long API_NULL_EXPIRE = 120;  // 2分钟
    
    // API详情和路径映射缓存永不过期，采用写时更新策略
}
```

### 4.2 缓存服务类

**文件路径**: `com.api.platform.service.ApiCacheService`

```java
public interface ApiCacheService {
    // ========== API详情缓存操作 ==========
    ApiVO getApiDetailFromCache(Long id);
    void cacheApiDetail(Long id, ApiVO apiVO);
    void deleteApiDetailCache(Long id);
    
    // ========== 空值缓存操作 ==========
    void cacheNullValue(Long id);
    boolean isNullValueCached(Long id);
    
    // ========== API路径映射缓存操作 ==========
    Long getApiIdByPath(String endpoint, String method);
    void cachePathMapping(String endpoint, String method, Long apiId);
    void deletePathMapping(String endpoint, String method);
    
    // ========== 批量更新缓存（用于统计数据同步后更新） ==========
    void updateApiStatistics(Long id, Long invokeCount, Long successCount, Long failCount, BigDecimal rating);
}
```

**文件路径**: `com.api.platform.service.impl.ApiCacheServiceImpl`

```java
@Service
public class ApiCacheServiceImpl implements ApiCacheService {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // ========== API详情缓存 ==========
    @Override
    public ApiVO getApiDetailFromCache(Long id) {
        String key = API_INFO_KEY + id;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return null;
        }
        return convertMapToApiVO(entries);
    }
    
    @Override
    public void cacheApiDetail(Long id, ApiVO apiVO) {
        String key = API_INFO_KEY + id;
        Map<String, String> map = convertApiVOToMap(apiVO);
        stringRedisTemplate.opsForHash().putAll(key, map);
        // 永不过期
    }
    
    @Override
    public void deleteApiDetailCache(Long id) {
        stringRedisTemplate.delete(API_INFO_KEY + id);
    }
    
    // ========== 空值缓存 ==========
    @Override
    public void cacheNullValue(Long id) {
        String key = API_NULL_KEY + id;
        stringRedisTemplate.opsForValue().set(key, "1", API_NULL_EXPIRE, TimeUnit.SECONDS);
    }
    
    @Override
    public boolean isNullValueCached(Long id) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(API_NULL_KEY + id));
    }
    
    // ========== API路径映射缓存 ==========
    @Override
    public Long getApiIdByPath(String endpoint, String method) {
        String key = API_PATH_KEY + endpoint + ":" + method;
        String idStr = stringRedisTemplate.opsForValue().get(key);
        return idStr != null ? Long.parseLong(idStr) : null;
    }
    
    @Override
    public void cachePathMapping(String endpoint, String method, Long apiId) {
        String key = API_PATH_KEY + endpoint + ":" + method;
        stringRedisTemplate.opsForValue().set(key, apiId.toString());
        // 永不过期
    }
    
    @Override
    public void deletePathMapping(String endpoint, String method) {
        String key = API_PATH_KEY + endpoint + ":" + method;
        stringRedisTemplate.delete(key);
    }
    
    // ========== 批量更新统计信息 ==========
    @Override
    public void updateApiStatistics(Long id, Long invokeCount, Long successCount, Long failCount, BigDecimal rating) {
        String key = API_INFO_KEY + id;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            Map<String, String> updates = new HashMap<>();
            updates.put("invokeCount", invokeCount.toString());
            updates.put("successCount", successCount.toString());
            updates.put("failCount", failCount.toString());
            updates.put("rating", rating.toString());
            stringRedisTemplate.opsForHash().putAll(key, updates);
        }
    }
    
    // ... Map与ApiVO互转方法
}
```

---

## 五、实施步骤

### 步骤1：创建缓存常量类
创建 `ApiCacheConstant` 类定义缓存Key和过期时间常量

### 步骤2：创建缓存服务
创建 `ApiCacheService` 接口和实现类，封装缓存操作逻辑

### 步骤3：改造InnerInterfaceInfoServiceImpl
为Dubbo服务添加缓存支持（最高优先级，API调用场景）

### 步骤4：改造ApiInvokeController
使用缓存服务获取API信息

### 步骤5：改造ApiInfoServiceImpl
为API详情查询添加缓存支持

### 步骤6：添加缓存更新逻辑
在创建、更新、删除、审核API时更新缓存

### 步骤7：改造StatisticsSyncServiceImpl
统计数据同步后更新缓存中的统计字段

### 步骤8：测试验证
测试缓存命中、缓存穿透、写时更新等场景

---

## 六、预期效果

| 指标 | 优化前 | 优化后 | 提升 |
|-----|-------|-------|-----|
| API详情查询响应时间 | ~50ms | ~5ms | 90% |
| API调用验证响应时间 | ~30ms | ~3ms | 90% |
| 数据库QPS（API查询） | 1000+ | 100- | 90% |
| 系统并发能力 | 低 | 高 | 显著提升 |

---

## 七、注意事项

1. **缓存一致性**：所有写操作必须同步更新缓存，确保数据一致性
2. **内存管理**：由于采用永不过期策略，需要监控Redis内存使用，必要时可手动清理
3. **监控告警**：监控缓存命中率，及时发现异常
4. **灰度发布**：建议先在测试环境验证，再逐步上线
5. **统计数据更新**：定时任务同步统计数据后，需要同步更新缓存中的统计字段
6. **空值缓存**：空值缓存设置较短过期时间（2分钟），避免内存浪费
7. **列表缓存**：列表查询条件多变，不适合永不过期策略，采用短时缓存
