package com.trabean.verification.dto.response;

import com.trabean.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OneWonVerificationResponseDTO {
    private ResponseCode responseCode;
    private String responseMessage;
}
