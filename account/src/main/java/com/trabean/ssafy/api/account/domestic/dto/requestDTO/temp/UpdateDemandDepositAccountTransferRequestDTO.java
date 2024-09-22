package com.trabean.ssafy.api.account.domestic.dto.requestDTO.temp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.RequestHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateDemandDepositAccountTransferRequestDTO {

    @JsonProperty("Header")
    private RequestHeader header;

    @JsonProperty("depositAccountNo")
    private String depositAccountNo;

    @JsonProperty("transactionBalance")
    private Long transactionBalance;

    @JsonProperty("withdrawalAccountNo")
    private String withdrawalAccountNo;

    @JsonProperty("depositTransactionSummary")
    private String depositTransactionSummary;

    @JsonProperty("withdrawalTransactionSummary")
    private String withdrawalTransactionSummary;
}
