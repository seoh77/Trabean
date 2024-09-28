package com.trabean.account.dto.request;

import lombok.Getter;

@Getter
public class TransferDomesticTravelAccountRequestDTO {
    private String depositAccountNo;
    private String withdrawalAccountNo;
    private String depositTransactionSummary;
    private String withdrawalTransactionSummary;
    private Long transactionBalance;
}
