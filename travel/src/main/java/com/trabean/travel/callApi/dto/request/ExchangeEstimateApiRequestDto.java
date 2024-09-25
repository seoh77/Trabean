package com.trabean.travel.callApi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.RequestHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ExchangeEstimateApiRequestDto {

    @JsonProperty("Header")
    private RequestHeader header;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("exchangeCurrency")
    private String exchangeCurrency;

    @JsonProperty("amount")
    private double amount;

}
