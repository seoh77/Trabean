package com.trabean.internal.dto.requestDTO;

import com.trabean.account.domain.UserAccountRelation;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdateUserRoleRequestDTO {
    private Long userId;
    private Long domesticAccountId;
    private List<Long> foreignAccountIdList;
    private UserAccountRelation.UserRole userRole;
}
