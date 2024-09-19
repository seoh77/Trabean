package com.trabean.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateRequest {

    @JsonProperty("Header")
    private Header header;  // 필드명은 소문자로, 직렬화 시에는 대문자로 변환

    @JsonProperty("currency")
    private String currency;

    @Override
    public String toString() {
        return "ExchangeRateRequest{" +
                "header=" + header +
                ", currency='" + currency + '\'' +
                '}';
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {

        @JsonProperty("apiName")
        private String apiName;

        @JsonProperty("transmissionDate")
        private String transmissionDate;

        @JsonProperty("transmissionTime")
        private String transmissionTime;

        @JsonProperty("institutionCode")
        private String institutionCode;

        @JsonProperty("fintechAppNo")
        private String fintechAppNo;

        @JsonProperty("apiServiceCode")
        private String apiServiceCode;

        @JsonProperty("institutionTransactionUniqueNo")
        private String institutionTransactionUniqueNo;

        @JsonProperty("apiKey")
        private String apiKey;

        @Override
        public String toString() {
            return "Header{" +
                    "apiName='" + apiName + '\'' +
                    ", transmissionDate='" + transmissionDate + '\'' +
                    ", transmissionTime='" + transmissionTime + '\'' +
                    ", institutionCode='" + institutionCode + '\'' +
                    ", fintechAppNo='" + fintechAppNo + '\'' +
                    ", apiServiceCode='" + apiServiceCode + '\'' +
                    ", institutionTransactionUniqueNo='" + institutionTransactionUniqueNo + '\'' +
                    ", apiKey='" + apiKey + '\'' +
                    '}';
        }
    }
}
