package com.seckill.config;

import com.seckill.interceptor.AdminInterceptor;
import com.seckill.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT认证拦截器 - 需要登录
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/admin/**", "/api/seckill/products", "/api/auth/info")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/seckill/products/public",
                        "/api/seckill/execute/**"
                );

        // 管理员权限拦截器 - 需要管理员角色
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**");
    }
}
