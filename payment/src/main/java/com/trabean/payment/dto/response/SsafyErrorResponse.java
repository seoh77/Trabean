package com.trabean.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SsafyErrorResponse {
    private String responseCode;
    private String responseMessage;
}
