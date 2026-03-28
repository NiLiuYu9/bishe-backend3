package com.api.platform.config;

import com.api.platform.interceptor.RateLimitInterceptor;
import com.api.platform.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
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
