package com.example.demo.global.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.domain.user.entity.User;
import com.example.demo.global.auth.PrincipalDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 인증(Authorization)을 위한 필터
 * - 모든 요청마다 실행되며, JWT가 포함된 경우 이를 검증하고 인증 정보를 등록한다.
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 Authorization 값을 가져온다.
        String header = request.getHeader(JwtProperties.HEADER_STRING);

        // 2. 헤더가 없거나 "Bearer "로 시작하지 않으면 필터를 타지 않고 다음 필터로 넘긴다.
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 접두사를 제거해 순수 토큰만 추출
        String token = header.replace(JwtProperties.TOKEN_PREFIX, "");

        try {
            // 4. 토큰 유효성 검사 및 디코딩 (검증 실패 시 예외 발생)
            if (jwtTokenProvider.validateToken(token)) {

                // 5. JWT 내부 클레임에서 사용자 정보 추출
                Long id = jwtTokenProvider.getUserId(token);
                String username = jwtTokenProvider.getUsername(token);

                // 6. 사용자 정보가 존재할 경우
                if (username != null) {
                    // DB에서 조회하지 않고 토큰 기반으로 가짜 User 객체를 생성
                    User user = new User();
                    user.setId(id);
                    user.setUsername(username);

                    // 스프링 시큐리티에서 사용할 PrincipalDetails 생성
                    PrincipalDetails principalDetails = new PrincipalDetails(user);

                    // 7. 인증 객체 생성 (비밀번호 X, 권한 리스트 비움)
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    principalDetails, null, Collections.emptyList());

                    // 8. 현재 요청의 SecurityContext에 인증 정보 등록
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JWTVerificationException e) {
            System.out.println("유효하지 않은 토큰입니다: " + e.getMessage());
        }

        // 9. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}