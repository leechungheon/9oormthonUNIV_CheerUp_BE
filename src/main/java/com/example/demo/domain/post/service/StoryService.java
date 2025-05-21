package com.example.demo.domain.post.service;

import com.example.demo.domain.post.dto.StoryDto;
import com.example.demo.domain.post.entity.Story;
import com.example.demo.domain.post.repository.StoryRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.global.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final StoryRepository storyRepository;
    // 스토리 작성
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StoryService.class);
    public void createStory(PrincipalDetails principal, StoryDto story) {
        User user = principal.getUser();  // PrincipalDetails에서 유저 객체 가져오기
        log.info("스토리 작성 요청 - 사용자 ID: {}, 사용자 이름: {}, content: '{}'",
                user.getId(), user.getUsername(), story.getContent());
        if(story.getContent()== null || story.getContent().isEmpty()){
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }
        Story newStory = Story.builder()
                .content(story.getContent())
                .user(user)  // 유저 정보 설정
                .build();

        storyRepository.save(newStory);
    }
}
