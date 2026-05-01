package com.api.platform.annotation;

import java.lang.annotation.*;

/**
 * 限流注解
 * <p>核心职责：标记需要限流的接口方法，配置令牌桶参数。
 * 由RateLimitInterceptor扫描此注解并执行限流逻辑。</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /** 令牌桶容量（最大令牌数），默认100 */
    int capacity() default 100;

    /** 令牌填充速率（每秒补充令牌数），默认10 */
    int refillRate() default 10;

    /** 自定义限流Key，为空时使用请求URI */
    String key() default "";

    /** 限流提示信息 */
    String message() default "请求过于频繁，请稍后再试";
}
