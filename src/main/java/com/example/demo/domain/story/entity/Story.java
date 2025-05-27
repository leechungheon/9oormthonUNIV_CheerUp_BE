package com.example.demo.domain.story.entity;

import com.example.demo.domain.cheer.entity.CheerMessage;
import com.example.demo.domain.category.entity.Category;

import com.example.demo.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // JPA 엔티티 매핑
@Table(name = "story") // 테이블 이름 지정
@Getter @Setter // Getter, Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder // 빌더 패턴 적용
public class Story {

    @Id // 기본 키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 전략
    private Long storyId; // 응원함 PK

    @Column(nullable = false, columnDefinition = "TEXT") // not null, 긴 텍스트 허용
    private String content; // 사연 내용

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성 시각

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true) // 응원 메시지와 1:N 관계
    private List<CheerMessage> cheerMessages = new ArrayList<>(); // 연결된 응원 메시지 목록

    @ManyToOne(fetch = FetchType.LAZY) // 카테고리와 다대일 관계
    @JoinColumn(name = "category_id") // 외래 키 설정
    private Category category; // 연결된 카테고리
}