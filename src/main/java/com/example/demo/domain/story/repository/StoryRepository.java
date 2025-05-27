package com.example.demo.domain.story.repository;

import com.example.demo.domain.story.entity.Story;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {

    @Query(value = "SELECT * FROM story ORDER BY RAND() LIMIT :size", nativeQuery = true)
    List<Story> findRandomStories(@Param("size") int size); // 랜덤 사연 N개 조회

    @Query("""
        SELECT s FROM Story s
        LEFT JOIN s.cheerMessages m
        GROUP BY s
        ORDER BY COUNT(m) DESC
        """)
    List<Story> findPopularStories(Pageable pageable); // 응원 수 기준 인기 사연 조회

    List<Story> findByUserNumber(Long userNumber); // 특정 사용자의 사연 목록 조회
}