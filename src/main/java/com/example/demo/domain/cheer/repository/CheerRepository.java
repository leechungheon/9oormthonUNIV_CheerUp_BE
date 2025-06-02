package com.example.demo.domain.cheer.repository;

import com.example.demo.domain.cheer.entity.CheerMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CheerRepository extends JpaRepository<CheerMessage, Long> {

    List<CheerMessage> findByStory_StoryId(Long storyId); // 특정 사연 ID에 해당하는 응원 메시지 목록 조회

    @Query(value = "SELECT * FROM cheer_message WHERE category_id = :catId ORDER BY RAND() LIMIT 1", nativeQuery = true)
    CheerMessage findRandomByCategory(@Param("catId") Long categoryId);

}
