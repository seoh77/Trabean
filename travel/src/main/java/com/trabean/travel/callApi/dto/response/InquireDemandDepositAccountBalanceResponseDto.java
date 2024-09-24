package com.trabean.travel.callApi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.ResponseHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InquireDemandDepositAccountBalanceResponseDto {

    @JsonProperty("Header")
    private ResponseHeader header;

    @JsonProperty("REC")
    private REC rec;

    @Getter
    public static class REC {
        private String bankCode;
        private String accountNo;
        private Long accountBalance;
        private String accountCreatedDate;
        private String accountExpireDate;
        private String lastTransactionDate;
        private String currency;
    }

}
