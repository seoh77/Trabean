package com.trabean.ssafy.api.account.foriegn.dto.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.RequestHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateForeignCurrencyDemandDepositAccountRequestDTO {

    @JsonProperty("Header")
    private RequestHeader header;

    @JsonProperty("accountTypeUniqueNo")
    private String accountTypeUniqueNo;

    @JsonProperty("currency")
    private String currency;
}
