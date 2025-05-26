package com.example.demo.domain.story.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter // Getter, Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
public class StoryRequest {

    @NotBlank // null 또는 빈 문자열 허용하지 않음
    private String content; // 사연 내용

    @NotNull
    private Long userNumber; // 사용자 번호

    @NotNull
    private Long categoryId; // 카테고리 ID
}
