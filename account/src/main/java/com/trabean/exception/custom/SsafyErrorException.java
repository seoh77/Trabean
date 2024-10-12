package com.trabean.exception.custom;

import com.trabean.external.ssafy.common.SsafyApiResponseDTO;
import lombok.Getter;

/**
 * SSAFY 금융 API Feign Client 에서 발생하는 예외
 */
@Getter
public class SsafyErrorException extends RuntimeException {

    private final SsafyApiResponseDTO errorResponse;

    public SsafyErrorException(SsafyApiResponseDTO errorResponse) {
        this.errorResponse = errorResponse;
    }

}
