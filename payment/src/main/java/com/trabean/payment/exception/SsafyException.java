package com.trabean.payment.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SsafyException extends RuntimeException {

    private String responseCode;
    private String responseMessage;

    // 메시지와 상태 코드를 받는 생성자
    public SsafyException(String message, String code) {
        this.responseCode = code;
        this.responseMessage = message;
    }
}
