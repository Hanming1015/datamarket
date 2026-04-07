package com.datamarket.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许跨域访问的路径：所有路径
                .allowedOriginPatterns("*") // 允许访问的源：所有源 (Spring Boot 2.4+ 推荐写法)
                .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH") // 允许的请求方法
                .allowCredentials(true) // 是否允许发送 Cookie/Token
                .allowedHeaders("*") // 允许的 Header
                .maxAge(3600); // 预检间隔时间 (秒)
    }
}