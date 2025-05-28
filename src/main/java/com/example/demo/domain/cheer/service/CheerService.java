package com.example.demo.domain.cheer.service;

import com.example.demo.domain.cheer.dto.*;
import com.example.demo.domain.cheer.entity.CheerMessage;
import com.example.demo.domain.cheer.entity.UserCheerLimit;
import com.example.demo.domain.cheer.repository.CheerRepository;
import com.example.demo.domain.cheer.repository.UserCheerLimitRepository;
import com.example.demo.domain.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service // 서비스 계층 선언
@RequiredArgsConstructor // 생성자 주입
public class CheerService {

    private final CheerRepository cheerRepository;
    private final StoryRepository storyRepository;
    private final UserCheerLimitRepository limitRepository;

    /*@Transactional // 응원 메시지 생성
    public CheerResponse create(CheerRequest req) {
        var story = storyRepository.findById(req.getStoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사연입니다."));

        CheerMessage cm = CheerMessage.builder()
                .story(story)
                .content(req.getContent())
                .userNumber(req.getUserNumber())
                .createdAt(LocalDateTime.now())
                .category(req.getCategory())
                .build();

        cm = cheerRepository.save(cm);
        return toDto(cm);
    }*/

    @Transactional(readOnly = true) // 특정 사연의 응원 메시지 전체 조회
    public List<CheerResponse> findByStory(Long storyId) {
        return cheerRepository.findByStory_StoryId(storyId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional // 랜덤 응원 메시지 조회 (카테고리 기반, 하루 3회 제한 포함)
    public CheerResponse randomByCategory(String category, Long userNumber) {
        LocalDate today = LocalDate.now();

        UserCheerLimit limit = limitRepository
                .findByUserNumberAndDate(userNumber, today)
                .orElseGet(() -> UserCheerLimit.builder()
                        .userNumber(userNumber)
                        .date(today)
                        .count(0)
                        .build());

        if (limit.getCount() >= 3) {
            throw new IllegalStateException("하루 응원 조회 제한(3회)을 초과했습니다.");
        }

        limit.setCount(limit.getCount() + 1);
        limitRepository.save(limit);

        CheerMessage cm = cheerRepository.findRandomByCategory(category);
        return toDto(cm);
    }

    @Transactional // 응원 메시지 내용 수정
    public CheerResponse update(Long id, CheerRequest req) {
        var cm = cheerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 응원 메시지입니다."));
        cm.setContent(req.getContent());
        return toDto(cm);
    }

    @Transactional // 응원 메시지 삭제
    public void delete(Long id) {
        cheerRepository.deleteById(id);
    }

    // 엔티티를 DTO로 변환
    private CheerResponse toDto(CheerMessage cm) {
        return CheerResponse.builder()
                //.cheerMessageId(cm.getCheerMessageId())
                //.storyId(cm.getStory().getStoryId())
                .content(cm.getContent())
                .createdAt(cm.getCreatedAt())
                //.userNumber(cm.getUserNumber())
                .category(cm.getCategory())
                .build();
    }
}