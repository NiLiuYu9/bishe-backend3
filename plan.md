# 优化前端仪表盘和平台统计的统计维度可选项

## 一、现状分析

### 1.1 当前统计页面

| 页面 | 位置 | 筛选维度 | 展示指标 |
|------|------|----------|----------|
| 管理员仪表盘 | `/admin/dashboard` | 仅日期范围(默认7天) | API总数、用户总数、订单总数、总收入、活跃用户趋势、API调用排行 |
| 管理员平台统计 | `/admin/statistics` | 日期范围 + API名称 | 日活用户、日访问量、API总数、总收入、平台趋势、API调用排行 |
| 用户统计分析 | `/user/statistics` | 日期范围 + API名称 | 调用次数、成功次数、失败次数、成功率、调用趋势 |

### 1.2 现有问题

1. **筛选维度单一**: 仅支持日期范围和API名称，缺少API分类、调用状态等维度
2. **时间选择不够便捷**: 没有快捷时间选项（今日、本周、本月等）
3. **图表指标固定**: 用户无法自定义选择要查看的指标
4. **缺少对比分析**: 无法进行环比、同比分析
5. **统计卡片信息有限**: 没有显示变化趋势

---

## 二、优化方案

### 2.1 增加时间快捷选项

**位置**: 所有统计页面的日期选择器

**新增选项**:
- 今日
- 昨日
- 本周
- 上周
- 本月
- 上月
- 最近7天
- 最近30天
- 最近90天

**实现方式**: 使用 Element Plus 的 `el-select` + `el-date-picker` 组合

### 2.2 增加API分类筛选

**位置**: 管理员平台统计、用户统计分析

**实现**:
- 前端：添加API分类下拉选择器，调用现有 `adminApi.getAllApiTypes()` 获取分类列表
- 后端：`StatisticsQueryDTO` 增加 `typeId` 字段，查询时关联 `ApiInfo` 表筛选

### 2.3 增加调用状态筛选

**位置**: 用户统计分析页面

**选项**:
- 全部
- 成功
- 失败

**实现**:
- 前端：添加状态下拉选择器
- 后端：`StatisticsQueryDTO` 增加 `status` 字段，查询时筛选对应状态的数据

### 2.4 增加图表指标选择

**位置**: 平台趋势图、调用趋势图

**可选指标**:
- 调用次数
- 成功次数
- 失败次数
- 成功率
- 活跃用户（仅管理员）

**实现方式**: 使用 `el-checkbox-group` 让用户多选要展示的指标

### 2.5 优化统计卡片

**新增功能**:
1. 显示环比变化百分比（与上一周期对比）
2. 使用颜色和箭头指示变化趋势（上升/下降）

**实现**:
- 后端返回当前周期和上一周期的数据
- 前端计算变化百分比并展示

### 2.6 增加排行榜维度切换

**位置**: API调用排行榜

**可选维度**:
- API调用排行（默认）
- API分类排行
- 用户调用排行（仅管理员）

---

## 三、详细实现计划

### 3.1 后端修改

#### 3.1.1 修改 `StatisticsQueryDTO.java`
```java
// 新增字段
private Long typeId;        // API分类ID
private String status;      // 调用状态: all/success/fail
private String timeRange;   // 快捷时间: today/yesterday/thisWeek/lastWeek/thisMonth/lastMonth/last7days/last30days/last90days
```

#### 3.1.2 修改 `PlatformStatisticsVO.java`
```java
// 新增字段用于环比分析
private Long prevTotalApis;
private Long prevTotalUsers;
private Long prevTotalOrders;
private BigDecimal prevTotalRevenue;
private Long prevDailyActiveUsers;
private Long prevDailyPageViews;
```

#### 3.1.3 修改 `ApiStatisticsVO.java`
```java
// 新增字段用于环比分析
private Long prevInvokeCount;
private Long prevSuccessCount;
private Long prevFailCount;
```

#### 3.1.4 修改 `ApiInvokeServiceImpl.java`
- 增加 `typeId` 筛选逻辑（关联ApiInfo表）
- 增加 `status` 筛选逻辑
- 增加快捷时间计算逻辑
- 增加上一周期数据计算逻辑

#### 3.1.5 新增接口
- `GET /admin/statistics/category-ranking` - 获取API分类调用排行
- `GET /admin/statistics/user-ranking` - 获取用户调用排行

### 3.2 前端修改

#### 3.2.1 创建公共组件

**新建 `src/components/statistics/TimeRangeSelector.vue`**
- 时间快捷选项选择器
- 支持自定义日期范围

**新建 `src/components/statistics/StatsCard.vue`**
- 统计卡片组件
- 支持显示环比变化

**新建 `src/components/statistics/IndicatorSelector.vue`**
- 图表指标多选组件

#### 3.2.2 修改 `admin/statistics.vue`
- 集成时间快捷选项
- 添加API分类筛选
- 添加图表指标选择
- 优化统计卡片显示环比

#### 3.2.3 修改 `admin/dashboard.vue`
- 集成时间快捷选项
- 添加图表指标选择
- 优化统计卡片显示环比

#### 3.2.4 修改 `user/statistics.vue`
- 集成时间快捷选项
- 添加API分类筛选
- 添加调用状态筛选
- 添加图表指标选择
- 优化统计卡片显示环比

#### 3.2.5 修改类型定义
- 更新 `PlatformStatistics` 接口
- 更新 `ApiStatistics` 接口
- 新增 `StatisticsQueryParams` 接口

---

## 四、文件修改清单

### 后端文件
| 文件 | 操作 | 说明 |
|------|------|------|
| `StatisticsQueryDTO.java` | 修改 | 增加typeId、status、timeRange字段 |
| `PlatformStatisticsVO.java` | 修改 | 增加环比数据字段 |
| `ApiStatisticsVO.java` | 修改 | 增加环比数据字段 |
| `ApiInvokeServiceImpl.java` | 修改 | 实现新增筛选逻辑和环比计算 |
| `AdminStatisticsController.java` | 修改 | 增加分类排行和用户排行接口 |
| `DailyStatsVO.java` | 修改 | 增加成功率字段 |

### 前端文件
| 文件 | 操作 | 说明 |
|------|------|------|
| `components/statistics/TimeRangeSelector.vue` | 新建 | 时间快捷选项组件 |
| `components/statistics/StatsCard.vue` | 新建 | 带环比的统计卡片组件 |
| `components/statistics/IndicatorSelector.vue` | 新建 | 图表指标选择组件 |
| `views/admin/statistics.vue` | 修改 | 集成新组件和功能 |
| `views/admin/dashboard.vue` | 修改 | 集成新组件和功能 |
| `views/user/statistics.vue` | 修改 | 集成新组件和功能 |
| `types/index.ts` | 修改 | 更新统计相关类型 |
| `types/api.ts` | 修改 | 更新统计相关类型 |
| `api/admin.ts` | 修改 | 增加新接口调用 |

---

## 五、实现优先级

### P0 - 核心功能（必须实现）
1. 时间快捷选项
2. API分类筛选
3. 统计卡片环比显示

### P1 - 重要功能
4. 图表指标选择
5. 调用状态筛选

### P2 - 增强功能
6. 排行榜维度切换
7. 用户调用排行

---

## 六、预期效果

优化后，用户可以：
1. 快速选择常用时间范围，无需手动选择日期
2. 按API分类筛选统计数据，便于分析不同类型API的表现
3. 按调用状态筛选，专注于分析成功或失败的调用
4. 自定义图表展示的指标，按需查看关键数据
5. 直观看到数据的变化趋势，了解业务增长情况
