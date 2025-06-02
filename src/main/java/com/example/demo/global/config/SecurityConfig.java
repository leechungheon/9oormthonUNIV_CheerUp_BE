package com.example.demo.global.config;

import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.auth.CustomOAuth2UserService;
import com.example.demo.global.auth.OAuth2SuccessHandler;
import com.example.demo.global.jwt.JwtAuthenticationFilter;
import com.example.demo.global.jwt.JwtAuthorizationFilter;
import com.example.demo.global.jwt.JwtTokenProvider;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    // ✅ CORS 필터 등록
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173","https://localhost","https://api.cheer-up.net")); // 프론트 주소
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authMgr) throws Exception {
        http
                .cors(Customizer.withDefaults()) // ✅ 이거 추가!
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())            .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                )            // 로그인 요청 처리 필터
                .addFilter(new JwtAuthenticationFilter(authMgr, jwtTokenProvider))
                // JWT 검증 및 권한 설정 필터
                .addFilterAfter(new JwtAuthorizationFilter(jwtTokenProvider, userRepository), JwtAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth                // OAuth2 요청·콜백, 테스트 페이지 및 Swagger/OpenAPI 허용
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/api/users/test",
                                "/api/users/health",
                                "/login",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()                // API 엔드포인트 중 회원가입·로그인 또는 홈페이지 공개
                        .requestMatchers(
                                "/api/users/signup",
                                "/api/users/login",
                                "/api/users/home",
                                "/api/users/oauth2/google",
                                "/api/users/oauth2/naver",
                                "/api/users/oauth2/kakao",
                                "/api/users/logout",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/swagger-ui/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );        return http.build();
    }
}