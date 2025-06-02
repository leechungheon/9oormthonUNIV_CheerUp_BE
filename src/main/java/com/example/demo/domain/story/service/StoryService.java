package com.example.demo.domain.story.service;

import com.example.demo.domain.category.dto.CategoryResponse;
import com.example.demo.domain.category.repository.CategoryRepository;
import com.example.demo.domain.cheer.dto.CheerResponse;
import com.example.demo.domain.story.dto.*;
import com.example.demo.domain.story.entity.Story;
import com.example.demo.domain.story.repository.StoryRepository;
import com.example.demo.domain.cheer.repository.CheerRepository;
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
    private final CheerRepository cheerRepository;

    @Transactional // 응원함 생성
    public StoryResponse create(PrincipalDetails principal, StoryRequest req) {
        try {
            User user = principal.getUser(); // 로그인한 유저 객체 가져오기
            if (user == null) {
                throw new CustomException(ErrorCode.UNAUTHORIZED); // 로그인 안되어 있다면
            }

            // 응원 1회 이상 여부 체크
            long cheerCount = cheerRepository.countByUserNumberAndOthers(user.getId());
            if (cheerCount < 1) {
                throw new CustomException(ErrorCode.FORBIDDEN);
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

    @Transactional(readOnly = true) // 전체 응원함 조회
    public List<StoryResponse> findAll() {
        return storyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // ID로 응원함 단건 조회 (인증 없음)
    public StoryResponse getStoryById(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));
        return toDto(story);
    }

    @Transactional // 응원함 수정
    public StoryResponse update(Long id, PrincipalDetails principal, StoryRequest req) {
        User currentUser = principal.getUser();
        if (currentUser == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED); // 로그인 안되어 있다면
        }

        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));

        if (!Objects.equals(story.getUser().getId(), currentUser.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN); // 다른 사용자의 스토리
        }

        // 입력 값 유효성 검증
        if (req.getContent() == null || req.getContent().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_CONTENT);
        }

        /*if (req.getContent().length() > 1000) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "내용이 너무 깁니다.");
        }*/

        try {
            story.setContent(req.getContent());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return toDto(story);
    }


    @Transactional // 응원함 삭제
    public void delete(Long id, PrincipalDetails principal) {
        User currentUser = principal.getUser();
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));

        if (!Objects.equals(story.getUser().getId(), currentUser.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        storyRepository.delete(story);
    }

    @Transactional(readOnly = true) // 랜덤 응원함 N개 조회
    public List<StoryResponse> random(int size) {
        return storyRepository.findRandomStories(size).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // 인기 응원함 N개 조회 (응원메시지 수 기준)
    public List<StoryResponse> popular(int size) {
        return storyRepository.findPopularStories(PageRequest.of(0, size)).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // 나의 응원함 목록 조회
    public List<StoryResponse> myStories(PrincipalDetails principal) {
        User currentUser = principal.getUser();
        if (currentUser == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED); // 로그인 안되어 있다면
        }
        return storyRepository.findByUserId(currentUser.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 엔티티를 DTO로 변환
    public StoryResponse toDto(Story s) {
        return StoryResponse.builder()
                .storyId(s.getStoryId())
                .content(s.getContent())
                .createdAt(s.getCreatedAt())
                .categoryName(s.getCategory().getCategoryName())
                .username(s.getUser().getUsername())
                .cheerCount(s.getCheerMessages().size())
                .build();
    }
}

