package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

}
