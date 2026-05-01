package com.api.platform.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus配置类
 * <p>核心职责：注册MyBatis-Plus拦截器，配置MySQL分页插件，
 * 使MyBatis-Plus的分页查询（Page对象）自动生效。</p>
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册MyBatis-Plus拦截器Bean
     * <p>添加MySQL分页插件，拦截分页查询SQL自动拼接LIMIT语句。</p>
     *
     * @return 配置好分页插件的MyBatis-Plus拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加MySQL分页插件，自动处理分页SQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
