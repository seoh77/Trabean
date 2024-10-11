package com.trabean.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TravelAccountMemberListResponse {
    private Long memberCount;
    private Members[] members;

    @Getter
    @AllArgsConstructor
    public static class Members {
        private Long userId;
        private String userName;
        private String Role;
    }
}
