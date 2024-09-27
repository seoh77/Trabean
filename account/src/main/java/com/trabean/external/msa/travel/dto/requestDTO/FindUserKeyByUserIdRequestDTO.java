package com.trabean.external.msa.travel.dto.requestDTO;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FindUserKeyByUserIdRequestDTO {
    private Long userId;
}
