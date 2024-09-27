package com.trabean.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SSAFY 금융 API 에서 요청 실패 시 반환하는 responseDTO
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SsafyErrorResponseDTO {
    private ResponseCode responseCode;
    private String responseMessage;
}
