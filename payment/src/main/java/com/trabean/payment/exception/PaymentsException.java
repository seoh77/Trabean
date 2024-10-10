package com.trabean.payment.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class PaymentsException extends RuntimeException {
    
    private HttpStatus status;  // HTTP 상태 코드
    private Long krwPrice;

    // 메시지와 상태 코드를 받는 생성자
    public PaymentsException(String message, HttpStatus status) {
        super(message);  // 예외 메시지를 부모 클래스에 전달
        this.status = status;  // 상태 코드 설정
    }

    // 메시지와 상태 코드와 한국 결제 금액 받는 생성자
    public PaymentsException(String message, Long krwPrice, HttpStatus status) {
        super(message);  // 예외 메시지를 부모 클래스에 전달
        this.krwPrice = krwPrice;  // 한국 결제 금액 설정
        this.status = status;  // 상태 코드 설정
    }

    // 상태 코드만 받는 생성자 (메시지가 필요 없을 때)
    public PaymentsException(HttpStatus status) {
        this.status = status;  // 상태 코드 설정
    }

}
