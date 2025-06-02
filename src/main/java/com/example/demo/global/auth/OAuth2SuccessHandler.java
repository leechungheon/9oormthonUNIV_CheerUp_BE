package com.example.demo.global.auth;

import com.example.demo.domain.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.example.demo.global.jwt.JwtTokenProvider;
import com.example.demo.global.jwt.JwtProperties;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        log.info("OAuth2 authentication successful");
        
        // CustomOAuth2User에서 이미 처리된 User 객체를 직접 가져옴
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) ((OAuth2AuthenticationToken) authentication).getPrincipal();
        User user = customOAuth2User.getUser();
        
        log.info("Creating JWT token for user: {} (ID: {})", user.getEmail(), user.getId());
        String token = jwtTokenProvider.createToken(user);
        log.info("JWT token created: {}", token.substring(0, Math.min(token.length(), 50)) + "...");
        
        // Set JWT as HttpOnly cookie (store raw token without prefix to avoid invalid space)
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(JwtProperties.EXPIRATION_TIME / 1000);
        response.addCookie(cookie);
        
        // 프론트엔드로 리다이렉트 (토큰은 쿠키에 포함됨)
        String frontendUrl = "https://cheerup-omega.vercel.app/home"; // 프론트엔드 성공 페이지 URL
        log.info("Redirecting to frontend: {}", frontendUrl);
        response.sendRedirect(frontendUrl);
    }
}
