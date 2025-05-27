package com.example.demo.domain.cheer.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter // Getter, Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder // 빌더 패턴 적용
public class CheerResponse {

    private Long cheerMessageId; // 응원 메시지 ID
    private Long storyId; // 대상 사연 ID
    private String content; // 응원 메시지 내용
    private LocalDateTime createdAt; // 생성 시각
    private Long userNumber; // 사용자 번호
    private String category; // 카테고리 (무조건 응원함 등)
}