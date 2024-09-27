package com.trabean.exception;

import com.trabean.common.SsafyErrorResponseDTO;
import lombok.Getter;

/**
 * SSAFY 금융 API Feign Client 에서 발생하는 예외
 */
@Getter
public class SsafyErrorException extends RuntimeException {

    private final SsafyErrorResponseDTO errorResponse;

    public SsafyErrorException(SsafyErrorResponseDTO errorResponse) {
        this.errorResponse = errorResponse;
    }

}
