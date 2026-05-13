package com.example.study.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * app 自己接入 MyBatis-Plus；数据库能力暂时不沉到 common。
 *
 * <p>这里集中扫描所有 Demo Mapper，避免每个学习模块重复写配置。</p>
 */
@Configuration(proxyBeanMethods = false)
@MapperScan({
        "com.example.study.demo.mybatis.blog.mapper",
        "com.example.study.demo.auth.mapper",
        "com.example.study.demo.user.mapper"
})
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
