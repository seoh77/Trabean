package com.trabean.external.feign.config;

import com.trabean.exception.dto.SsafyErrorResponseDTO;
import lombok.Getter;

@Getter
public class CustomFeignClientException extends RuntimeException {

    private final SsafyErrorResponseDTO errorResponse;

    public CustomFeignClientException(SsafyErrorResponseDTO errorResponse) {
        this.errorResponse = errorResponse;
    }

}
