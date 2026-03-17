# 前后端功能对齐计划

## 背景

通过分析发现前端有一些定义但未使用的接口，以及后端缺失的测试记录相关接口。

## 需要处理的问题

### 1. 删除前端未使用的API删除接口

前端定义了 `DELETE /api/delete/{id}` 接口，但API删除已改为下架机制，需要清理相关代码。

**涉及文件：**

* `api-platform-frontend/src/api/api.ts` - 删除 `delete` 方法

* `api-platform-frontend/src/config/index.ts` - 删除 `api.delete` 端点配置

### 2. 删除前端未使用的API提交审核接口

前端定义了 `POST /api/audit/{id}` 接口，但提交审核是在上架API时自动进行的，无需单独接口。

**涉及文件：**

* `api-platform-frontend/src/api/api.ts` - 删除 `submitAudit` 方法

* `api-platform-frontend/src/config/index.ts` - 删除 `api.audit` 端点配置

### 3. 删除前端未使用的管理员用户详情接口

前端定义了 `GET /admin/user-detail/{id}` 接口，但没有被实际使用。

**涉及文件：**

* `api-platform-frontend/src/api/admin.ts` - 删除 `getUserDetail` 方法

* `api-platform-frontend/src/config/index.ts` - 删除 `admin.userDetail` 端点配置

### 4. 实现测试记录后端接口

数据库已有 `api_test_record` 表，后端已有 `ApiTestRecord` 实体类，需要实现以下接口：

#### 后端需要创建的文件和接口：

**新建文件：**

* `ApiTestRecordMapper.java` - Mapper接口

* `ApiTestRecordService.java` - Service接口

* `ApiTestRecordServiceImpl.java` - Service实现

**修改文件：**

* `TestController.java` - 添加以下接口：

  * `POST /test/save-record` - 保存测试记录

  * `GET /test/records` - 获取测试记录列表（不需要分页）

  * `DELETE /test/records/{id}` - 删除测试记录

  * `GET /test/records/count` - 获取用户对某API的测试记录数量

**业务规则：**

* 每个用户每个API限制保存5条测试记录

* 保存前检查记录数量，返回是否已满

#### 前端需要修改：

**修改文件：**

* `api-platform-frontend/src/api/test.ts` - 添加删除记录接口

* `api-platform-frontend/src/views/api/test.vue` - 修改保存逻辑：

  * 保存前先检查记录数量

  * 如果已满5条，弹窗让用户选择删除一条已有记录或取消保存

## 实施步骤

### 步骤1：删除前端未使用的API删除相关代码

1. 编辑 `api-platform-frontend/src/api/api.ts`，删除 `delete` 方法
2. 编辑 `api-platform-frontend/src/config/index.ts`，删除 `api.delete` 配置

### 步骤2：删除前端未使用的API审核相关代码

1. 编辑 `api-platform-frontend/src/api/api.ts`，删除 `submitAudit` 方法
2. 编辑 `api-platform-frontend/src/config/index.ts`，删除 `api.audit` 配置

### 步骤3：删除前端未使用的管理员用户详情相关代码

1. 编辑 `api-platform-frontend/src/api/admin.ts`，删除 `getUserDetail` 方法
2. 编辑 `api-platform-frontend/src/config/index.ts`，删除 `admin.userDetail` 配置

### 步骤4：实现后端测试记录接口

1. 创建 `ApiTestRecordMapper.java`
2. 创建 `ApiTestRecordService.java` 接口
3. 创建 `ApiTestRecordServiceImpl.java` 实现
4. 修改 `TestController.java`，添加测试记录相关接口

### 步骤5：修改前端测试记录功能

1. 编辑 `api-platform-frontend/src/api/test.ts`，添加删除和计数接口
2. 编辑 `api-platform-frontend/src/views/api/test.vue`，实现保存前检查和选择删除逻辑

## 数据库说明

`api_test_record` 表已存在，无需创建新表。表结构如下：

* id: 主键

* api\_id: API ID

* api\_name: API名称

* user\_id: 用户ID

* params: 请求参数JSON

* result: 响应结果JSON

* success: 是否成功

* error\_msg: 错误信息

* response\_time: 响应时间

* status\_code: HTTP状态码

* create\_time: 创建时间

