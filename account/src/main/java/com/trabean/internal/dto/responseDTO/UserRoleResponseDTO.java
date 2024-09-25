package com.trabean.internal.dto.responseDTO;

import com.trabean.account.domain.UserAccountRelation;
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
    private UserAccountRelation.UserRole userRole;
}
