package com.example.demo.domain.category.repository;

import com.example.demo.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName); // 카테고리 이름으로 조회
}