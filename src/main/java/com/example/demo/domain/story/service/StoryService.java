package com.example.demo.domain.story.service;

import com.example.demo.domain.category.repository.CategoryRepository;
import com.example.demo.domain.story.dto.*;
import com.example.demo.domain.story.entity.Story;
import com.example.demo.domain.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service // 서비스 계층 선언
@RequiredArgsConstructor // 생성자 주입
public class StoryService {

    private final StoryRepository storyRepository;
    private final CategoryRepository categoryRepository; // 카테고리 리포지토리 의존성 주입

    @Transactional // 사연 생성
    public StoryResponse create(StoryRequest req) {
        var category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        Story story = Story.builder()
                .content(req.getContent())
                .userNumber(req.getUserNumber())
                .createdAt(LocalDateTime.now())
                .category(category)
                .build();

        return toDto(storyRepository.save(story));
    }

    @Transactional(readOnly = true) // 전체 사연 조회
    public List<StoryResponse> findAll() {
        return storyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // ID로 사연 단건 조회
    public StoryResponse findById(Long id) {
        return storyRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("해당 사연이 없습니다."));
    }

    @Transactional // 사연 수정
    public StoryResponse update(Long id, StoryRequest req) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사연이 없습니다."));
        story.setContent(req.getContent());
        return toDto(story);
    }

    @Transactional // 사연 삭제
    public void delete(Long id) {
        storyRepository.deleteById(id);
    }

    @Transactional(readOnly = true) // 랜덤 사연 N개 조회
    public List<StoryResponse> random(int size) {
        return storyRepository.findRandomStories(size).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // 인기 사연 N개 조회 (응원 수 기준)
    public List<StoryResponse> popular(int size) {
        return storyRepository.findPopularStories(PageRequest.of(0, size)).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // 특정 사용자의 사연 목록 조회
    public List<StoryResponse> myStories(Long userNumber) {
        return storyRepository.findByUserNumber(userNumber).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 엔티티를 DTO로 변환
    private StoryResponse toDto(Story s) {
        return StoryResponse.builder()
                .storyId(s.getStoryId())
                .content(s.getContent())
                .createdAt(s.getCreatedAt())
                .userNumber(s.getUserNumber())
                .cheerCount(s.getCheerMessages() != null ? s.getCheerMessages().size() : 0)
                .categoryName(s.getCategory() != null ? s.getCategory().getCategoryName() : null)
                .build();
    }
}