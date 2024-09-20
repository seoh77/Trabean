package com.trabean.ssafy.api.account.domestic.dto.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.ResponseHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InquireTransactionHistoryResponseDTO {

    @JsonProperty("Header")
    private ResponseHeader header;

    @JsonProperty("REC")
    private REC rec;

    @Data
    public static class REC {
        private Long transactionUniqueNo;
        private String transactionDate;
        private String transactionTime;
        private String transactionType;
        private String transactionTypeName;
        private String transactionAccountNo;
        private Long transactionBalance;
        private Long transactionAfterBalance;
        private String transactionSummary;
        private String transactionMemo;
    }
}
