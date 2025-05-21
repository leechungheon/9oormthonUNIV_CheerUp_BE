package com.example.demo.domain.post.controller;

import com.example.demo.domain.post.dto.StoryDto;
import com.example.demo.domain.post.service.StoryService;
import com.example.demo.global.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/story")
@Tag(name = "Story", description = "응원함 관련 API")
@RequiredArgsConstructor
public class StoryController {
    @Autowired
    private StoryService storyService;
    // 스토리 작성
    @PostMapping("/create")
    public ResponseEntity<String> createStory(@AuthenticationPrincipal PrincipalDetails principal, @RequestBody @Valid StoryDto story) {
        storyService.createStory(principal,story);
        return ResponseEntity.ok("스토리가 작성되었습니다.");
    }
}
