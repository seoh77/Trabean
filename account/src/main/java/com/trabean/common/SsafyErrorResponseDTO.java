package com.trabean.common;

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
