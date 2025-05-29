package com.example.demo.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    public String createToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("id", user.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }
    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                    .build()
                    .verify(token); // 검증
            return true;
        } catch (Exception e) {
            return false; // 유효하지 않은 토큰
        }
    }

    public String getUsername(String token) {
        return JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build()
                .verify(token)
                .getClaim("username")
                .asString();
    }
    public Long getUserId(String token) {
        return JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build()
                .verify(token)
                .getClaim("id")
                .asLong();
    }

}