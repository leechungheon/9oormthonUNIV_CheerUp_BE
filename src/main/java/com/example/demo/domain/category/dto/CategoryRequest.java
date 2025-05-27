package com.example.demo.domain.category.dto;

import lombok.*;

@Getter // Getter 자동 생성
@Setter // Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
public class CategoryRequest {
    private String categoryName; // 생성 또는 수정할 카테고리 이름
}