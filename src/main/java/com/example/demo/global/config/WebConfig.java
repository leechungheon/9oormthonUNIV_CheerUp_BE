package com.example.demo.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 URL 패턴에 대해
                        .allowedOrigins("*") // 모든 Origin 허용 (보안상 실제 운영에서는 제한하는 게 좋음)
                        .allowedMethods("*") // 모든 HTTP Method 허용 (GET, POST, PUT, DELETE 등)
                        .allowedHeaders("*") // 모든 헤더 허용
                        .allowCredentials(false); // 인증 정보 포함 여부 (쿠키, Authorization 헤더 등)
            }
        };
    }
}
