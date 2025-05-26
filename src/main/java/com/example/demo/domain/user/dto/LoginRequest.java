package com.example.demo.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // Getter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
public class LoginRequest {

    private String email; // 사용자 이메일
    private String password; // 사용자 비밀번호
}