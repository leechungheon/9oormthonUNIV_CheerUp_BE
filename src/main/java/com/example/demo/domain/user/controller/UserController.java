package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.LoginRequest;
import com.example.demo.domain.user.dto.RegisterRequest;
import com.example.demo.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // REST API 컨트롤러
@RequestMapping("/api/users") // 사용자 API 엔드포인트
@Tag(name = "User", description = "회원 관련 API") // Swagger 설명
@RequiredArgsConstructor // 생성자 주입
public class UserController {

    private final UserService userService; // 사용자 서비스 의존성 주입

    @GetMapping("/home") // 홈 테스트 엔드포인트
    public String home() {
        return "home";
    }

    @PostMapping("/signup") // 회원가입 요청 처리
    public ResponseEntity<String> signup(@Valid @RequestBody RegisterRequest request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login") // 로그인 요청 처리
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(token); // 로그인 성공 시 JWT 토큰 반환
    }
}