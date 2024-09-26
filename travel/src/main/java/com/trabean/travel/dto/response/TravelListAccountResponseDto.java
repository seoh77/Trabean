package com.trabean.travel.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TravelListAccountResponseDto {

    private String accountName;
    private List<TravelAccountResponseDto> account;

}
