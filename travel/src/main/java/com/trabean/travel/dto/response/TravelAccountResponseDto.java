package com.trabean.travel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class TravelAccountResponseDto {

    private Long accountId;
    private String country;
    private String exchangeCurrency;
    private double accountBalance;

    public static TravelAccountResponseDto from(Long accountId, String country, String exchangeCurrency,
                                                double accountBalance) {
        return new TravelAccountResponseDto(
                accountId,
                country,
                exchangeCurrency,
                accountBalance
        );
    }

}