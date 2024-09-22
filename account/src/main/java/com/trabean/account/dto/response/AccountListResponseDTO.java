package com.trabean.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountListResponseDTO {
    private String message;

    private List<Account> accountList;

    @Builder
    @Getter
    public static class Account {
        private Long accountId;
        private String accountNo;
        private String accountName;
        private Long accountBalance;
    }
}
