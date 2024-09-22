package com.trabean.ssafy.api.account.domestic.dto.requestDTO.temp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.RequestHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InquireTransactionHistoryRequestDTO {

    @JsonProperty("Header")
    private RequestHeader header;

    @JsonProperty("accountNo")
    private String accountNo;

    @JsonProperty("transactionUniqueNo")
    private Long transactionUniqueNo;
}
