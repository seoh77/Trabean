package com.trabean.internal.service;

import com.trabean.account.domain.Account;
import com.trabean.account.domain.UserAccountRelation;
import com.trabean.account.domain.UserAccountRelation.UserRole;
import com.trabean.account.repository.UserAccountRelationRepository;
import com.trabean.common.InternalServerSuccessResponseDTO;
import com.trabean.exception.*;
import com.trabean.external.msa.user.client.UserClient;
import com.trabean.external.msa.user.dto.request.UserKeyRequestDTO;
import com.trabean.external.msa.user.dto.response.UserKeyResponseDTO;
import com.trabean.internal.dto.requestDTO.*;
import com.trabean.internal.dto.responseDTO.*;
import com.trabean.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.trabean.constant.Constant.PEPPER;

@Service
@Transactional
@RequiredArgsConstructor
public class InternalService {

    private final AccountRepository accountRepository;
    private final UserAccountRelationRepository userAccountRelationRepository;

    private final UserClient userClient;

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

        UserRole userRole = userAccountRelationRepository.findByUserIdAndAccountId(requestDTO.getUserId(), requestDTO.getAccountId())
                .orElseThrow(UserAccountRelationNotFoundException::getInstance)
                .getUserRole();

        return UserRoleResponseDTO.builder()
                .message("통장 권한 조회 성공")
                .userRole(userRole)
                .build();
    }

    // 여행통장 결제 권한 변경 서비스 로직
    public InternalServerSuccessResponseDTO updateUserRole(UpdateUserRoleRequestDTO requestDTO) {

        userAccountRelationRepository.updateUserRoleByUserIdAndAccountId(requestDTO.getUserId(), requestDTO.getDomesticAccountId(), requestDTO.getUserRole());

        for(Long foreignAccountId : requestDTO.getForeignAccountIdList()){
            userAccountRelationRepository.updateUserRoleByUserIdAndAccountId(requestDTO.getUserId(), foreignAccountId, requestDTO.getUserRole());
        }

        return InternalServerSuccessResponseDTO.builder()
                .message("여행통장 결제 권한 변경 성공")
                .build();
    }

    // 결제 비밀번호 검증 서비스 로직
    public InternalServerSuccessResponseDTO verifyPassword(VerifyPasswordRequestDTO requestDTO) {

        UserRole userRole = userAccountRelationRepository.findByUserIdAndAccountId(requestDTO.getUserId(), requestDTO.getAccountId())
                .orElseThrow(UserAccountRelationNotFoundException::getInstance)
                .getUserRole();

        if(userRole == UserRole.NONE_PAYER) {
            throw UnauthorizedUserRoleException.getInstance();
        }

        String savedPassword = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(AccountNotFoundException::getInstance)
                .getPassword();

        if(passwordEncoder.matches(requestDTO.getPassword() + PEPPER, savedPassword)){
            return InternalServerSuccessResponseDTO.builder()
                    .message("결제 비밀번호 검증 성공")
                    .build();
        }
        else{
            throw InvalidPasswordException.getInstance();
        }
    }

    // 여행통장 가입 서비스 로직
    public InternalServerSuccessResponseDTO joinTravelAccount(JoinTravelAccountRequestDTO requestDTO) {

        // 한화 여행통장에 가입
        UserAccountRelation domesticUserAccountRelation = UserAccountRelation.builder()
                .userId(requestDTO.getUserId())
                .account(Account.builder()
                        .accountId(requestDTO.getDomesticAccountId())
                        .build())
                .userRole(UserRole.NONE_PAYER)
                .build();

        userAccountRelationRepository.save(domesticUserAccountRelation);

        // 한화 여행통장에 연결된 외화 여행통장에 가입
        for(Long foreignAccountId : requestDTO.getForeignAccountIdList()) {
            UserAccountRelation foreignUserAccountRelation = UserAccountRelation.builder()
                    .userId(requestDTO.getUserId())
                    .account(Account.builder()
                            .accountId(foreignAccountId)
                            .build())
                    .userRole(UserRole.NONE_PAYER)
                    .build();

            userAccountRelationRepository.save(foreignUserAccountRelation);
        }

        return InternalServerSuccessResponseDTO.builder()
                .message("여행통장 가입 성공")
                .build();
    }

    // 통장 주인의 userKey 조회 서비스 로직
    public AdminUserKeyResponseDTO getAdminUserKey(AdminUserKeyRequestDTO requestDTO) {

        List<UserAccountRelation> userAccountRelations = userAccountRelationRepository.findAllByAccountId(requestDTO.getAccountId())
                .orElse(new ArrayList<>());

        Long userId = userAccountRelations.stream()
                .filter(relation -> relation.getUserRole() == UserRole.ADMIN)
                .map(UserAccountRelation::getUserId)
                .findFirst()
                .orElseThrow(UserAccountRelationNotFoundException::getInstance);

        // User 서버에 userKey 조회 요청
        UserKeyRequestDTO userKeyRequestDTO = UserKeyRequestDTO.builder()
                .userId(userId)
                .build();

        UserKeyResponseDTO userKeyResponseDTO = userClient.getUserKey(userKeyRequestDTO);

        String userKey = userKeyResponseDTO.getUserKey();

        return AdminUserKeyResponseDTO.builder()
                .userKey(userKey)
                .build();
    }

}
