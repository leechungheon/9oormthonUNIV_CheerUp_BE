package com.example.demo.domain.story.service;

import com.example.demo.domain.category.repository.CategoryRepository;
import com.example.demo.domain.story.dto.*;
import com.example.demo.domain.story.entity.Story;
import com.example.demo.domain.story.repository.StoryRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.global.auth.PrincipalDetails;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service // 서비스 계층 선언
@RequiredArgsConstructor // 생성자 주입
public class StoryService {

    private final StoryRepository storyRepository;
    private final CategoryRepository categoryRepository; // 카테고리 리포지토리 의존성 주입

    @Transactional // 사연 생성
    public StoryResponse create(PrincipalDetails principal, StoryRequest req) {
        try {
            User user = principal.getUser(); // 로그인한 유저 객체 가져오기
            if (user == null) {
                throw new CustomException(ErrorCode.UNAUTHORIZED); // 로그인 안되어 있다면
            }
            if (req.getContent() == null || req.getContent().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_CONTENT); // 내용이 비어있다면
            }
            var category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CATEGORY)); // 카테고리 유효성 검사

            Story story = Story.builder()
                    .content(req.getContent())
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .category(category)
                    .build();

            storyRepository.save(story);
            return toDto(story);

        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.DATA_ACCESS_ERROR); // Handle database access errors
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // Handle unexpected errors
        }
    }

    @Transactional(readOnly = true) // 전체 사연 조회
    public List<StoryResponse> findAll() {
        return storyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // ID로 사연 단건 조회
    public StoryResponse getStoryById(Long storyId, PrincipalDetails principal) {
        try {
            User currentUser = principal.getUser(); // 현재 로그인한 사용자
            if (currentUser == null) {
                throw new CustomException(ErrorCode.UNAUTHORIZED); // 로그인 안 된 상태
            }

            Story story = storyRepository.findById(storyId)
                    .orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND)); // 스토리가 없는 경우

            if (!Objects.equals(story.getUser().getId(), currentUser.getId())) {
                throw new CustomException(ErrorCode.FORBIDDEN); // 본인이 작성한 사연이 아닐 경우 권한 없음
            }

            return toDto(story);

        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.DATA_ACCESS_ERROR);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
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
    public List<StoryResponse> myStories(Long userId) {
        return storyRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 엔티티를 DTO로 변환
    private StoryResponse toDto(Story s) {
        return StoryResponse.builder()
                .content(s.getContent())
                .createdAt(s.getCreatedAt())
                .cheerMessages(s.getCheerMessages())
                .category(s.getCategory())
                .build();
    }
}