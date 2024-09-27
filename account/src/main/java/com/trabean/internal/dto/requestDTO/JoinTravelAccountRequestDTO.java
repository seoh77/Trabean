package com.trabean.internal.dto.requestDTO;

import lombok.Getter;

import java.util.List;

@Getter
public class JoinTravelAccountRequestDTO {
    private Long userId;
    private Long domesticAccountId;
    private List<Long> foreignAccountIdList;
}
