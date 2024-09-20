package com.trabean.ssafy.api.account.foriegn.dto.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.RequestHeader;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InquireForeignCurrencyDemandDepositAccountListRequestDTO {

    @JsonProperty("Header")
    private RequestHeader header;

    @JsonProperty("currency")
    private List<String> currency;
}
