# Tasks

- [x] Task 1: 修复后端 RateLimiter —— Lua 脚本毫秒级精度 + DefaultRedisScript 单例
  - [x] SubTask 1.1: 修改 Lua 脚本，使用毫秒级时间戳，修正令牌补充时间推进逻辑（仅推进实际消耗的时间）
  - [x] SubTask 1.2: 将 DefaultRedisScript 改为类级别 static final 常量
  - [x] SubTask 1.3: 修改 tryAcquire 方法，传入毫秒级时间戳，refillRate 参数语义保持"每秒令牌数"（方法内部除以1000传给Lua）

- [x] Task 2: 修复网关 RateLimiter —— 与后端保持一致
  - [x] SubTask 2.1: 修改 Lua 脚本，与后端完全一致
  - [x] SubTask 2.2: 将 DefaultRedisScript 改为类级别 static final 常量
  - [x] SubTask 2.3: 修改 tryAcquire 方法，与后端一致

- [x] Task 3: 修复网关 RateLimitFilter —— 固定限流参数 + 未识别用户拒绝
  - [x] SubTask 3.1: 移除 callLimit 动态参数逻辑，固定 capacity=2, refillRate=2
  - [x] SubTask 3.2: 未识别用户时返回 401 而非放行
  - [x] SubTask 3.3: 更新错误提示信息

- [x] Task 4: 修复后端 ApiInvokeController —— 固定限流参数
  - [x] SubTask 4.1: 移除 callLimit 动态参数逻辑，固定 capacity=2, refillRate=2
  - [x] SubTask 4.2: 更新错误提示信息

- [x] Task 5: 为 TestController.testCall 新增限流
  - [x] SubTask 5.1: 注入 RateLimiter，在 testCall 方法中添加限流检查（capacity=2, refillRate=2，Key=test:userId:apiId）

- [x] Task 6: 移除业务操作的 @RateLimit 注解
  - [x] SubTask 6.1: 移除 OrderController 上的 @RateLimit 注解
  - [x] SubTask 6.2: 移除 ApiController 上的 @RateLimit 注解（3处）

- [x] Task 7: 编译验证
  - [x] SubTask 7.1: 后端 `mvn clean compile` 通过
  - [x] SubTask 7.2: 云模块 `mvn clean compile` 通过

# Task Dependencies
- Task 1 和 Task 2 可并行
- Task 3 依赖 Task 2
- Task 4 和 Task 5 依赖 Task 1
- Task 6 无依赖，可随时执行
- Task 7 依赖 Task 1-6 全部完成
