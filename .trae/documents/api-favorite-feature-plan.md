# API收藏功能实施计划

## 功能概述
在API卡片的右上角添加爱心按钮，用于表示当前用户是否收藏了该API。已收藏显示填充爱心，未收藏显示空心爱心。后端同步支持收藏相关的接口。

---

## 一、数据库设计

### 新建收藏表 `api_favorite`

```sql
CREATE TABLE `api_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `api_id` bigint NOT NULL COMMENT 'API ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_api`(`user_id`, `api_id`),
  INDEX `idx_user_id`(`user_id`),
  INDEX `idx_api_id`(`api_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API收藏表';
```

**设计说明**：
- 使用 `user_id + api_id` 唯一索引防止重复收藏
- 添加 `user_id` 和 `api_id` 单独索引优化查询性能
- 不需要 `deleted` 字段，取消收藏直接删除记录
- MySQL InnoDB 默认使用 B+树索引，无需显式指定索引类型

---

## 二、后端实现

### 2.1 新建实体类
**文件**: `api-platform-backend/src/main/java/com/api/platform/entity/ApiFavorite.java`

字段：
- `id`: Long - 主键ID
- `userId`: Long - 用户ID
- `apiId`: Long - API ID
- `createTime`: LocalDateTime - 收藏时间

### 2.2 新建Mapper
**文件**: `api-platform-backend/src/main/java/com/api/platform/mapper/ApiFavoriteMapper.java`

继承 `BaseMapper<ApiFavorite>`，添加自定义方法：
- `selectUserFavoriteApiIds(Long userId)` - 查询用户收藏的所有API ID列表

### 2.3 新建Service
**文件**: `api-platform-backend/src/main/java/com/api/platform/service/ApiFavoriteService.java`

方法定义：
- `addFavorite(Long userId, Long apiId)` - 添加收藏
- `removeFavorite(Long userId, Long apiId)` - 取消收藏
- `isFavorited(Long userId, Long apiId)` - 检查是否已收藏
- `getUserFavoriteApiIds(Long userId)` - 获取用户收藏的API ID列表
- `getUserFavorites(Long userId, Integer pageNum, Integer pageSize)` - 分页获取用户收藏的API列表

**文件**: `api-platform-backend/src/main/java/com/api/platform/service/impl/ApiFavoriteServiceImpl.java`

实现上述接口方法

### 2.4 新建VO
**文件**: `api-platform-backend/src/main/java/com/api/platform/vo/ApiFavoriteVO.java`

字段：
- `apiId`: Long - API ID
- `apiName`: String - API名称
- `typeName`: String - 类型名称
- `method`: String - 请求方法
- `price`: BigDecimal - 价格
- `priceUnit`: String - 计费单位
- `rating`: BigDecimal - 评分
- `invokeCount`: Long - 调用次数
- `favoriteTime`: LocalDateTime - 收藏时间

### 2.5 修改ApiVO
**文件**: `api-platform-backend/src/main/java/com/api/platform/vo/ApiVO.java`

新增字段：
- `isFavorited`: Boolean - 当前用户是否已收藏

### 2.6 新建Controller
**文件**: `api-platform-backend/src/main/java/com/api/platform/controller/ApiFavoriteController.java`

接口设计：

| 接口 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 添加收藏 | POST | `/api/favorite/add/{apiId}` | 收藏指定API |
| 取消收藏 | DELETE | `/api/favorite/remove/{apiId}` | 取消收藏 |
| 检查收藏状态 | GET | `/api/favorite/check/{apiId}` | 检查是否已收藏 |
| 获取收藏列表 | GET | `/api/favorite/list` | 分页获取收藏的API列表 |

### 2.7 修改ApiInfoService
**文件**: `api-platform-backend/src/main/java/com/api/platform/service/impl/ApiInfoServiceImpl.java`

修改 `getApis` 方法，在返回结果中填充 `isFavorited` 字段：
- 从Session获取当前用户ID
- 如果用户已登录，批量查询用户收藏的API ID
- 设置每个API的 `isFavorited` 字段

---

## 三、前端实现

### 3.1 修改类型定义
**文件**: `api-platform-frontend/src/types/api.ts`

在 `ApiItem` 接口中新增：
```typescript
isFavorited?: boolean
```

新增 `ApiFavoriteItem` 接口：
```typescript
export interface ApiFavoriteItem {
  apiId: number
  apiName: string
  typeName: string
  method: string
  price: number
  priceUnit: string
  rating: number
  invokeCount: number
  favoriteTime: string
}
```

### 3.2 新增API接口
**文件**: `api-platform-frontend/src/api/api.ts`

新增方法：
```typescript
addFavorite(apiId: number)        // 添加收藏
removeFavorite(apiId: number)     // 取消收藏
checkFavorite(apiId: number)      // 检查收藏状态
getFavoriteList(params)           // 获取收藏列表
```

### 3.3 修改API端点配置
**文件**: `api-platform-frontend/src/config/index.ts`

在 `apiEndpoints` 中添加收藏相关端点：
```typescript
favorite: {
  add: '/api/favorite/add',
  remove: '/api/favorite/remove',
  check: '/api/favorite/check',
  list: '/api/favorite/list'
}
```

### 3.4 修改API卡片组件

#### 3.4.1 API市场列表页
**文件**: `api-platform-frontend/src/views/api/list.vue`

修改内容：
1. 在 `.api-card` 右上角添加爱心按钮
2. 使用 `@click.stop` 阻止事件冒泡
3. 根据登录状态显示/隐藏按钮
4. 实现收藏/取消收藏的切换逻辑
5. 使用 Element Plus 的图标：
   - 已收藏：`<el-icon color="#f56c6c"><StarFilled /></el-icon>`
   - 未收藏：`<el-icon><Star /></el-icon>`

#### 3.4.2 首页热门API
**文件**: `api-platform-frontend/src/views/home/index.vue`

修改内容同上

### 3.5 新增收藏列表页面（可选）
**文件**: `api-platform-frontend/src/views/user/favorites.vue`

功能：
- 分页展示用户收藏的API列表
- 支持取消收藏
- 点击跳转到API详情

### 3.6 添加路由配置
**文件**: `api-platform-frontend/src/router/index.ts`

添加收藏列表页面路由（如果实现）

---

## 四、实施步骤

### 步骤1：数据库
1. 创建 `api_favorite` 表

### 步骤2：后端
1. 创建 `ApiFavorite` 实体类
2. 创建 `ApiFavoriteMapper` 接口
3. 创建 `ApiFavoriteService` 接口
4. 创建 `ApiFavoriteServiceImpl` 实现类
5. 创建 `ApiFavoriteVO` 视图对象
6. 修改 `ApiVO`，添加 `isFavorited` 字段
7. 创建 `ApiFavoriteController` 控制器
8. 修改 `ApiInfoServiceImpl`，在查询API列表时填充收藏状态

### 步骤3：前端
1. 修改 `types/api.ts`，添加类型定义
2. 修改 `config/index.ts`，添加API端点
3. 修改 `api/api.ts`，添加收藏相关接口
4. 修改 `views/api/list.vue`，添加爱心按钮
5. 修改 `views/home/index.vue`，添加爱心按钮

### 步骤4：测试验证
1. 测试添加收藏功能
2. 测试取消收藏功能
3. 测试收藏状态显示
4. 测试未登录状态下的显示
5. 测试API列表页和首页的一致性

---

## 五、注意事项

1. **性能优化**：在获取API列表时，批量查询收藏状态，避免N+1查询问题
2. **登录状态**：未登录用户不显示收藏按钮或点击时提示登录
3. **事件冒泡**：点击收藏按钮时使用 `@click.stop` 阻止事件冒泡，避免触发卡片点击
4. **防抖处理**：收藏按钮点击添加防抖，避免重复请求
5. **数据一致性**：取消收藏后及时更新前端状态
