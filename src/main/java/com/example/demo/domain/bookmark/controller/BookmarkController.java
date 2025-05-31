package com.example.demo.domain.bookmark.controller;

import com.example.demo.domain.bookmark.service.BookmarkService;
import com.example.demo.domain.story.dto.StoryResponse;
import com.example.demo.domain.story.entity.Story;
import com.example.demo.domain.story.service.StoryService;
import com.example.demo.global.auth.PrincipalDetails;
import com.example.demo.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final StoryService storyService;

    // 북마크 추가
    @PostMapping("/{storyId}")
    @Operation(summary = "스토리 북마크 추가")
    public ApiResponse<Void> addBookmark(@PathVariable Long storyId, @RequestAttribute PrincipalDetails principal) {
        bookmarkService.addBookmark(storyId, principal.getUser());
        return ApiResponse.success(null, "북마크 추가 성공");
    }

    // 북마크 제거
    @DeleteMapping("/{storyId}")
    @Operation(summary = "스토리 북마크 삭제")
    public ApiResponse<Void> removeBookmark(@PathVariable Long storyId, @RequestAttribute PrincipalDetails principal) {
        bookmarkService.removeBookmark(storyId, principal.getUser());
        return ApiResponse.success(null, "북마크 삭제 성공");
    }

    // 내 북마크 목록 조회
    @GetMapping
    @Operation(summary = "내 북마크 목록 조회")
    public ApiResponse<List<StoryResponse>> myBookmarks(@RequestAttribute PrincipalDetails principal) {
        List<Story> bookmarks = bookmarkService.getMyBookmarks(principal.getUser());
        List<StoryResponse> response = bookmarks.stream()
                .map(storyService::toDto)
                .toList();
        return ApiResponse.success(response, "내 북마크 목록 조회 성공");
    }
}
