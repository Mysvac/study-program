package com.asaki0019.advertising.webConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * 添加跨域资源共享（CORS）映射配置
             * 该方法用于配置允许跨域请求的规则，使得前端应用可以跨越不同的域名进行API请求
             *
             * @param registry CorsRegistry对象，用于注册CORS映射规则
             */
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
//                        .allowedOrigins(
//                                "http://localhost:5173",
//                                "http://10.100.164.22:8080",
//                                "http://localhost:8080")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization", "Content-Type") // 暴露响应头
                        .allowCredentials(true);
            }
        };
    }
}