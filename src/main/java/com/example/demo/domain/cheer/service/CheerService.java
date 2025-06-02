package com.example.demo.domain.cheer.service;

import com.example.demo.domain.cheer.dto.*;
import com.example.demo.domain.cheer.entity.CheerMessage;
import com.example.demo.domain.cheer.entity.UserCheerLimit;
import com.example.demo.domain.cheer.repository.CheerRepository;
import com.example.demo.domain.cheer.repository.UserCheerLimitRepository;
import com.example.demo.domain.story.entity.Story;
import com.example.demo.domain.story.repository.StoryRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.global.auth.PrincipalDetails;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service // 서비스 계층 선언
@RequiredArgsConstructor // 생성자 주입
public class CheerService {

    private final CheerRepository cheerRepository;
    private final StoryRepository storyRepository;
    private final UserCheerLimitRepository limitRepository;

    @Transactional
    public CheerResponse create(PrincipalDetails principal, CheerRequest req) {
        try {
            User user = principal.getUser();
            if (user == null) throw new CustomException(ErrorCode.UNAUTHORIZED);

            if (req.getContent() == null || req.getContent().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_CONTENT);
            }

            Story story = storyRepository.findById(req.getStoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));

            CheerMessage cm = CheerMessage.builder()
                    .content(req.getContent())
                    .createdAt(LocalDateTime.now())
                    .user(user) // 로그인 사용자 ID 사용
                    .category(story.getCategory()) // 사연의 카테고리 사용
                    .story(story)
                    .build();

            cheerRepository.save(cm);
            return toDto(cm);
        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.DATA_ACCESS_ERROR);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true) // 특정 사연의 응원 메시지 전체 조회
    public List<CheerResponse> findByStory(Long storyId) {
        return cheerRepository.findByStory_StoryId(storyId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional // 랜덤 응원 메시지 조회 (카테고리 기반, 하루 3회 제한 포함)
    public CheerResponse randomByCategory(PrincipalDetails principal, Long categoryId) {
        LocalDate today = LocalDate.now();
        User user= principal.getUser();
        UserCheerLimit limit = limitRepository
                .findByUserNumberAndDate(user.getId(), today)
                .orElseGet(() -> UserCheerLimit.builder()
                        .userNumber(user.getId())
                        .date(today)
                        .count(0)
                        .build());

        if (limit.getCount() >= 3) {
            throw new CustomException(ErrorCode.FORBIDDEN); // 하루 3회 초과
        }

        limit.setCount(limit.getCount() + 1); // 횟수 +1
        limitRepository.save(limit);

        CheerMessage cm = cheerRepository.findRandomByCategory(categoryId); // 카테고리에 해당하는 응원 메시지 중 하나 무작위 조회
        return toDto(cm);
    }

    @Transactional
    public CheerResponse update(Long id, PrincipalDetails principal, CheerRequest req) {
        User user = principal.getUser();
        if (user == null) throw new CustomException(ErrorCode.UNAUTHORIZED); // 로그인하지 않은 사용자

        CheerMessage cm = cheerRepository.findById(id) // 수정할 응원 메시지 조회
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND));

        if (!Objects.equals(cm.getUser().getId(), user.getId())) { // 본인이 작성한 응원 메시지가 아닌 경우 권한 없음
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (req.getContent() == null || req.getContent().trim().isEmpty()) { // 응원 메시지 내용이 비어 있는 경우
            throw new CustomException(ErrorCode.INVALID_CONTENT);
        }

        cm.setContent(req.getContent());
        return toDto(cm);
    }

    @Transactional // 응원 메시지 삭제
    public void delete(Long id, PrincipalDetails principal) {
        User user = principal.getUser();
        if (user == null) throw new CustomException(ErrorCode.UNAUTHORIZED); // 로그인하지 않은 사용자
        CheerMessage cm = cheerRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND));

        if (!Objects.equals(cm.getUser().getId(), user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        cheerRepository.delete(cm);
    }

    // 엔티티를 DTO로 변환
    private CheerResponse toDto(CheerMessage cm) {
        return CheerResponse.builder()
                .cheerId(cm.getCheerMessageId())
                .content(cm.getContent())
                .createdAt(cm.getCreatedAt())
                .username(cm.getUser().getUsername())
                .categoryName(cm.getCategory().getCategoryName())
                .build();
    }
}