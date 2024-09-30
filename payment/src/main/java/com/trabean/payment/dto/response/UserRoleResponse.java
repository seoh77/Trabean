package com.trabean.payment.dto.response;

import com.trabean.payment.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserRoleResponse {
    private String message;
    private UserRole userRole;
}