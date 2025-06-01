package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@SpringBootApplication
public class BackendApplication {

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Configuration
	public class OpenApiConfig {
		@Bean
		public OpenAPI cheerUpOpenAPI() {
			return new OpenAPI()
					.info(new Info()
							.title("CheerUp API")
							.version("1.0.1")
							.description("CheerUp 서비스 API 문서")
					)
					.servers(List.of(
							new Server().url("/")  // ← 상대 경로로 변경
					));
		}
	}
}
