# API调用次数统计功能设计方案（最终版）

## 一、需求分析

### 1.1 业务需求
- 统计各个API的调用次数
- 记录调用用户ID
- 记录调用日期
- 统计调用成功次数和失败次数

### 1.2 用户统计需求
- **用户调用统计**：用户自己调用其他API的次数
- **API被调用统计**：用户上架的API被其他用户调用的次数

---

## 二、技术方案

### 2.1 Redis + 数据库混合方案

**设计思路**：
1. **Redis**：实时记录调用次数，性能高，支持原子操作
2. **数据库**：持久化存储，支持复杂查询和报表

**数据流程**：
```
API调用 → Redis INCR计数 → 定时任务同步 → 数据库持久化
```

### 2.2 Redis Key设计

通过`api_owner_id`字段区分调用和被调用，合并为一个Key：

```
# API调用统计（统一Key，包含调用者、API所有者、API ID、日期）
invoke:{callerId}:{ownerId}:{apiId}:{date}  # Hash: total, success, fail
```

**查询方式**：
- 查询用户调用统计：`invoke:{userId}:*:*:{date}` （userId作为调用者）
- 查询用户API被调用统计：`invoke:*:{userId}:*:{date}` （userId作为API所有者）

---

## 三、数据库设计

### 3.1 API调用每日统计表 `api_invoke_daily`

统一记录所有调用统计，通过`caller_id`和`api_owner_id`区分调用和被调用。

```sql
CREATE TABLE `api_invoke_daily` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `api_id` BIGINT(20) NOT NULL COMMENT 'API ID',
    `api_name` VARCHAR(100) NOT NULL COMMENT 'API名称（冗余存储，便于筛选）',
    `caller_id` BIGINT(20) NOT NULL COMMENT '调用者用户ID',
    `api_owner_id` BIGINT(20) NOT NULL COMMENT 'API所有者用户ID',
    `stat_date` DATE NOT NULL COMMENT '统计日期',
    `total_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '总调用次数',
    `success_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '成功次数',
    `fail_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '失败次数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_api_caller_date` (`api_id`, `caller_id`, `stat_date`),
    KEY `idx_api_id` (`api_id`),
    KEY `idx_api_name` (`api_name`),
    KEY `idx_caller_id` (`caller_id`),
    KEY `idx_api_owner_id` (`api_owner_id`),
    KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API调用每日统计表';
```

**字段说明**：
- `caller_id`：调用API的用户ID（谁在调用）
- `api_owner_id`：API所有者用户ID（API属于谁）
- `api_id`：被调用的API ID
- `api_name`：API名称（冗余存储，便于按名称筛选）

**查询示例**：
```sql
-- 查询平台所有API的调用统计（支持按API名筛选）
SELECT * FROM api_invoke_daily 
WHERE stat_date BETWEEN #{startDate} AND #{endDate}
AND (#{apiName} IS NULL OR api_name LIKE CONCAT('%', #{apiName}, '%'));

-- 查询用户调用其他API的统计（支持按API名筛选）
SELECT * FROM api_invoke_daily 
WHERE caller_id = #{userId} 
AND stat_date BETWEEN #{startDate} AND #{endDate}
AND (#{apiName} IS NULL OR api_name LIKE CONCAT('%', #{apiName}, '%'));

-- 查询用户上架的API被调用的统计（支持按API名筛选）
SELECT * FROM api_invoke_daily 
WHERE api_owner_id = #{userId} 
AND stat_date BETWEEN #{startDate} AND #{endDate}
AND (#{apiName} IS NULL OR api_name LIKE CONCAT('%', #{apiName}, '%'));

-- 查询指定API的调用统计
SELECT * FROM api_invoke_daily 
WHERE api_id = #{apiId} 
AND stat_date BETWEEN #{startDate} AND #{endDate};
```

---

## 四、后端接口设计

### 4.1 管理员统计接口

#### 接口1：获取平台API调用统计
- **路径**：`GET /admin/statistics`
- **参数**：
  - `startDate` (可选)：开始日期
  - `endDate` (可选)：结束日期
  - `apiName` (可选)：API名称（模糊匹配）
- **响应**：
```json
{
    "code": 200,
    "data": {
        "totalApis": 128,
        "totalUsers": 1024,
        "totalOrders": 356,
        "totalRevenue": 56800.00,
        "dailyActiveUsers": 256,
        "dailyPageViews": 3580,
        "apiCallRanking": [
            {"apiId": 1, "apiName": "天气查询", "invokeCount": 125680},
            {"apiId": 2, "apiName": "身份证识别", "invokeCount": 89560}
        ],
        "dailyStats": [
            {"date": "2024-01-01", "activeUsers": 200, "pageViews": 3000, "newUsers": 15, "newOrders": 10}
        ]
    }
}
```

### 4.2 用户统计接口

#### 接口2：获取用户调用统计（用户调用其他API）
- **路径**：`GET /api/statistics/my-invoke`
- **参数**：
  - `userId` (必填)：用户ID
  - `startDate` (可选)：开始日期
  - `endDate` (可选)：结束日期
  - `apiName` (可选)：API名称（模糊匹配）
- **响应**：
```json
{
    "code": 200,
    "data": {
        "invokeCount": 125680,
        "successCount": 125000,
        "failCount": 680,
        "dailyStats": [
            {"date": "2024-01-01", "invokeCount": 1000, "successCount": 980, "failCount": 20}
        ]
    }
}
```

#### 接口3：获取用户API被调用统计（用户上架的API被调用）
- **路径**：`GET /api/statistics/my-api-invoke`
- **参数**：
  - `userId` (必填)：用户ID
  - `startDate` (可选)：开始日期
  - `endDate` (可选)：结束日期
  - `apiName` (可选)：API名称（模糊匹配）
- **响应**：
```json
{
    "code": 200,
    "data": {
        "invokeCount": 125680,
        "successCount": 125000,
        "failCount": 680,
        "dailyStats": [
            {"date": "2024-01-01", "invokeCount": 1000, "successCount": 980, "failCount": 20}
        ]
    }
}
```

#### 接口4：获取指定API的调用统计
- **路径**：`GET /api/statistics/{apiId}`
- **参数**：
  - `apiId` (路径参数)：API ID
  - `startDate` (可选)：开始日期
  - `endDate` (可选)：结束日期
- **响应**：
```json
{
    "code": 200,
    "data": {
        "invokeCount": 125680,
        "successCount": 125000,
        "failCount": 680,
        "dailyStats": [
            {"date": "2024-01-01", "invokeCount": 1000, "successCount": 980, "failCount": 20}
        ]
    }
}
```

---

## 五、实现方案

### 5.1 数据收集流程

```
API调用 → Redis计数 → 返回响应
                ↓
         定时任务（每小时）
                ↓
         同步到数据库
```

### 5.2 Redis操作示例

```java
String date = LocalDate.now().toString();

// 统一Key：invoke:{callerId}:{ownerId}:{apiId}:{date}
String key = String.format("invoke:%d:%d:%d:%s", callerId, apiOwnerId, apiId, date);
redisTemplate.opsForHash().increment(key, "total", 1);
redisTemplate.opsForHash().increment(key, success ? "success" : "fail", 1);
```

### 5.3 代码实现清单

#### 实体类
1. `ApiInvokeDaily.java` - API每日统计实体

#### DTO/VO
1. `StatisticsQueryDTO.java` - 统计查询参数DTO（包含userId、apiName、startDate、endDate）
2. `PlatformStatisticsVO.java` - 平台统计响应VO
3. `ApiStatisticsVO.java` - API统计响应VO
4. `ApiCallRankingVO.java` - API调用排行VO
5. `DailyStatsVO.java` - 每日统计VO

#### Mapper
1. `ApiInvokeDailyMapper.java` - 继承BaseMapper

#### Service
1. `ApiInvokeService.java` - 统计记录和查询服务（Redis操作）
2. `StatisticsSyncService.java` - 数据同步服务（定时任务）

#### Controller
1. `AdminStatisticsController.java` - 管理员统计接口
2. 在 `ApiInfoController.java` 中添加统计接口

### 5.4 调用链路改造

修改 `InnerUserInterfaceInfoServiceImpl.invokeCount()` 方法：
```java
@Override
public void invokeCount(Long interfaceInfoId, Long userId) {
    // 原有逻辑...
    
    // 新增：Redis记录调用统计
    ApiInfo apiInfo = apiInfoMapper.selectById(interfaceInfoId);
    // callerId = userId (调用者)
    // apiOwnerId = apiInfo.getUserId() (API所有者)
    // apiName = apiInfo.getName() (API名称)
    apiInvokeService.recordInvoke(interfaceInfoId, apiInfo.getName(), userId, apiInfo.getUserId(), true);
}
```

---

## 六、任务清单

### 阶段一：数据库表创建
- [ ] 创建 `api_invoke_daily` 表

### 阶段二：实体类和Mapper
- [ ] 创建 `ApiInvokeDaily` 实体类
- [ ] 创建 `ApiInvokeDailyMapper` 接口

### 阶段三：DTO/VO类
- [ ] 创建 `StatisticsQueryDTO`
- [ ] 创建 `PlatformStatisticsVO`
- [ ] 创建 `ApiStatisticsVO`
- [ ] 创建 `ApiCallRankingVO`
- [ ] 创建 `DailyStatsVO`

### 阶段四：Service层
- [ ] 创建 `ApiInvokeService` 接口
- [ ] 创建 `ApiInvokeServiceImpl` 实现类（Redis操作）
- [ ] 创建 `StatisticsSyncService` 接口
- [ ] 创建 `StatisticsSyncServiceImpl` 实现类（定时同步）

### 阶段五：Controller层
- [ ] 创建 `AdminStatisticsController`
- [ ] 在 `ApiInfoController` 中添加统计接口

### 阶段六：集成调用链路
- [ ] 修改 `InnerUserInterfaceInfoServiceImpl` 集成统计记录

### 阶段七：测试验证
- [ ] 编译项目确保无错误
