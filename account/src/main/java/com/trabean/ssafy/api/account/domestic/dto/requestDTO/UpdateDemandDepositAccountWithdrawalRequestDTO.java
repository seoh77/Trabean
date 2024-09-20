package com.trabean.ssafy.api.account.domestic.dto.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.RequestHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateDemandDepositAccountWithdrawalRequestDTO {

    @JsonProperty("Header")
    private RequestHeader header;

    @JsonProperty("accountNo")
    private String accountNo;

    @JsonProperty("transactionBalance")
    private Long transactionBalance;

    @JsonProperty("transactionSummary")
    private String transactionSummary;
}
