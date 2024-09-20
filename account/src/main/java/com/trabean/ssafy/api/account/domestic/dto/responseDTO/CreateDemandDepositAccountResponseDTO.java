package com.trabean.ssafy.api.account.domestic.dto.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.ResponseHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDemandDepositAccountResponseDTO {

    @JsonProperty("Header")
    private ResponseHeader header;

    @JsonProperty("REC")
    private REC rec;

    @Data
    public static class REC {
        private String bankCode;
        private String accountNo;
        private Currency currency;
    }

    @Data
    public static class Currency {
        private String currency;
        private String currencyName;
    }
}
