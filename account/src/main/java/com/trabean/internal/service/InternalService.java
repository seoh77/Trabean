package com.trabean.internal.service;

import com.trabean.account.domain.UserAccountRelation;
import com.trabean.account.dto.response.AccountNoResponseDTO;
import com.trabean.account.repository.UserAccountRelationRepository;
import com.trabean.exception.*;
import com.trabean.internal.dto.requestDTO.UserRoleRequestDTO;
import com.trabean.internal.dto.requestDTO.VerifyPasswordRequestDTO;
import com.trabean.internal.dto.responseDTO.UserRoleResponseDTO;
import com.trabean.account.repository.AccountRepository;
import com.trabean.internal.dto.requestDTO.AccountNoRequestDTO;
import com.trabean.internal.dto.responseDTO.VerifyPasswordResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.trabean.constant.Constant.PEPPER;

@Service
@Transactional
@RequiredArgsConstructor
public class InternalService {

    private final AccountRepository accountRepository;
    private final UserAccountRelationRepository userAccountRelationRepository;

    private final PasswordEncoder passwordEncoder;

    // 통장 계좌번호 조회 서비스 로직
    public AccountNoResponseDTO getAccountNo(AccountNoRequestDTO requestDTO) {

        String accountNo = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(AccountNotFoundException::getInstance)
                .getAccountNo();

        return AccountNoResponseDTO.builder()
                .message("통장 계좌번호 조회 성공")
                .accountNo(accountNo)
                .build();
    }

    // 통장 권한 조회 서비스 로직
    public UserRoleResponseDTO getUserRole(UserRoleRequestDTO requestDTO) {

        UserAccountRelation.UserRole userRole = userAccountRelationRepository.findByUserIdAndAccountId(requestDTO.getUserId(), requestDTO.getAccountId())
                .orElseThrow(UserAccountRelationNotFoundException::getInstance)
                .getUserRole();

        return UserRoleResponseDTO.builder()
                .message("통장 권한 조회 성공")
                .userRole(userRole)
                .build();
    }

    // 결제 비밀번호 검증 서비스 로직
    public VerifyPasswordResponseDTO verifyPassword(VerifyPasswordRequestDTO requestDTO) {

        UserAccountRelation.UserRole userRole = userAccountRelationRepository.findByUserIdAndAccountId(requestDTO.getUserId(), requestDTO.getAccountId())
                .orElseThrow(UserAccountRelationNotFoundException::getInstance)
                .getUserRole();

        if(userRole == UserAccountRelation.UserRole.NONE_PAYER) {
            throw UnauthorizedUserRoleException.getInstance();
        }

        String savedPassword = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(AccountNotFoundException::getInstance)
                .getPassword();

        String password = requestDTO.getPassword();

        if(passwordEncoder.matches(password + PEPPER, savedPassword)){
            return VerifyPasswordResponseDTO.builder()
                    .message("결제 비밀번호 검증 성공")
                    .build();
        }
        else{
            throw InvalidPasswordException.getInstance();
        }
    }

}
