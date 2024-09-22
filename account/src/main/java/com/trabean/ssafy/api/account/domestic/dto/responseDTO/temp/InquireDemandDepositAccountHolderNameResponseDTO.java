package com.trabean.ssafy.api.account.domestic.dto.responseDTO.temp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.ResponseHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InquireDemandDepositAccountHolderNameResponseDTO {

    @JsonProperty("Header")
    private ResponseHeader header;

    @JsonProperty("REC")
    private REC rec;

    @Data
    public static class REC {
        private String bankCode;
        private String bankName;
        private String accountNo;
        private String userName;
        private String currency;
    }
}
