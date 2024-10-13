package com.trabean.payment.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestPaymentRequest {
    private String transactionId;
    private Long payId;
    private Long merchantId;
    private Long krwAmount;
    private Double foreignAmount;
}
