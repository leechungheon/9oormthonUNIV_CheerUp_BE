package com.example.demo.domain.post.entity;

import com.example.demo.domain.user.entity.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "story")
@NoArgsConstructor
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storyId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();  // 기본값 설정

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // 응원 메시지 목록
    @OneToMany(mappedBy = "story")
    private List<Message> messageList;

    @Builder
    public Story(Long storyId, String content, LocalDateTime createdAt, User user) {
        this.storyId = storyId;
        this.content = content;
        this.createdAt = createdAt;
        this.user = user;
    }

}

