package com.example.demo.global.jwt;

public class JwtProperties {
    public static final String SECRET = "myJwtSecretKey123456"; // JWT 서명용 비밀 키 (노출 주의)
    public static final int EXPIRATION_TIME = 864000000; // 10일 (밀리초 단위)
    public static final String TOKEN_PREFIX = "Bearer "; // 토큰 접두사(Bearer는 JWT의 HTTP Authorization 헤더 표준)
    public static final String HEADER_STRING = "Authorization"; //HTTP 표준 헤더
}