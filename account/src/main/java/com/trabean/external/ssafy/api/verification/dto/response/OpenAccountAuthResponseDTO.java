package com.trabean.external.ssafy.api.verification.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.external.ssafy.util.ResponseHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SSAFY 금융 API p.202 - 1원 송금 responseDTO
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAccountAuthResponseDTO {

    @JsonProperty("Header")
    private ResponseHeader header;

    @JsonProperty("REC")
    private REC rec;

    @Getter
    public static class REC {
        private Long transactionUniqueNo;
        private String accountNo;
    }
}
