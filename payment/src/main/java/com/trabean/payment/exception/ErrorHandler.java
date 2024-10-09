package com.trabean.payment.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(PaymentsException.class)
    public ResponseEntity<ErrorResponse> handlePaymentsException(PaymentsException ex) {

        // 사용자에게 보여줄 커스텀 응답 객체
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getStatus().value(), ex.getKrwPrice());

        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }
}
