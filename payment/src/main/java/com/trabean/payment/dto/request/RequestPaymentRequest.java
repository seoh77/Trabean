package com.trabean.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestPaymentRequest {
    private String transactionId;
    private Long userId;
    private Long payId;
    private Long merchantId;
    private Long krwAmount;
    private Double foreignAmount;
}
