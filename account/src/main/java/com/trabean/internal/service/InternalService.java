package com.trabean.internal.service;

import com.trabean.account.domain.UserAccountRelation;
import com.trabean.account.dto.response.AccountNoResponseDTO;
import com.trabean.account.repository.UserAccountRelationRepository;
import com.trabean.exception.*;
import com.trabean.external.msa.user.client.UserClient;
import com.trabean.external.msa.user.dto.request.UserKeyRequestDTO;
import com.trabean.external.msa.user.dto.response.UserKeyResponseDTO;
import com.trabean.internal.dto.requestDTO.*;
import com.trabean.internal.dto.responseDTO.AdminUserKeyResponseDTO;
import com.trabean.internal.dto.responseDTO.UpdateUserRoleResponseDTO;
import com.trabean.internal.dto.responseDTO.UserRoleResponseDTO;
import com.trabean.account.repository.AccountRepository;
import com.trabean.internal.dto.responseDTO.VerifyPasswordResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        UserAccountRelation.UserRole userRole = userAccountRelationRepository.findUserRoleByUserIdAndAccountId(requestDTO.getUserId(), requestDTO.getAccountId())
                .orElseThrow(UserAccountRelationNotFoundException::getInstance)
                .getUserRole();

        return UserRoleResponseDTO.builder()
                .message("통장 권한 조회 성공")
                .userRole(userRole)
                .build();
    }

    // 여행통장 결제 권한 변경 서비스 로직
    public UpdateUserRoleResponseDTO updateUserRole(UpdateUserRoleRequestDTO requestDTO) {
        int count = 0;

        userAccountRelationRepository.updateUserRoleByUserIdAndAccountId(requestDTO.getUserId(), requestDTO.getDomesticAccountId(), requestDTO.getUserRole());
        count++;

        for(Long foreignAccountId : requestDTO.getForeignAccountIdList()){
            userAccountRelationRepository.updateUserRoleByUserIdAndAccountId(requestDTO.getUserId(), foreignAccountId, requestDTO.getUserRole());
            count++;
        }

        return UpdateUserRoleResponseDTO.builder()
                .message("여행통장 결제 권한 변경 성공 : " + count + "개 바뀜")
                .build();
    }

    // 결제 비밀번호 검증 서비스 로직
    public VerifyPasswordResponseDTO verifyPassword(VerifyPasswordRequestDTO requestDTO) {

        UserAccountRelation.UserRole userRole = userAccountRelationRepository.findUserRoleByUserIdAndAccountId(requestDTO.getUserId(), requestDTO.getAccountId())
                .orElseThrow(UserAccountRelationNotFoundException::getInstance)
                .getUserRole();

        if(userRole == UserAccountRelation.UserRole.NONE_PAYER) {
            throw UnauthorizedUserRoleException.getInstance();
        }

        String savedPassword = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(AccountNotFoundException::getInstance)
                .getPassword();

        if(passwordEncoder.matches(requestDTO.getPassword() + PEPPER, savedPassword)){
            return VerifyPasswordResponseDTO.builder()
                    .message("결제 비밀번호 검증 성공")
                    .build();
        }
        else{
            throw InvalidPasswordException.getInstance();
        }
    }

    // 통장 주인의 userKey 조회 서비스 로직
    public AdminUserKeyResponseDTO getAdminUserKey(AdminUserKeyRequestDTO requestDTO) {

        List<UserAccountRelation> userAccountRelations = userAccountRelationRepository.findAllByAccountId(requestDTO.getAccountId());

        Long userId = userAccountRelations.stream()
                .filter(relation -> relation.getUserRole() == UserAccountRelation.UserRole.ADMIN)
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
