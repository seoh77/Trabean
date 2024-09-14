package com.trabean.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentInfoRequest {
    private Long payId;
    private String transactionId;
    private Long merchantId;
    private Long krwAmount;
    private Double foreignAmount;
}
