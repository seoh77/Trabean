package com.trabean.account.service;

import com.trabean.account.domain.UserAccountRelation;
import com.trabean.account.repository.UserAccountRelationRepository;
import com.trabean.exception.custom.UserAccountRelationNotFoundException;
import com.trabean.external.msa.user.client.UserClient;
import com.trabean.external.msa.user.dto.request.UserKeyRequestDTO;
import com.trabean.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.trabean.account.domain.UserAccountRelation.UserRole.ADMIN;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountHelperService {

    private final UserAccountRelationRepository userAccountRelationRepository;

    private final UserClient userClient;

    // 통장 주인의 userKey 조회 메서드
    public String getAdminUserKeyByAccountId(Long accountId) {
        List<UserAccountRelation> userAccountRelationList = ValidationUtil.validateUserAccountRelationList(userAccountRelationRepository.findAllByAccountId(accountId));

        Long userId = userAccountRelationList.stream()
                .filter(relation -> relation.getUserRole() == ADMIN)
                .map(UserAccountRelation::getUserId)
                .findFirst()
                .orElseThrow(UserAccountRelationNotFoundException::getInstance);

        // User 서버에 userKey 조회 요청
        UserKeyRequestDTO userKeyRequestDTO = UserKeyRequestDTO.builder()
                .userId(userId)
                .build();
        return userClient.getUserKey(userKeyRequestDTO).getUserKey();
    }

    // 개인 통장 or 한화 여행통장에 가입된 userId 조회 메서드
    public List<Long> getAccountMemberIdList(Long accountId) {
        List<UserAccountRelation> userAccountRelationList = ValidationUtil.validateUserAccountRelationList(userAccountRelationRepository.findAllByAccountId(accountId));
        List<Long> userIdList = new ArrayList<>();

        for (UserAccountRelation userAccountRelation : userAccountRelationList) {
            userIdList.add(userAccountRelation.getUserId());
        }

        return userIdList;
    }

}
