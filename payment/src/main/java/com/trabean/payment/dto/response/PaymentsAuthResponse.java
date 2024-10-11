package com.trabean.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentsAuthResponse {
    private String transactionId;
    private Long payId;
    private String paymentDate;
}
