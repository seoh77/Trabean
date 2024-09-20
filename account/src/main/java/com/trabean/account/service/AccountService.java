package com.trabean.account.service;

import com.trabean.account.dto.request.AccountNoRequestDTO;
import com.trabean.account.dto.request.UserRoleRequestDTO;
import com.trabean.account.dto.response.AccountNoResponseDTO;
import com.trabean.account.dto.response.UserRoleResponseDTO;
import com.trabean.account.repository.AccountRepository;
import com.trabean.account.repository.UserAccountRelationRepository;
import com.trabean.exception.AccountNotFoundException;
import com.trabean.exception.UserAccountRelationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserAccountRelationRepository userAccountRelationRepository;

    public AccountNoResponseDTO getAccountNoById(AccountNoRequestDTO requestDTO) {
        Long accountId = requestDTO.getAccountId();

        String accountNo = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("해당 계좌를 찾을 수 없습니다."))
                .getAccountNo();

        return AccountNoResponseDTO.builder()
                .accountNo(accountNo)
                .message("성공")
                .build();
    }

    public UserRoleResponseDTO getUserRoleByUserIdAndAccountId(UserRoleRequestDTO requestDTO) {
        Long userId = requestDTO.getUserId();
        Long accountId = requestDTO.getAccountId();

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
