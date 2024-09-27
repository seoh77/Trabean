package com.trabean.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 다른 서버에서 발생한 에러에 대한 responseDTO
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalServerErrorResponseDTO {
    private String message;
}
