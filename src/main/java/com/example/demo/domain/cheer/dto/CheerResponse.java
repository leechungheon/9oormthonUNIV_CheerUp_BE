package com.example.demo.domain.cheer.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter // Getter, Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder // 빌더 패턴 적용
public class CheerResponse {
    private Long cheerId; // 응원 메시지 ID
    private String username; // 응원한 사용자 nickname
    private String content; // 응원 메시지 내용
    private LocalDateTime createdAt; // 생성 시각
    private String categoryName; // 카테고리 (무조건 응원함 등)
}