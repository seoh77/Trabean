package com.trabean.ssafy.api.account.foriegn.dto.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.ResponseHeader;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InquireForeignCurrencyDemandDepositAccountListResponseDTO {

    @JsonProperty("Header")
    private ResponseHeader header;

    @JsonProperty("REC")
    private List<REC> rec;

    @Data
    public static class REC {
        private String bankCode;
        private String bankName;
        private String userName;
        private String accountNo;
        private String accountName;
        private String accountTypeCode;
        private String accountTypeName;
        private String accountCreateDate;
        private String accountExpiryDate;
        private Double dailyTransferLimit;
        private Double oneTimeTransferLimit;
        private Double accountBalance;
        private String lastTransactionDate;
        private String currency;
    }
}
