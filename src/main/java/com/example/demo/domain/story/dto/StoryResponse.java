package com.example.demo.domain.story.dto;

import com.example.demo.domain.category.entity.Category;
import com.example.demo.domain.cheer.dto.CheerResponse;
import com.example.demo.domain.cheer.entity.CheerMessage;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter // Getter, Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder // 빌더 패턴 적용
public class StoryResponse {
    private Long storyId; // 응원함 PK
    private String content; // 사연 내용
    private LocalDateTime createdAt; // 생성 시각
    private List<CheerResponse> cheerMessages; // 연결된 응원 메시지 목록
    private String categoryName; // 연결된 카테고리
    private String username; // 사용자 번호
    private int cheerCount; // 응원 메시지 개수
}