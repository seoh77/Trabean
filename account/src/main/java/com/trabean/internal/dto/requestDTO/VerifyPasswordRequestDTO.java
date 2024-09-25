package com.trabean.internal.dto.requestDTO;

import lombok.Getter;

@Getter
public class VerifyPasswordRequestDTO {
    private Long userId;
    private Long accountId;
    private String password;
}
