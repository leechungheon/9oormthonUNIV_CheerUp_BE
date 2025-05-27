package com.example.demo.domain.cheer.repository;

import com.example.demo.domain.cheer.entity.UserCheerLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserCheerLimitRepository extends JpaRepository<UserCheerLimit, Long> {

    Optional<UserCheerLimit> findByUserNumberAndDate(Long userNumber, LocalDate date); // 특정 사용자와 날짜에 대한 응원 제한 기록 조회
}