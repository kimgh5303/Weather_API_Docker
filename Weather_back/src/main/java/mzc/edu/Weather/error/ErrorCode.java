package mzc.edu.Weather.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// enum class를 이용해 Gender라는 새로운 상수들의 타입을 정의
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    OK(HttpStatus.OK, "요청이 성공하였습니다."),  // 200
    NEED_TO_LOGIN(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다"),  // 401
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),  // 404
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "기업을 찾을 수 없습니다"),  // 404
    USER_EXIST(HttpStatus.CONFLICT, "회원이 이미 존재합니다"),  // 409
    COMPANY_EXIST(HttpStatus.CONFLICT, "기업이 이미 존재합니다"),  // 409
    TRANS_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "처리 중에 오류가 발생했습니다");  // 500

    private final HttpStatus httpStatus;
    private final String message;
}

