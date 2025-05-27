package com.example.demo.global.config;

import com.example.demo.global.jwt.JwtAuthenticationFilter;
import com.example.demo.global.jwt.JwtAuthorizationFilter;
import com.example.demo.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .addFilter(new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider)) // 로그인 처리 필터
                .addFilterAfter(new JwtAuthorizationFilter(jwtTokenProvider), JwtAuthenticationFilter.class) // 권한 처리 필터
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/**"
                        ).permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**"  // ← 네 설정에서 사용 중인 커스텀 경로
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}