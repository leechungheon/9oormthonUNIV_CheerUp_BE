package com.example.demo.domain.cheer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service // 서비스 계층 선언
@RequiredArgsConstructor // 생성자 주입 (현재는 필드 없음)
public class CheerLimitService {

    // 사용자별 응원 횟수 제한 저장용 맵 (메모리 기반, 스레드 안전)
    private final Map<Long, CheerLimit> cheerLimitMap = new ConcurrentHashMap<>();

    // 하루 3회 응원 가능 여부 확인 및 횟수 증가
    public boolean canSendToday(Long userNumber) {
        CheerLimit limit = cheerLimitMap.getOrDefault(userNumber, new CheerLimit());

        // 날짜가 바뀌었으면 초기화
        if (!limit.getDate().equals(LocalDate.now())) {
            limit = new CheerLimit();
        }

        // 3회 이상이면 차단
        if (limit.getCount() >= 3) return false;

        // 응원 횟수 1 증가
        limit.increment();
        cheerLimitMap.put(userNumber, limit);
        return true;
    }

    // 사용자별 응원 횟수 정보 내부 클래스
    private static class CheerLimit {
        private LocalDate date = LocalDate.now(); // 기준 날짜
        private int count = 0; // 해당 날짜 응원 횟수

        public LocalDate getDate() {
            return date;
        }

        public int getCount() {
            return count;
        }

        public void increment() {
            // 날짜가 바뀌면 초기화
            if (!LocalDate.now().equals(date)) {
                date = LocalDate.now();
                count = 0;
            }
            count++;
        }
    }
}