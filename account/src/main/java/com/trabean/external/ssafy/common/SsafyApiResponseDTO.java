package com.trabean.external.ssafy.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trabean.external.ssafy.constant.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SSAFY 금융 API 응답 코드와 메시지를 반환하는 responseDTO
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SsafyApiResponseDTO {

    @JsonProperty("responseCode")
    private ResponseCode responseCode;

    @JsonProperty("responseMessage")
    private String responseMessage;
}
