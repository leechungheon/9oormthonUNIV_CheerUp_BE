package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.LoginRequest;
import com.example.demo.domain.user.dto.RegisterRequest;
import com.example.demo.domain.user.dto.UserInfoResponse;
import com.example.demo.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import com.example.demo.global.auth.PrincipalDetails;
import com.example.demo.global.jwt.JwtTokenProvider;
import com.example.demo.global.jwt.JwtProperties;
import com.example.demo.global.response.ApiResponse;
import com.example.demo.domain.user.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
                        padding: 12px 24px; 
                        border: none; border-radius: 5px; cursor: pointer; font-size: 16px;
                        text-decoration: none; display: inline-block; margin: 5px;
                    }
                    .google-btn { background: #4285f4; color: white; }
                    .google-btn:hover { background: #3367d6; }
                    .naver-btn { background: #03c75a; color: white; }
                    .naver-btn:hover { background: #02b051; }
                    .kakao-btn { background: #fee500; color: #000; }
                    .kakao-btn:hover { background: #fdd835; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>로그인</h1>
                    <p>소셜 계정으로 로그인하세요.</p>
                    <div>
                        <a href="/oauth2/authorization/google" class="login-btn google-btn">Google로 로그인</a>
                    </div>
                    <div>
                        <a href="/oauth2/authorization/naver" class="login-btn naver-btn">네이버로 로그인</a>
                    </div>
                    <div>
                        <a href="/oauth2/authorization/kakao" class="login-btn kakao-btn">카카오로 로그인</a>
                    </div>
                </div>
            </body>
            </html>
            """;
        
        return ResponseEntity.ok()
            .header("Content-Type", "text/html; charset=UTF-8")
            .body(html);
    }
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal PrincipalDetails principal, HttpServletResponse response) throws IOException {
        // 이미 로그인된 상태면 teststatelogin으로 리다이렉트
        if (principal != null) {
            response.sendRedirect("/api/users/teststatelogin");
            return null;
        }
        return "홈페이지 - 로그인이 필요합니다. <a href='/oauth2/authorization/google'>Google로 로그인</a> | <a href='/oauth2/authorization/naver'>네이버로 로그인</a> | <a href='/oauth2/authorization/kakao'>카카오로 로그인</a>";
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

    @GetMapping("/oauth2/naver")
    public void redirectToNaver(@AuthenticationPrincipal PrincipalDetails principal, HttpServletResponse response) throws IOException {
        // 이미 로그인된 상태면 teststatelogin으로 리다이렉트
        if (principal != null) {
            response.sendRedirect("/api/users/teststatelogin");
            return;
        }
        // Naver OAuth2 인증 페이지로 리다이렉트
        response.sendRedirect("/oauth2/authorization/naver");
    }

    @GetMapping("/oauth2/kakao")
    public void redirectToKakao(@AuthenticationPrincipal PrincipalDetails principal, HttpServletResponse response) throws IOException {
        // 이미 로그인된 상태면 teststatelogin으로 리다이렉트
        if (principal != null) {
            response.sendRedirect("/api/users/teststatelogin");
            return;
        }
        // Kakao OAuth2 인증 페이지로 리다이렉트
        response.sendRedirect("/oauth2/authorization/kakao");
    }

    @Operation(summary = "회원가입 요청 처리")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody RegisterRequest request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @Operation(summary = "로그인 요청 처리")
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(token);
    }    
    /*
     * 로그인 상태 확인용 엔드포인트 - HTML 페이지 형태로 응답
     */
    @GetMapping("/teststatelogin")
    public ResponseEntity<String> testStateLogin(@AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getUser().getId();
        String email = principal.getUser().getEmail();
        String username = principal.getUser().getUsername();
        String provider = principal.getUser().getProvider() != null ? principal.getUser().getProvider() : "일반";
        
        // 로그 추가
        log.info("사용자 정보 - ID: {}, Email: {}, Provider: {}", userId, email, principal.getUser().getProvider());
        
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
                        margin-right: 10px;
                    }
                    .logout-btn:hover { background: #c82333; }
                    .provider-badge {
                        display: inline-block;
                        padding: 4px 8px;
                        border-radius: 4px;
                        font-size: 12px;
                        font-weight: bold;
                        color: white;
                    }
                    .google { background: #4285f4; }
                    .naver { background: #03c75a; }
                    .kakao { background: #fee500; color: #000; }
                    .normal { background: #6c757d; }
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
                        <p><strong>로그인 제공자:</strong> <span class="provider-badge %s">%s</span></p>
                    </div>
                    <button class="logout-btn" onclick="logout()">로그아웃</button>
                </div>
                <script>
                    function logout() {
                        fetch('/api/users/logout', {
                            method: 'POST',
                            credentials: 'include'
                        }).then(response => {
                            if (response.redirected) {
                                window.location.href = response.url;
                            } else {
                                window.location.href = '/api/users/login';
                            }
                        }).catch(() => {
                            window.location.href = '/api/users/login';
                        });
                    }
                </script>
            </body>
            </html>
            """.formatted(userId, email, username, provider.toLowerCase(), provider.toUpperCase());
        
        return ResponseEntity.ok()
            .header("Content-Type", "text/html; charset=UTF-8")
            .body(html);
    }      
    /*
        로그아웃 처리 - OAuth 제공자별로 세션 종료
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal PrincipalDetails principal, HttpServletResponse response) throws IOException {
        // JWT 토큰 쿠키 삭제
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        
        // OAuth 제공자별 로그아웃 처리
        if (principal != null && principal.getUser().getProvider() != null) {
            String provider = principal.getUser().getProvider().toLowerCase();
            String logoutUrl = getProviderLogoutUrl(provider);
            
            if (logoutUrl != null) {
                // OAuth 제공자의 로그아웃 페이지로 리다이렉트하여 세션 종료
                response.sendRedirect(logoutUrl);
                return null; // 리다이렉트 시에는 null 반환
            }
        }
        
        // 일반 로그아웃 (OAuth가 아닌 경우) - 로그인 페이지로 리다이렉트
        response.sendRedirect("/api/users/login");
        return null;
    }
      /**
     * OAuth 제공자별 로그아웃 URL 반환
     */
    private String getProviderLogoutUrl(String provider) {
        String homeUrl = "https://api.cheer-up.net/api/users/login";
        
        return switch (provider) {
            case "google" -> "https://accounts.google.com/logout?continue=" + homeUrl;
            case "naver" -> "https://nid.naver.com/nidlogin.logout?returl=" + homeUrl;
            case "kakao" -> "https://kauth.kakao.com/oauth/logout?client_id=${KAKAO_CLIENT_ID}&logout_redirect_uri=" + homeUrl;
            default -> null;
        };
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

    /**
     * 기존 사용자의 provider 정보 업데이트 테스트용 엔드포인트
     */
    @GetMapping("/update-test-provider")
    public ResponseEntity<String> updateTestProvider(@RequestParam(defaultValue = "google") String provider) {
        try {
            // 테스트용 사용자 조회 및 provider 업데이트
            userService.updateUserProvider("test@test.com", provider);
            return ResponseEntity.ok("Test user provider updated to: " + provider);
        } catch (Exception e) {
            log.error("Error updating test user provider", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * 데이터베이스의 사용자 정보 확인용 엔드포인트
     */
    @GetMapping("/debug-user/{email}")
    public ResponseEntity<String> debugUser(@PathVariable String email) {
        try {
            // 이메일로 사용자 조회
            User user = userService.findByEmail(email);
            String info = String.format(
                "사용자 정보 - ID: %d, Email: %s, Username: %s, Provider: %s", 
                user.getId(), user.getEmail(), user.getUsername(), user.getProvider()
            );
            log.info(info);
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            log.error("Error debugging user", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    /**
     * AWS ELB 헬스체크 엔드포인트
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    /**
     * 현재 로그인된 사용자 정보 조회 API
     * 프론트엔드에서 OAuth 로그인 후 사용자 정보(닉네임, 이메일)를 가져올 때 사용
     */
    @Operation(summary = "현재 로그인된 사용자 정보 조회", 
               description = "JWT 토큰을 통해 현재 로그인된 사용자의 정보(ID, 이메일, 닉네임, OAuth 제공자)를 반환합니다.")    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser(@AuthenticationPrincipal PrincipalDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("인증이 필요합니다."));
        }
        
        User user = principal.getUser();
        UserInfoResponse userInfo = UserInfoResponse.from(user);
        
        log.info("사용자 정보 조회 - ID: {}, Email: {}, Username: {}, Provider: {}", 
                user.getId(), user.getEmail(), user.getUsername(), user.getProvider());
        
        return ResponseEntity.ok(ApiResponse.success(userInfo, "사용자 정보 조회 성공"));
    }
    
    /**
     * 인증 상태 확인 API - 단순히 인증 여부만 확인
     * 프론트엔드에서 로그인 상태를 빠르게 체크할 때 사용
     */
    @Operation(summary = "인증 상태 확인", 
               description = "현재 사용자의 인증 상태를 확인합니다.")
    @GetMapping("/auth/status")
    public ResponseEntity<ApiResponse<Boolean>> checkAuthStatus(@AuthenticationPrincipal PrincipalDetails principal) {
        boolean isAuthenticated = (principal != null);
        String message = isAuthenticated ? "인증된 사용자입니다." : "인증되지 않은 사용자입니다.";
        
        return ResponseEntity.ok(ApiResponse.success(isAuthenticated, message));
    }

    /**
     * OAuth 로그인 성공 후 프론트엔드 콜백 처리
     * 쿠키에서 JWT 토큰을 추출하여 사용자 정보와 함께 반환
     */
    @Operation(summary = "OAuth 콜백 처리", 
               description = "OAuth 로그인 성공 후 쿠키에서 토큰을 추출하여 사용자 정보를 반환합니다.")
    @GetMapping("/oauth/callback")
    public ResponseEntity<String> oauthCallback(@AuthenticationPrincipal PrincipalDetails principal, 
                                               HttpServletRequest request) {
        if (principal == null) {
            // 인증되지 않은 경우 프론트엔드 로그인 페이지로 리다이렉트하는 HTML 반환
            String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>로그인 필요</title>
                </head>
                <body>                    <script>
                        alert('로그인이 필요합니다.');
                        window.location.href = 'https://cheer-up.net/login';
                    </script>
                </body>
                </html>
                """;
            return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
        }
        
        User user = principal.getUser();
        
        // 프론트엔드로 사용자 정보를 전달하는 HTML 페이지
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>로그인 성공</title>
            </head>
            <body>
                <script>
                    // 사용자 정보를 부모 창에 전달
                    const userInfo = {
                        id: %d,
                        email: '%s',
                        username: '%s',
                        provider: '%s'
                    };
                    
                    // 부모 창이 있는 경우 (팝업으로 열린 경우)
                    if (window.opener) {                        window.opener.postMessage({
                            type: 'OAUTH_SUCCESS',
                            data: userInfo
                        }, 'https://cheer-up.net');
                        window.close();
                    } else {                        // 같은 탭에서 열린 경우 localStorage에 저장 후 리다이렉트
                        localStorage.setItem('user', JSON.stringify(userInfo));
                        window.location.href = 'https://cheer-up.net/dashboard';
                    }
                </script>
            </body>
            </html>
            """.formatted(
                user.getId(), 
                user.getEmail(), 
                user.getUsername(), 
                user.getProvider() != null ? user.getProvider() : "general"
            );
            
        return ResponseEntity.ok()
            .header("Content-Type", "text/html; charset=UTF-8")
            .body(html);
    }

    /**
     * 프론트엔드용 로그아웃 API
     * JWT 토큰 쿠키를 삭제하고 성공 응답을 반환
     */
    @Operation(summary = "로그아웃", 
               description = "JWT 토큰 쿠키를 삭제하고 로그아웃을 처리합니다.")    
    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<String>> logoutApi(HttpServletRequest request, HttpServletResponse response) {
        // JWT 토큰 쿠키 삭제
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        
        // 프로덕션 환경에서는 Secure 플래그 추가
        String serverName = request.getServerName();
        if (serverName.contains("cheer-up.net") || serverName.contains("api.cheer-up.net")) {
            cookie.setSecure(true);
        }
        
        response.addCookie(cookie);
        
        log.info("사용자 로그아웃 처리 완료");
        
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다.", "로그아웃 성공"));
    }
}