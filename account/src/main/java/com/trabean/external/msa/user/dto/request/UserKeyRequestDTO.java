package com.trabean.external.msa.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserKeyRequestDTO {
    private Long userId;
}
