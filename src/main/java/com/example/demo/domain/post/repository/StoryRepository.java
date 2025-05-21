package com.example.demo.domain.post.repository;

import com.example.demo.domain.post.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {
    // 스토리 작성
    // 스토리 조회
    // 스토리 수정
    // 스토리 삭제
}
