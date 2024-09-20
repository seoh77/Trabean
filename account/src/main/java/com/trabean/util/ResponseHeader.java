package com.trabean.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseHeader {
    String responseCode;
    String responseMessage;
    String apiName;
    String transmissionDate;
    String transmissionTime;
    final String institutionCode = "00100";
    final String fintechAppNo = "001";
    String apiServiceCode;
    String institutionTransactionUniqueNo;
}
