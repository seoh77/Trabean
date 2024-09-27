package com.trabean.external.msa.travel.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FindUserKeyByUserIdResponseDTO {
    private String userKey;
}
