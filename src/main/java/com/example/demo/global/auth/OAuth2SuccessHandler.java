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
        
        // 프로덕션 환경에서는 Secure 플래그 추가 (HTTPS 필요)
        String serverName = request.getServerName();
        if (serverName.contains("cheer-up.net") || serverName.contains("api.cheer-up.net")) {
            cookie.setSecure(true); // HTTPS에서만 전송
        }
        
        response.addCookie(cookie);log.info("JWT cookie set, redirecting to OAuth callback");
        // OAuth 콜백 엔드포인트로 리다이렉트
        response.sendRedirect("/api/users/oauth/callback");
    }
}
