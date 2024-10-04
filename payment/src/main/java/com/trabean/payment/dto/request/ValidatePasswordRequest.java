package com.trabean.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidatePasswordRequest {
    private Long payId;
    private Long accountId;
    private String password;
}
