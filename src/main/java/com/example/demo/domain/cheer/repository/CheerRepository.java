package com.example.demo.domain.cheer.repository;

import com.example.demo.domain.cheer.entity.CheerMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CheerRepository extends JpaRepository<CheerMessage, Long> {

    @Query("SELECT cm FROM CheerMessage cm JOIN FETCH cm.user u JOIN FETCH cm.category c WHERE cm.story.storyId = :storyId")
    List<CheerMessage> findByStory_StoryId(@Param("storyId") Long storyId);


    @Query("SELECT cm FROM CheerMessage cm JOIN FETCH cm.user u JOIN FETCH cm.category c WHERE cm.category.categoryId = :catId")
    List<CheerMessage> findAllByCategoryWithJoins(@Param("catId") Long categoryId);


}
