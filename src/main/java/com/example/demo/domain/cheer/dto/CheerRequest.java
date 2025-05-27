package com.example.demo.domain.cheer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter // Getter, Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
public class CheerRequest {

    @NotNull // null 허용하지 않음
    private Long storyId; // 대상 사연 ID

    @NotBlank // null, 빈 문자열 허용하지 않음
    private String content; // 응원 메시지 내용

    @NotNull
    private Long userNumber; // 사용자 번호

    private String category; // 무조건 응원함용 카테고리 (선택값)
}