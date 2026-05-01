# 修复限流逻辑 Spec

## Why
当前限流实现存在严重缺陷：令牌桶参数 `refillRate = capacity` 导致限流形同虚设、Lua 脚本精度丢失、两层限流职责混乱。用户只需要在两个场景限流：测试调用每秒2次、AK/SK调用每秒2次，不需要对其他业务操作限流。

## What Changes
- 移除 OrderController 和 ApiController 上的 `@RateLimit` 注解（不需要业务操作限流）
- 修复 Lua 令牌桶脚本精度丢失问题（秒级→毫秒级）
- 修复网关 RateLimitFilter：固定 capacity=2, refillRate=2（每秒2次），移除错误的 `refillRate = capacity` 逻辑
- 修复后端 ApiInvokeController：固定 capacity=2, refillRate=2
- 为 TestController.testCall 新增限流（capacity=2, refillRate=2）
- 优化 DefaultRedisScript 重复创建的性能问题（改为类级别常量）
- 修复网关层未识别用户直接放行的安全漏洞
- 两层限流使用不同 Redis Key 前缀隔离

## Impact
- Affected code:
  - `api-platform-cloud/.../filter/RateLimitFilter.java` — 修复 refillRate、未识别用户处理
  - `api-platform-cloud/.../ratelimit/RateLimiter.java` — 修复 Lua 脚本、DefaultRedisScript 单例
  - `api-platform-backend/.../ratelimit/RateLimiter.java` — 修复 Lua 脚本、DefaultRedisScript 单例
  - `api-platform-backend/.../controller/ApiInvokeController.java` — 修复限流参数
  - `api-platform-backend/.../controller/TestController.java` — 新增限流
  - `api-platform-backend/.../controller/OrderController.java` — 移除 @RateLimit
  - `api-platform-backend/.../controller/ApiController.java` — 移除 @RateLimit

---

## 问题详细分析

### 问题1：`refillRate = capacity` —— 令牌桶限流形同虚设（致命）

**网关 RateLimitFilter 第68行：**
```java
int refillRate = capacity;  // BUG: 桶每秒完全填满
```

**后端 ApiInvokeController 第119行：**
```java
rateLimiter.tryAcquire(rateLimitKey, callLimit, callLimit)  // BUG: 同上
```

当 `callLimit = 100` 时，capacity=100, refillRate=100，桶每秒完全填满，等效于无限制。

### 问题2：Lua 脚本精度丢失

使用秒级时间戳 + `math.floor(elapsed * refillRate)`：
- 同一秒内多次请求时 `elapsed = 0`，不补充令牌
- `lastRefillTime = now` 无条件更新，丢失小数时间进度

### 问题3：TestController 无限流

测试调用接口 `/test/call` 没有任何频率限制，可被高频调用。

### 问题4：DefaultRedisScript 每次调用重新创建

`RateLimiter.tryAcquire()` 每次都 `new DefaultRedisScript<>()`。

### 问题5：网关未识别用户直接放行

```java
if (userId == null) {
    return chain.filter(exchange);  // 静默放行
}
```

### 问题6：业务操作不需要限流但加了 @RateLimit

OrderController 和 ApiController 上的 `@RateLimit` 注解不需要。

---

## ADDED Requirements

### Requirement: 测试调用限流
系统 SHALL 对测试调用接口（`/test/call`）实施令牌桶限流，每用户每API每秒最多2次请求。

#### Scenario: 测试调用频率限制
- **WHEN** 用户对某API发起测试调用
- **THEN** 令牌桶 capacity=2, refillRate=2，每秒最多允许2次测试调用

### Requirement: AK/SK 调用限流
系统 SHALL 对通过 AK/SK 鉴权的 API 调用实施令牌桶限流，每用户每API每秒最多2次请求。

#### Scenario: 网关 AK/SK 调用频率限制
- **WHEN** 用户通过网关以 AK/SK 方式调用 API
- **THEN** 令牌桶 capacity=2, refillRate=2，每秒最多允许2次调用

#### Scenario: 后端代理 AK/SK 调用频率限制
- **WHEN** 用户通过后端 `/invoke/call` 以 AK/SK 方式调用 API
- **THEN** 令牌桶 capacity=2, refillRate=2，每秒最多允许2次调用

### Requirement: Lua 脚本毫秒级精度
系统 SHALL 使用毫秒级时间戳执行令牌桶计算，避免秒级精度导致的令牌补充不连续。

#### Scenario: 高频请求下的令牌补充
- **WHEN** refillRate=2，在500毫秒内连续请求
- **THEN** 每500ms补充1个令牌，令牌补充平滑连续

### Requirement: DefaultRedisScript 单例复用
系统 SHALL 将 DefaultRedisScript 作为类级别常量。

### Requirement: 网关未识别用户拒绝请求
系统 SHALL 在网关限流过滤器中，当无法识别用户身份时拒绝请求。

#### Scenario: 无法解析 userId
- **WHEN** RateLimitFilter 无法从请求中解析 userId
- **THEN** 返回 401 Unauthorized 响应

## MODIFIED Requirements

### Requirement: 令牌桶限流器（Lua 脚本）
修改 Lua 脚本使用毫秒级时间戳：
- 时间戳单位从秒改为毫秒
- refillRate 传入时除以1000（每毫秒令牌数）
- 补充令牌时仅推进实际消耗的时间，避免时间进度丢失
- 过期时间保持 3600 秒

### Requirement: 网关 RateLimitFilter
- 移除基于 callLimit 的动态参数计算
- 固定 capacity=2, refillRate=2
- 未识别用户返回 401

### Requirement: 后端 ApiInvokeController
- 移除基于 callLimit 的动态参数计算
- 固定 capacity=2, refillRate=2

## REMOVED Requirements

### Requirement: 业务操作限流（@RateLimit on OrderController/ApiController）
**Reason**: 用户明确表示不需要对业务操作限流
**Migration**: 移除 OrderController 和 ApiController 上的 @RateLimit 注解，保留 @RateLimit 注解定义和拦截器基础设施以备后续使用
