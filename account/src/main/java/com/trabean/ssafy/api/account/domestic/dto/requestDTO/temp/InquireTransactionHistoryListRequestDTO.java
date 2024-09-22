package com.trabean.ssafy.api.account.domestic.dto.requestDTO.temp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.util.RequestHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InquireTransactionHistoryListRequestDTO {

    @JsonProperty("Header")
    private RequestHeader header;

    @JsonProperty("accountNo")
    private String accountNo;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;

    @JsonProperty("transactionType")
    private String transactionType;

    @JsonProperty("orderByType")
    private String orderByType;
}
