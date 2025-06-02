package com.example.demo.domain.cheer.repository;

import com.example.demo.domain.cheer.entity.CheerMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CheerRepository extends JpaRepository<CheerMessage, Long> {

    @Query("SELECT c FROM CheerMessage c JOIN FETCH c.user JOIN FETCH c.category WHERE c.story.storyId = :storyId")
    List<CheerMessage> findByStory_StoryId(@Param("storyId") Long storyId);

    @Query(value = "SELECT * FROM cheer_message WHERE category_id = :catId ORDER BY RAND() LIMIT 1", nativeQuery = true)
    CheerMessage findRandomByCategory(@Param("catId") Long categoryId);

}
