package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponse {
    private Long id;
    private String email;
    private String username;
    private String provider;
    
    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .provider(user.getProvider())
                .build();
    }
}
