package com.trabean.account.dto.request;

import lombok.Getter;

@Getter
public class CreateDomesticTravelAccountRequestDTO {
    private String password;
    private String accountName;
    private Long targetAmount;
}
