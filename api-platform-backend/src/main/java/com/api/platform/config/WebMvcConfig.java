package com.api.platform.config;

import com.api.platform.interceptor.RateLimitInterceptor;
import com.api.platform.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC配置类
 * <p>核心职责：注册拦截器（Session登录校验、接口限流）和配置跨域CORS策略。
 * 拦截器注册顺序决定执行顺序，Session拦截器先于限流拦截器执行。</p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    /**
     * 注册拦截器
     * <p>Session拦截器：校验用户登录状态，排除登录/注册/公开API/内部接口等路径。
     * 限流拦截器：基于令牌桶算法限制接口访问频率，排除与Session相同的公开路径。</p>
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Session登录校验拦截器：拦截所有路径，排除无需登录的公开接口
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/register",
                        "/api/list",
                        "/api/detail/**",
                        "/api/api-types",
                        "/api/statistics/**",
                        "/admin/api-types/all",
                        "/requirement/list",
                        "/requirement/detail/**",
                        "/invoke/**",
                        "/internal/**",
                        "/ws/**",
                        "/order/pay/notify",
                        "/error",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v2/**",
                        "/v3/**",
                        "/doc.html"
                );

        // 限流拦截器：对所有需登录的接口进行访问频率限制
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/register",
                        "/api/list",
                        "/api/detail/**",
                        "/api/api-types",
                        "/api/statistics/**",
                        "/admin/api-types/all",
                        "/requirement/list",
                        "/requirement/detail/**",
                        "/invoke/**",
                        "/internal/**",
                        "/ws/**",
                        "/order/pay/notify",
                        "/error",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v2/**",
                        "/v3/**",
                        "/doc.html"
                );
    }

    /**
     * 配置全局跨域策略
     * <p>允许所有来源、所有请求头、常用HTTP方法，支持携带Cookie，
     * 预检请求缓存3600秒。</p>
     *
     * @param registry CORS注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

}
