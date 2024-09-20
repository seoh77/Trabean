package com.trabean.ssafy.api.account.domestic.dto.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.RequestHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InquireDemandDepositAccountListRequestDTO {

    @JsonProperty("Header")
    private RequestHeader header;
}
