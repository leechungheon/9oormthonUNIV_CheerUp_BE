package com.example.demo.domain.user.repository;

import com.example.demo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username); // 사용자명으로 조회

    Optional<User> findByEmail(String email); // 이메일로 조회
    
    boolean existsByUsername(String username); // 사용자명 존재 여부 확인
}