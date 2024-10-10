package com.trabean.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
public class ErrorHandler {

    /**
     * PaymentsException 처리
     */
    @ExceptionHandler(PaymentsException.class)
    public ResponseEntity<ErrorResponse> handlePaymentsException(PaymentsException ex) {
        // 사용자에게 보여줄 커스텀 응답 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getStatus().value(), ex.getKrwPrice());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    /**
     * 그 외 발생할 수 있는 일반적인 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        // 서버 내부 오류 메시지 처리
        ErrorResponse errorResponse = new ErrorResponse("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
