package com.example.demo.domain.story.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter // Getter, Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder // 빌더 패턴 적용
public class StoryResponse {

    private Long storyId; // 사연 ID
    private String content; // 사연 내용
    private LocalDateTime createdAt; // 생성 시각
    private Long userNumber; // 사용자 번호
    private int cheerCount; // 응원 메시지 수 (인기순 정렬용)
    private String categoryName; // 카테고리 이름
}