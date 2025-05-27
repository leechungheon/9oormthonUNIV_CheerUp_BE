package com.example.demo.domain.cheer.controller;

import com.example.demo.domain.cheer.dto.*;
import com.example.demo.domain.cheer.service.CheerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
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

    @PostMapping // 응원 메시지 생성
    public ResponseEntity<CheerResponse> create(@Valid @RequestBody CheerRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cheerService.create(req));
    }

    @GetMapping("/story/{storyId}") // 특정 사연에 대한 응원 메시지 조회
    public ResponseEntity<List<CheerResponse>> byStory(@PathVariable Long storyId) {
        return ResponseEntity.ok(cheerService.findByStory(storyId));
    }

    @GetMapping("/random") // 카테고리 기반 랜덤 응원 메시지 조회 (사용자 번호 포함)
    public ResponseEntity<CheerResponse> random(
            @RequestParam String category,
            @RequestParam Long userNumber) {
        return ResponseEntity.ok(cheerService.randomByCategory(category, userNumber));
    }

    @PutMapping("/{id}") // 응원 메시지 수정
    public ResponseEntity<CheerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CheerRequest req) {
        return ResponseEntity.ok(cheerService.update(id, req));
    }

    @DeleteMapping("/{id}") // 응원 메시지 삭제
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cheerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}