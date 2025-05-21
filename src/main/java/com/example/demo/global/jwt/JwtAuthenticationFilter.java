package com.example.demo.global.jwt;

import com.example.demo.domain.user.entity.User;
import com.example.demo.global.auth.PrincipalDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;
// Login url(/login) POST 요청시에만 실행되는 클래스 UsernamePasswordAuthenticationFilter
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // 인증을 처리해주는 AuthenticationManager (Spring Security가 관리함)
    private final AuthenticationManager authenticationManager;
    // JWT 토큰을 생성해주는 클래스
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인 요청이 들어왔을 때 실행되는 메서드
     * 사용자의 username과 password를 추출하고 인증 시도
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 파싱 실패", e);
        }
    }

    //JWT 토큰 생성 & 응답에 담기
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        User user = principalDetails.getUser();

        String jwtToken = jwtTokenProvider.createToken(user);

        // JSON 응답설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        //JWT 토큰을 JSON 으로 반환
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        Map.of("token", JwtProperties.TOKEN_PREFIX + jwtToken)
                )
        );
        // HTTP응답 헤더에 토큰 추가
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }
}
