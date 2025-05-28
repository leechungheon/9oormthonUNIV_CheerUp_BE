package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.LoginRequest;
import com.example.demo.domain.user.dto.RegisterRequest;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.auth.PrincipalDetails;
import com.example.demo.global.jwt.JwtTokenProvider;
import com.example.demo.global.jwt.JwtProperties;
import com.example.demo.domain.user.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "회원 관련 API")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;    /**
     * OAuth2 리다이렉트 테스트용 엔드포인트
     */
    @GetMapping("/test")
    public String test(@RequestParam("userId") Long userId) {
        return "Test page - User ID: " + userId;
    }

    /**
     * 로그인 페이지 - 이미 로그인된 경우 teststatelogin으로 리다이렉트
     */
    @GetMapping("/login")
    public ResponseEntity<String> loginPage(@AuthenticationPrincipal PrincipalDetails principal, HttpServletResponse response) throws IOException {
        // 이미 로그인된 상태면 teststatelogin으로 리다이렉트
        if (principal != null) {
            response.sendRedirect("/api/users/teststatelogin");
            return null;
        }
        
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>로그인</title>
                <style>
                    body { font-family: Arial, sans-serif; padding: 20px; }
                    .container { max-width: 400px; margin: 0 auto; text-align: center; }
                    .login-btn { 
                        background: #4285f4; color: white; padding: 12px 24px; 
                        border: none; border-radius: 5px; cursor: pointer; font-size: 16px;
                        text-decoration: none; display: inline-block;
                    }
                    .login-btn:hover { background: #3367d6; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>로그인</h1>
                    <p>Google 계정으로 로그인하세요.</p>
                    <a href="/oauth2/authorization/google" class="login-btn">Google로 로그인</a>
                </div>
            </body>
            </html>
            """;
        
        return ResponseEntity.ok()
            .header("Content-Type", "text/html; charset=UTF-8")
            .body(html);
    }@GetMapping("/home")
    public String home(@AuthenticationPrincipal PrincipalDetails principal, HttpServletResponse response) throws IOException {
        // 이미 로그인된 상태면 teststatelogin으로 리다이렉트
        if (principal != null) {
            response.sendRedirect("/api/users/teststatelogin");
            return null;
        }
        return "홈페이지 - 로그인이 필요합니다. <a href='/oauth2/authorization/google'>Google로 로그인</a>";
    }

    @GetMapping("/oauth2/google")
    public void redirectToGoogle(@AuthenticationPrincipal PrincipalDetails principal, HttpServletResponse response) throws IOException {
        // 이미 로그인된 상태면 teststatelogin으로 리다이렉트
        if (principal != null) {
            response.sendRedirect("/api/users/teststatelogin");
            return;
        }
        // Google OAuth2 인증 페이지로 리다이렉트
        response.sendRedirect("/oauth2/authorization/google");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody RegisterRequest request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(token);
    }    /**
     * 로그인 상태 확인용 엔드포인트 - HTML 페이지 형태로 응답
     */
    @GetMapping("/teststatelogin")
    public ResponseEntity<String> testStateLogin(@AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getUser().getId();
        String email = principal.getUser().getEmail();
        String username = principal.getUser().getUsername();
        
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>로그인 상태 확인</title>
                <style>
                    body { font-family: Arial, sans-serif; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; }
                    .user-info { background: #f5f5f5; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
                    .logout-btn { 
                        background: #dc3545; color: white; padding: 10px 20px; 
                        border: none; border-radius: 5px; cursor: pointer; font-size: 16px;
                    }
                    .logout-btn:hover { background: #c82333; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>로그인 상태 확인</h1>
                    <div class="user-info">
                        <h3>로그인된 사용자 정보:</h3>
                        <p><strong>사용자 ID:</strong> %d</p>
                        <p><strong>이메일:</strong> %s</p>
                        <p><strong>사용자명:</strong> %s</p>
                    </div>
                    <button class="logout-btn" onclick="logout()">로그아웃</button>
                </div>
                <script>
                    function logout() {
                        fetch('/api/users/logout', {
                            method: 'POST',
                            credentials: 'include'
                        }).then(response => {
                            if (response.ok) {
                                window.location.href = '/api/users/home';
                            }
                        });
                    }
                </script>
            </body>
            </html>
            """.formatted(userId, email, username);
        
        return ResponseEntity.ok()
            .header("Content-Type", "text/html; charset=UTF-8")
            .body(html);
    }

    /**
     * 로그아웃 처리
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // JWT 토큰 쿠키 삭제
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        
        return ResponseEntity.ok("로그아웃 완료");
    }

    /**
     * JWT 쿠키 테스트용 엔드포인트 - 테스트용 사용자의 JWT 토큰을 쿠키로 설정
     */
    @GetMapping("/set-test-cookie")
    public ResponseEntity<String> setTestCookie(HttpServletResponse response) {
        try {
            // 테스트용 사용자 생성 (실제로는 DB에서 가져와야 함)
            User testUser = new User();
            testUser.setId(1L);
            testUser.setEmail("test@test.com");
            testUser.setUsername("testuser");
            
            String token = jwtTokenProvider.createToken(testUser);
            log.info("Created test JWT token: {}", token.substring(0, Math.min(token.length(), 50)) + "...");
            
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(JwtProperties.EXPIRATION_TIME / 1000);
            response.addCookie(cookie);
            
            return ResponseEntity.ok("Test JWT cookie set. Try accessing /api/users/teststatelogin");
        } catch (Exception e) {
            log.error("Error creating test cookie", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

}