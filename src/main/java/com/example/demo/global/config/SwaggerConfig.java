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
        String jwt = "JWT";

        // JWT 인증 방식 명시
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

        // JWT 보안 구성 요소 설정
        Components components = new Components().addSecuritySchemes(jwt,
                new SecurityScheme()
                        .name(jwt)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
        );

        // 실제 요청 서버 주소 입력 (배포 주소 or 상대경로 등)
        Server server = new Server();
        server.setUrl("https://api.cheer-up.net");

        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components)
                .addServersItem(server);
    }

    // API 정보 설정
    private Info apiInfo() {
        return new Info()
                .title("CheerUp API")
                .description("구름톤 유니브 3조 CheerUp 프로젝트 API 문서")
                .version("v1.0.0");
    }
}
