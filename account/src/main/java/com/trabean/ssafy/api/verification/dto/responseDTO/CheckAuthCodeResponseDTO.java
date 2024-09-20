package com.trabean.ssafy.api.verification.dto.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.ResponseHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckAuthCodeResponseDTO {

    @JsonProperty("Header")
    private ResponseHeader header;

    @JsonProperty("REC")
    private REC rec;

    @Data
    public static class REC {
        private String status;
        private Long transactionUniqueNo;
        private String accountNo;
    }
}
