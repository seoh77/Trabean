package com.trabean.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountNoResponse {
    private String message;
    private String accountNo;
}
