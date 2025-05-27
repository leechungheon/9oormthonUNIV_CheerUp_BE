package com.example.demo.domain.cheer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity // JPA 엔티티 매핑
@Table(name = "user_cheer_limit") // 테이블 이름 지정
@Getter @Setter // Getter, Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder // 빌더 패턴 적용
public class UserCheerLimit {

    @Id // 기본 키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 전략
    private Long id; // 기본 키

    @Column(nullable = false)
    private Long userNumber; // 사용자 번호

    @Column(nullable = false)
    private LocalDate date; // 응원한 날짜

    @Column(nullable = false)
    private int count; // 해당 날짜의 응원 횟수
}