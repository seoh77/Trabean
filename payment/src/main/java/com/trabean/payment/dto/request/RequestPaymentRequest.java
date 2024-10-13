package com.trabean.payment.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "internalBuilder")
public class RequestPaymentRequest {
    private String transactionId;
    private Long payId;
    private Long merchantId;
    private Long krwAmount;
    private Double foreignAmount;

    // 필수 필드 빌더
    public static class RequestPaymentRequestBuilder {
        public RequestPaymentRequestBuilder(String transactionId, Long payId, Long merchantId) {
            this.transactionId = transactionId;
            this.payId = payId;
            this.merchantId = merchantId;
        }
    }
    
    public static RequestPaymentRequestBuilder builder(String transactionId, Long payId, Long merchantId) {
        return internalBuilder()
                .transactionId(transactionId)
                .payId(payId)
                .merchantId(merchantId);
    }
}
