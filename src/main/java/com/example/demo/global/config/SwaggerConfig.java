package com.example.demo.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
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
                        .version("v1.0.1"))

                // 현재 도메인 기준으로 API 요청이 나가도록 상대 경로 사용
                .addServersItem(new Server().url("/"))

                // 보안 설정 추가 (JWT 토큰 인증 방식 명시)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

                // JWT 인증 방식에 대한 세부 설정
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }
}
