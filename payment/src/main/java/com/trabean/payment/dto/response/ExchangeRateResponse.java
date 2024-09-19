package com.trabean.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {
    private Header Header;
    private REC REC;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        private String responseCode;
        private String responseMessage;
        private String apiName;
        private String transmissionDate;
        private String transmissionTime;
        private String institutionCode;
        private String apiKey;
        private String apiServiceCode;
        private String institutionTransactionUniqueNo;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class REC {
        private Long id;
        private String currency;
        private Double exchangeRate;
        private Double exchangeMin;
        private String created;
    }
}
