package com.example.demo.domain.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter // Getter 자동 생성
@RequiredArgsConstructor // final 필드 대상 생성자 자동 생성...인데 지워도 될 것 같아요...(아마)
public class RegisterRequest {
    private String username; // 사용자 이름
    private String email; // 사용자 이메일
    private String password; // 사용자 비밀번호
    private String role; // 사용자 권한 (예: USER, ADMIN)
}
