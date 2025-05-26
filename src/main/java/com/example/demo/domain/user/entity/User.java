package com.example.demo.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity // JPA 엔티티 매핑
@Data // Getter, Setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder // 빌더 패턴 적용
public class User {

    @Id // 기본 키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 전략
    private long id; // 사용자 ID

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (고유값)

    @Column(nullable = false, unique = true)
    private String username; // 사용자 닉네임 또는 아이디 (고유값)

    private String password; // 암호화된 비밀번호

    @Column(nullable = false)
    private String role; // 사용자 권한 (예: USER, ADMIN)

    private String profileImageUrl; // 프로필 이미지 URL (nullable)

    // 생성자 오버로딩
    public User(String email, String username, String password, String role, String profileImageUrl) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
    }
}