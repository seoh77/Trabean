package com.trabean.exception.dto;

import com.trabean.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SsafyErrorResponseDTO {
    private ResponseCode responseCode;
    private String responseMessage;
}
