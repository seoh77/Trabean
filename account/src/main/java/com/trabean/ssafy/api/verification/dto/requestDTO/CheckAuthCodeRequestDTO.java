package com.trabean.ssafy.api.verification.dto.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.RequestHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckAuthCodeRequestDTO {

    @JsonProperty("Header")
    private RequestHeader header;

    @JsonProperty("accountNo")
    private String accountNo;

    @JsonProperty("authText")
    private final String authText = "Trabean";

    @JsonProperty("authCode")
    private String authCode;
}
