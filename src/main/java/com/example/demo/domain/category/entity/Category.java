package com.example.demo.domain.category.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity // JPA 엔티티 매핑
@Table(name = "category") // 테이블 이름 지정
@Getter // Getter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder // 빌더 패턴 적용
public class Category {

    @Id // 기본 키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 전략 사용
    private Long categoryId; // 카테고리 ID

    @Column(nullable = false, unique = true, length = 50) // not null, 유니크 제약조건, 최대 길이 50
    private String categoryName; // 카테고리 이름
}