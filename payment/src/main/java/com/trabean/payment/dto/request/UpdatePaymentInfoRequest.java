package com.trabean.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePaymentInfoRequest {
    private Long accountId; // 돈 빠져나가는 계좌
    private Long merchantId;
    private Double foreignAmount;
    private Long krwAmount;
}
