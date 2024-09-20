package com.trabean.account.service;

import com.trabean.account.dto.response.AccountNoResponseDTO;
import com.trabean.account.dto.response.UserRoleResponseDTO;
import com.trabean.account.repository.AccountRepository;
import com.trabean.account.repository.UserAccountRelationRepository;
import com.trabean.exception.AccountNotFoundException;
import com.trabean.exception.UserAccountRelationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private final UserAccountRelationRepository userAccountRelationRepository;

    public AccountNoResponseDTO getAccountNoById(Long accountId) {
        String accountNo = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("해당 계좌를 찾을 수 없습니다."))
                .getAccountNo();

        return AccountNoResponseDTO.builder()
                .accountNo(accountNo)
                .message("성공")
                .build();
    }

    public UserRoleResponseDTO getUserRoleByUserIdAndAccountId(Long userId, Long accountId) {
        String userRole = userAccountRelationRepository.findByUserIdAndAccountId(userId, accountId)
                .orElseThrow(() -> new UserAccountRelationNotFoundException("잘못된 요청입니다."))
                .getUserRole()
                .name();

        return UserRoleResponseDTO.builder()
                .userRole(userRole)
                .message("성공")
                .build();
    }
}
