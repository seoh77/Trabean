package com.trabean.verification.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OneWonVerificationRequestDTO {
    private String userKey;
    private String accountNo;
    private String otp;
}
