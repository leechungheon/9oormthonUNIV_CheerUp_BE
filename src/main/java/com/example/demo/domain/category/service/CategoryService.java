package com.example.demo.domain.category.service;

import com.example.demo.domain.category.dto.*;
import com.example.demo.domain.category.entity.Category;
import com.example.demo.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // 서비스 계층 선언
@RequiredArgsConstructor // 생성자 주입을 통한 의존성 주입
public class CategoryService {

    private final CategoryRepository categoryRepository; // 카테고리 리포지토리 의존성 주입

    @Transactional // 트랜잭션 처리 (쓰기)
    public CategoryResponse create(CategoryRequest request) {
        // 중복 카테고리 이름 확인
        if (categoryRepository.findByCategoryName(request.getCategoryName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }

        // 카테고리 생성 및 저장
        Category category = Category.builder()
                .categoryName(request.getCategoryName())
                .build();

        category = categoryRepository.save(category);

        // 응답 객체로 변환 후 반환
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }

    @Transactional(readOnly = true) // 트랜잭션 처리 (읽기 전용)
    public List<CategoryResponse> findAll() {
        // 전체 카테고리 조회 후 DTO 리스트로 변환
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryResponse(cat.getCategoryId(), cat.getCategoryName()))
                .collect(Collectors.toList());
    }
}