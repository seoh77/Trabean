package com.trabean.ssafy.api.account.domestic.dto.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.RequestHeader;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InquireDemandDepositAccountListRequestDTO {

    @JsonProperty("Header")
    private RequestHeader header;
}
