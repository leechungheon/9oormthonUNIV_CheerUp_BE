package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class BackendApplication {
	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {

    	/*
    	Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    	dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));*/
		SpringApplication.run(BackendApplication.class, args);
	}
	
	@Configuration
	public class OpenApiConfig {
	  @Bean
	  public OpenAPI cheerUpOpenAPI() {
	    return new OpenAPI()
	      .info(new Info()
	        .title("CheerUp API")
	        .version("1.0.0")          // ← 버전 정보
	        .description("CheerUp 서비스 API 문서")
	      );
	  }
	}
}
