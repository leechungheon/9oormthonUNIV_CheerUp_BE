package com.example.demo.domain.bookmark.repository;

import com.example.demo.domain.bookmark.entity.Bookmark;
import com.example.demo.domain.story.entity.Story;
import com.example.demo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserAndStory(User user, Story story);

    List<Bookmark> findAllByUser(User user);

    boolean existsByUserAndStory(User user, Story story);
}