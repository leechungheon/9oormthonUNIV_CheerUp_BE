package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.LoginRequest;
import com.example.demo.domain.user.dto.RegisterRequest;
import com.example.demo.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "회원 관련 API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * OAuth2 리다이렉트 테스트용 엔드포인트
     */
    @GetMapping("/test")
    public String test(@RequestParam("userId") Long userId) {
        return "Test page - User ID: " + userId;
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/oauth2/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
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
    }


}