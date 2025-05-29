package com.example.demo.domain.story.controller;

import com.example.demo.domain.story.dto.*;
import com.example.demo.domain.story.service.StoryService;
import com.example.demo.global.auth.PrincipalDetails;
import com.example.demo.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController // REST API 컨트롤러
@RequestMapping("/api/stories") // 사연 API 엔드포인트 매핑
@Tag(name = "Story", description = "응원함 관련 API") // Swagger 설명
@RequiredArgsConstructor // 생성자 주입
@Validated // 유효성 검사 활성화
public class StoryController {

    private final StoryService storyService; // 응원함 서비스 의존성 주입

    @Operation(summary = "응원함 생성")
    @PostMapping("/create")
    public ApiResponse<StoryResponse> createStory(@AuthenticationPrincipal PrincipalDetails principal, @RequestBody @Valid StoryRequest story) {
        StoryResponse createdStory=storyService.create(principal,story);
        return ApiResponse.success(createdStory, "스토리 생성 성공");
    }

    @Operation(summary = "전체 응원함 조회")
    @GetMapping
    public ApiResponse<List<StoryResponse>> findAll() {
        return ApiResponse.success(storyService.findAll(), "스토리 전체 조회 성공");
    }

    @Operation(summary = "특정 응원함 조회", description = "{id}의 응원함을 조회합니다.")
    @GetMapping("/{storyId}")
    public ApiResponse<StoryResponse> getStoryById(@PathVariable Long storyId) {
        StoryResponse foundStory = storyService.getStoryById(storyId);
        return ApiResponse.success(foundStory, "스토리 단건 조회 성공");
    }

    @Operation(summary = "응원함 수정")
    @PutMapping("/{id}")
    public ApiResponse<StoryResponse> update(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principal, @RequestBody @Valid StoryRequest req) {
        return ApiResponse.success(storyService.update(id, principal, req), "스토리 수정 성공");
    }

    @Operation(summary = "응원함 삭제")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principal) {
        storyService.delete(id, principal);
        return ApiResponse.success(null, "스토리 삭제 성공");
    }

    @Operation(summary = "응원함 랜덤 조회", description = "응원함을 랜덤으로 5개 조회합니다.")
    @GetMapping("/random")
    public ResponseEntity<List<StoryResponse>> random(
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(storyService.random(size));
    }

    @Operation(summary = "인기 응원함 조회", description = "인기 응원함을 10개 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<List<StoryResponse>> popular(
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(storyService.popular(size));
    }

    @Operation(summary = "특정 사용자의 사연목록 조회", description = "수정필요.")
    @GetMapping("/my")
    public ResponseEntity<List<StoryResponse>> myStories(
            @RequestParam Long userNumber) {
        return ResponseEntity.ok(storyService.myStories(userNumber));
    }
}