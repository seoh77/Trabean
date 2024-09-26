package com.trabean.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private String status;
    private String message;
    private PaymentInfo paymentInfo;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PaymentInfo {
        private Long krwAmount;
        private Double foreignAmount;
    }
}
