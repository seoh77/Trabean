package com.trabean.account.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRoleResponseDTO {
    private String userRole;
    private String message;
}
