package com.trabean.payment.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

/**
 * 전역 예외 처리 핸들러
 */
@ControllerAdvice
@Slf4j
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

    @ExceptionHandler(SsafyException.class)
    public ResponseEntity<ErrorResponse> handleSsafyException(SsafyException ex) {
        // 사용자에게 보여줄 커스텀 응답 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(ex.getResponseMessage(), 500, 0L);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * FeignException 처리
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        log.error("FeignException 발생: {}", ex.getMessage());
        // FeignException 처리 로직
        ErrorResponse errorResponse = new ErrorResponse("외부 API 호출 오류: " + ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                null);
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * RestClientException 처리
     */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleRestClientException(RestClientException ex) {
        log.error("RestClientException 발생: {}", ex.getMessage());
        // RestClientException 처리 로직
        ErrorResponse errorResponse = new ErrorResponse("API 호출 중 오류: " + ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                null);
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * 그 외 발생할 수 있는 일반적인 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException 발생: {}", ex.getMessage());
        // 서버 내부 오류 메시지 처리
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 그 외 처리되지 않은 일반 Exception 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("알 수 없는 오류 발생: {}", ex.getMessage());
        // 알 수 없는 오류 처리
        ErrorResponse errorResponse = new ErrorResponse("서버 오류: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
