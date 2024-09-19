package com.trabean.account.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountNoResponseDTO {
    private String accountNo;
    private String message;
}
