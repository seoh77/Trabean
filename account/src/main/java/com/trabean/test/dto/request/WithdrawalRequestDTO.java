package com.trabean.test.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class WithdrawalRequestDTO {

    @JsonProperty("accountNo")
    private String accountNo;

    @JsonProperty("transactionBalance")
    private Long transactionBalance;

    @JsonProperty("transactionSummary")
    private String transactionSummary;
}
