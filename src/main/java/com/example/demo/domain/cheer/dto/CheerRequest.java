package com.example.demo.domain.cheer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter // Getter, Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
public class CheerRequest {

    @NotNull
    private Long storyId; // 연결할 사연의 ID

    @NotBlank(message = "내용을 입력해주세요.")
    private String content; // 응원 메시지 내용
}