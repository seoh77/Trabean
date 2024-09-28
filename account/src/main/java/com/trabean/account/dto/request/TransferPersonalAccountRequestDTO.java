package com.trabean.account.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TransferPersonalAccountRequestDTO {

    @JsonProperty("depositAccountNo")
    private String depositAccountNo;

    @JsonProperty("withdrawalAccountNo")
    private String withdrawalAccountNo;

    @JsonProperty("depositTransactionSummary")
    private String depositTransactionSummary;

    @JsonProperty("withdrawalTransactionSummary")
    private String withdrawalTransactionSummary;

    @JsonProperty("transactionBalance")
    private Long transactionBalance;
}
