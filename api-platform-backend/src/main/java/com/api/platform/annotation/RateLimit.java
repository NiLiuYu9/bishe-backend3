package com.api.platform.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    int capacity() default 100;

    int refillRate() default 10;

    String key() default "";

    String message() default "请求过于频繁，请稍后再试";
}
