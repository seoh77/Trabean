package com.trabean.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 다른 서버에서 발생한 성공 응답에 대한 공용 responseDTO
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalServerSuccessResponseDTO {

    @JsonProperty("message")
    private String message;
}