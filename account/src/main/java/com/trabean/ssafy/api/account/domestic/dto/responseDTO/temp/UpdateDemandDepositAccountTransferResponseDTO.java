package com.trabean.ssafy.api.account.domestic.dto.responseDTO.temp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.ResponseHeader;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateDemandDepositAccountTransferResponseDTO {

    @JsonProperty("Header")
    private ResponseHeader header;

    @JsonProperty("REC")
    private List<REC> rec;

    @Data
    public static class REC {
        private Long transactionUniqueNo;
        private String accountNo;
        private String transactionDate;
        private String transactionType;
        private String transactionTypeName;
        private String transactionAccountNo;
    }
}
