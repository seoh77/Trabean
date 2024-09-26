package com.trabean.external.feign.msa.travel.dto.requestDTO;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SaveDomesticTravelAccountRequestDTO {
    private Long accountId;
    private String accountName;
    private Long targetAmount;
}
