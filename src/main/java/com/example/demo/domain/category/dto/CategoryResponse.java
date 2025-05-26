package com.example.demo.domain.category.dto;

import lombok.*;

@Getter // Getter 자동 생성
@Setter // Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder // 빌더 패턴 적용
public class CategoryResponse {
    private Long categoryId;   // 카테고리 ID
    private String categoryName; // 카테고리 이름
}