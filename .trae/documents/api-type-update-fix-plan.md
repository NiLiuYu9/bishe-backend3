# API分类编辑问题修复计划

## 问题描述
前端编辑API分类后：
1. 数据库数据没有被正确修改
2. 前端显示没有更新

## 问题分析

### 根本原因
MyBatis Plus配置了全局逻辑删除（`deleted`字段），导致：
1. `updateById()` 方法会自动添加 `WHERE deleted = 0` 条件
2. 当分类被禁用（`deleted = 1`）后，更新操作会失败

### 当前代码问题
```java
@Override
public void updateType(ApiType apiType) {
    // ...
    baseMapper.updateById(apiType);  // 这个方法仍然受逻辑删除影响
}
```

## 修复方案

### 方案一：添加忽略逻辑删除的更新方法（推荐）

在 `ApiTypeMapper.java` 中添加：
```java
@InterceptorIgnore(tenantLine = "true", illegalSql = "true", blockAttack = "true")
@Update("<script>" +
       "UPDATE api_type SET " +
       "name = #{name}, " +
       "description = #{description}, " +
       "update_time = NOW() " +
       "WHERE id = #{id}" +
       "</script>")
int updateByIdIgnoreLogicDelete(ApiType apiType);
```

在 `ApiTypeServiceImpl.java` 中使用：
```java
baseMapper.updateByIdIgnoreLogicDelete(apiType);
```

### 方案二：使用XML映射文件

在 `ApiTypeMapper.xml` 中定义更新方法，忽略逻辑删除。

## 实施步骤

1. 在 `ApiTypeMapper.java` 添加 `updateByIdIgnoreLogicDelete` 方法
2. 在 `ApiTypeServiceImpl.java` 使用新方法
3. 编译测试
