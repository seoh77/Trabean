package com.trabean.internal.dto.responseDTO;

import com.trabean.account.domain.UserAccountRelation.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponseDTO {
    private String message;
    private UserRole userRole;
}
