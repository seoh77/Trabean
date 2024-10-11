package com.trabean.payment.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String message;
    private int status;
    private Long krwPrice;   // 한국 결제 금액 (외화 결제 오류시 원화로 재결제 여부 물어보기 위함)
}