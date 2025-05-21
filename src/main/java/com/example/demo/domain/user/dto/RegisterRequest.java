package com.example.demo.domain.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role;
}
