package com.trabean.account.dto.response;

import com.trabean.common.ResponseCode;
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
    private ResponseCode responseCode;
    private String responseMessage;

    private List<Account> accountList;

    @Builder
    @Getter
    public static class Account {
        private Long accountId;
        private String accountNo;
        private String bankName;
        private Long accountBalance;
    }
}
