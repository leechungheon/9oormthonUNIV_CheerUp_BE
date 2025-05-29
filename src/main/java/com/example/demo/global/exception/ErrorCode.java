package com.example.demo.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 🔐 인증 및 인가 관련
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "사용자 인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 사용자 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 사용자 이름입니다."),
    INVALID_USER_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 상태입니다."),

    // 입력 및 요청 관련
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "내용을 입력해주세요."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리를 선택해주세요."),

    // 응원함/메시지 등 리소스 관련
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "응원메시지를 찾을 수 없습니다."),
    STORY_NOT_FOUND(HttpStatus.NOT_FOUND, "응원함을 찾을 수 없습니다."),

    // 데이터베이스 및 서버 관련
    DATA_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 처리 중 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "백엔드 서버 오류가 발생했습니다.."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서비스를 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus status; // HTTP 상태 코드
    private final String message;    // 프론트 또는 클라이언트에게 보여줄 메시지

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
