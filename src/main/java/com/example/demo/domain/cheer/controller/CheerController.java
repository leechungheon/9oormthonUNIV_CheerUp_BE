package com.example.demo.domain.cheer.controller;

import com.example.demo.domain.cheer.dto.*;
import com.example.demo.domain.cheer.service.CheerService;
import com.example.demo.global.auth.PrincipalDetails;
import com.example.demo.global.exception.CustomException;
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
@RequestMapping("/api/cheers") // 응원 API 엔드포인트 매핑
@Tag(name = "Cheer", description = "응원 메시지 관련 API") // Swagger 설명
@RequiredArgsConstructor // 생성자 주입을 통한 의존성 주입
@Validated // 유효성 검사 활성화
public class CheerController {

    private final CheerService cheerService; // 응원 서비스 의존성 주입

    @Operation(summary = "응원 메시지 생성")
    @PostMapping
    public ApiResponse<CheerResponse> create(@AuthenticationPrincipal PrincipalDetails principal, @Valid @RequestBody CheerRequest req) {
        CheerResponse createdCheer = cheerService.create(principal, req);
        return ApiResponse.success(createdCheer, "응원 메시지 생성 성공");
    }

    @Operation(summary = "응원 메시지 조회", description = "{storyId}의 응원 메시지를 조회합니다.")
    @GetMapping("/story/{storyId}")
    public ApiResponse<List<CheerResponse>> findByStory(@PathVariable Long storyId) {
        return ApiResponse.success(cheerService.findByStory(storyId), "응원 메시지 조회 성공");
    }

    @Operation(summary = "랜덤 응원 메시지 조회", description = "카테고리 기반 랜덤 응원 메시지를 하루 3회까지 조회할 수 있습니다.")
    @GetMapping("/random")
    public ApiResponse<?> randomByCategory(@RequestParam String category, @RequestParam Long userNumber) {
        try {
            CheerResponse randomCheer = cheerService.randomByCategory(category, userNumber);
            return ApiResponse.success(randomCheer, "랜덤 응원 메시지 조회 성공");
        } catch (CustomException e) {
            return ApiResponse.error(e.getErrorCode().getMessage());
        }
    }

    @Operation(summary = "응원 메시지 수정")
    @PutMapping("/{id}") // 응원 메시지 수정
    public ResponseEntity<CheerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CheerRequest req) {
        return ResponseEntity.ok(cheerService.update(id, req));
    }

    @Operation(summary = "응원 메시지 삭제")
    @DeleteMapping("/{id}") // 응원 메시지 삭제
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cheerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}