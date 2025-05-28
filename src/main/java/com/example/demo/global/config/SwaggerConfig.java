package com.example.demo.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("CheerUp API")
                        .description("구름톤 유니브 3조 CheerUp 프로젝트 API 문서")
                        .version("v1.0.0"))

                // 보안 설정 추가 (JWT 토큰 인증 방식 명시)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

                // JWT 인증 방식에 대한 세부 설정
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP) // HTTP 방식의 인증 사용
                                .scheme("bearer") // 'Bearer' 인증 방식 (토큰 기반)
                                .bearerFormat("JWT") // JWT 형식 사용 명시
                ));
    }
}