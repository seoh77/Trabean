package com.trabean.external.ssafy.api.domestic.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.external.ssafy.util.ResponseHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SSAFY 금융 API p.42 - 계좌 조회 (단건) responseDTO
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InquireDemandDepositAccountResponseDTO {

    @JsonProperty("Header")
    private ResponseHeader header;

    @JsonProperty("REC")
    private REC rec;

    /**
     * SSAFY 금융 API p.38 - 계좌 목록 조회 responseDTO의 REC과 동일 타입
     */
    @Getter
    public static class REC extends InquireDemandDepositAccountListResponseDTO.REC {
        private String bankCode;
        private String bankName;
        private String userName;
        private String accountNo;
        private String accountName;
        private String accountTypeCode;
        private String accountTypeName;
        private String accountCreatedDate;
        private String accountExpiryDate;
        private Long dailyTransferLimit;
        private Long oneTimeTransferLimit;
        private Long accountBalance;
        private String lastTransactionDate;
        private String currency;
    }
}
