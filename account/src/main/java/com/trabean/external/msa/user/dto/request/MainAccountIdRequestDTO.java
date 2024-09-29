package com.trabean.external.msa.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MainAccountIdRequestDTO {
    private Long userId;
    private Long mainAccountId;
}
