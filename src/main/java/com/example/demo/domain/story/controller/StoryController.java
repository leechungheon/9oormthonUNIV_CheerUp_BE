package com.example.demo.domain.story.controller;

import com.example.demo.domain.story.dto.*;
import com.example.demo.domain.story.service.StoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController // REST API 컨트롤러
@RequestMapping("/api/stories") // 사연 API 엔드포인트 매핑
@Tag(name = "Story", description = "사연 CRUD 및 조회 API") // Swagger 설명
@RequiredArgsConstructor // 생성자 주입
@Validated // 유효성 검사 활성화
public class StoryController {

    private final StoryService storyService; // 사연 서비스 의존성 주입

    @PostMapping // 사연 생성
    public ResponseEntity<StoryResponse> create(@Valid @RequestBody StoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storyService.create(req));
    }

    @GetMapping // 전체 사연 조회
    public ResponseEntity<List<StoryResponse>> findAll() {
        return ResponseEntity.ok(storyService.findAll());
    }

    @GetMapping("/{id}") // ID로 사연 단건 조회
    public ResponseEntity<StoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(storyService.findById(id));
    }

    @PutMapping("/{id}") // 사연 수정
    public ResponseEntity<StoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody StoryRequest req) {
        return ResponseEntity.ok(storyService.update(id, req));
    }

    @DeleteMapping("/{id}") // 사연 삭제
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/random") // 랜덤 사연 목록 조회 (기본 5개)
    public ResponseEntity<List<StoryResponse>> random(
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(storyService.random(size));
    }

    @GetMapping("/popular") // 인기 사연 목록 조회 (기본 10개)
    public ResponseEntity<List<StoryResponse>> popular(
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(storyService.popular(size));
    }

    @GetMapping("/my") // 특정 사용자의 사연 목록 조회
    public ResponseEntity<List<StoryResponse>> myStories(
            @RequestParam Long userNumber) {
        return ResponseEntity.ok(storyService.myStories(userNumber));
    }
}