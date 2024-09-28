package com.trabean.account.dto.request;

import lombok.Getter;

@Getter
public class CreateForeignTravelAccountRequestDTO {
    private Long domesticAccountId;
    private String currency;
}
