package com.example.demo.domain.user.entity;

import com.example.demo.domain.story.entity.Story;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {

    @Id // 기본 키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 전략
    private long id; // 사용자 ID

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (고유값)

    @Column(nullable = false, unique = true)
    private String username; // 사용자 닉네임 또는 아이디 (고유값)

    private String password; // 암호화된 비밀번호

    private String provider; // OAuth 제공자 (google, naver, kakao 등)

    @Column(nullable = false)
    private String role; // 사용자 권한 (예: USER, ADMIN)

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    //mappedBy = "user"는 Story 엔티티의 user 필드가 외래키의 주인임을 의미
    private List<Story> stories = new ArrayList<>();
    // 생성자 오버로딩
    public User(String email, String username, String password, String role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // OAuth 제공자를 포함한 생성자
    public User(String email, String username, String password, String role, String provider) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.provider = provider;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", provider='" + provider + '\'' +
                ", role='" + role + '\'' +
                '}';
        // stories는 포함하지 않음
    }
}