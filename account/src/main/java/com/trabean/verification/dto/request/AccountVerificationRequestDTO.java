package com.trabean.verification.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountVerificationRequestDTO {
    private String userKey;
    private String accountNo;
}
