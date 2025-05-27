package com.example.demo.domain.cheer.entity;

import com.example.demo.domain.story.entity.Story;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity // JPA 엔티티 매핑
@Table(name = "cheer_message") // 테이블 이름 지정
@Getter @Setter // Getter, Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder // 빌더 패턴 적용
public class CheerMessage {

    @Id // 기본 키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 전략
    private Long cheerMessageId; // 응원 메시지 ID

    @Column(nullable = false, columnDefinition = "TEXT") // not null, 긴 텍스트 허용
    private String content; // 응원 메시지 내용

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성 시각

    @Column(nullable = false)
    private Long userNumber; // 사용자 번호

    private String category; // 카테고리 (무조건 응원함 등)

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계, 지연 로딩
    @JoinColumn(name = "story_id", nullable = false) // 외래 키 설정
    private Story story; // 연결된 사연
}