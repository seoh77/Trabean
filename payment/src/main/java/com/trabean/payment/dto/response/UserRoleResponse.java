package com.trabean.payment.dto.response;

import com.trabean.payment.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleResponse {
    private String status;
    private String message;
    private Long userId;
    private UserRole userRole;
}