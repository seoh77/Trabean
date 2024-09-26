package com.trabean.travel.callApi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.travel.dto.response.ExchangeResponseDto.AccountInfo;
import com.trabean.travel.dto.response.ExchangeResponseDto.ExchangeCurrency;
import com.trabean.util.ResponseHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeApiResponseDto {

    @JsonProperty("Header")
    private ResponseHeader header;

    @JsonProperty("REC")
    private REC rec;

    @Getter
    public static class REC {

        @JsonProperty("exchangeCurrency")
        private ExchangeCurrency exchangeCurrency;

        @JsonProperty("accountInfo")
        private AccountInfo accountInfo;
    }

}
