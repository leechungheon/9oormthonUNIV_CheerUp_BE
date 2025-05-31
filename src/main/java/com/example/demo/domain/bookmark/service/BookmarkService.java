package com.example.demo.domain.bookmark.service;

import com.example.demo.domain.bookmark.entity.Bookmark;
import com.example.demo.domain.bookmark.repository.BookmarkRepository;
import com.example.demo.domain.story.entity.Story;
import com.example.demo.domain.story.repository.StoryRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.global.auth.PrincipalDetails;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final StoryRepository storyRepository;

    // 북마크 추가
    @Transactional
    public void addBookmark(Long storyId, User user) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));

        if (bookmarkRepository.existsByUserAndStory(user, story)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        bookmarkRepository.save(Bookmark.builder()
                .user(user)
                .story(story)
                .build());
    }

    // 북마크 취소
    @Transactional
    public void removeBookmark(Long storyId, User user) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));

        Bookmark bookmark = bookmarkRepository.findByUserAndStory(user, story)
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN));

        bookmarkRepository.delete(bookmark);
    }

    // 내 북마크 목록 조회
    @Transactional(readOnly = true)
    public List<Story> getMyBookmarks(User user) {
        return bookmarkRepository.findAllByUser(user).stream()
                .map(Bookmark::getStory)
                .toList();
    }
}
