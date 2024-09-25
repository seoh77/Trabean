package com.trabean.external.travel.dto.requestDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class DomesticTravelAccountSaveRequestDTO {
    private Long accountId;
    private String accountName;
    private Long targetAmount;
}
