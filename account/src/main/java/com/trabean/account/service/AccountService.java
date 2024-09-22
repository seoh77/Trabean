package com.trabean.account.service;

import com.trabean.account.domain.Account;
import com.trabean.account.domain.UserAccountRelation;
import com.trabean.account.dto.request.*;
import com.trabean.account.dto.response.*;
import com.trabean.account.repository.AccountRepository;
import com.trabean.account.repository.UserAccountRelationRepository;
import com.trabean.exception.AccountNotFoundException;
import com.trabean.exception.UserAccountRelationNotFoundException;
import com.trabean.ssafy.api.account.domestic.client.DomesticClient;
import com.trabean.ssafy.api.account.domestic.dto.requestDTO.CreateDemandDepositAccountRequestDTO;
import com.trabean.ssafy.api.account.domestic.dto.requestDTO.InquireDemandDepositAccountListRequestDTO;
import com.trabean.ssafy.api.account.domestic.dto.responseDTO.CreateDemandDepositAccountResponseDTO;
import com.trabean.ssafy.api.account.domestic.dto.responseDTO.InquireDemandDepositAccountListResponseDTO;
import com.trabean.ssafy.api.config.CustomFeignClientException;
import com.trabean.ssafy.api.response.code.ResponseCode;
import com.trabean.util.RequestHeader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.trabean.constant.Constants.DOMESTIC_TRAVEL_ACCOUNT_TYPE_UNIQUE_NO;
import static com.trabean.constant.Constants.PERSONAL_ACCOUNT_TYPE_UNIQUE_NO;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserAccountRelationRepository userAccountRelationRepository;

    private final DomesticClient domesticClient;

    // 개인 통장 생성 서비스 로직
    public CreatePersonalAccountResponseDTO createPersonalAccount(CreatePersonalAccountRequestDTO requestDTO) {
        String userKey = requestDTO.getUserKey();
        Long userId = requestDTO.getUserId();
        String password = requestDTO.getPassword();

        // SSAFY API 계좌 생성 요청
        CreateDemandDepositAccountRequestDTO createDemandDepositAccountRequestDTO = CreateDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("createDemandDepositAccount")
                        .userKey(userKey)
                        .build())
                .accountTypeUniqueNo(PERSONAL_ACCOUNT_TYPE_UNIQUE_NO)
                .build();

        ResponseCode responseCode;
        String responseMessage;

        try {
            CreateDemandDepositAccountResponseDTO createDemandDepositAccountResponseDTO = domesticClient.createDemandDepositAccount(createDemandDepositAccountRequestDTO);

            responseCode = createDemandDepositAccountResponseDTO.getHeader().getResponseCode();
            responseMessage = createDemandDepositAccountResponseDTO.getHeader().getResponseMessage();

            String accountNo = createDemandDepositAccountResponseDTO.getRec().getAccountNo();

            // Account 테이블에 저장
            Account account = Account.builder()
                    .accountNo(accountNo)
                    .password(password)
                    .userId(userId)
                    .build();

            Account savedAccount = accountRepository.save(account);

            // UserAccountRelation 테이블에 저장
            UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                    .userId(userId)
                    .account(savedAccount)
                    .userRole(UserAccountRelation.UserRole.ADMIN)
                    .build();

            userAccountRelationRepository.save(userAccountRelation);

        } catch (CustomFeignClientException e) {
            responseCode = e.getErrorResponse().getResponseCode();
            responseMessage = e.getErrorResponse().getResponseMessage();
        }
        return CreatePersonalAccountResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

    // 한화 여행통장 생성 서비스 로직
    public CreateDomesticTravelAccountResponseDTO createDomesticTravelAccount(CreateDomesticTravelAccountRequestDTO requestDTO) {
        String userKey = requestDTO.getUserKey();
        Long userId = requestDTO.getUserId();
        String password = requestDTO.getPassword();

        // SSAFY API 계좌 생성 요청
        CreateDemandDepositAccountRequestDTO createDemandDepositAccountRequestDTO = CreateDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("createDemandDepositAccount")
                        .userKey(userKey)
                        .build())
                .accountTypeUniqueNo(DOMESTIC_TRAVEL_ACCOUNT_TYPE_UNIQUE_NO)
                .build();

        ResponseCode responseCode;
        String responseMessage;

        try {
            CreateDemandDepositAccountResponseDTO createDemandDepositAccountResponseDTO = domesticClient.createDemandDepositAccount(createDemandDepositAccountRequestDTO);

            responseCode = createDemandDepositAccountResponseDTO.getHeader().getResponseCode();
            responseMessage = createDemandDepositAccountResponseDTO.getHeader().getResponseMessage();

            String accountNo = createDemandDepositAccountResponseDTO.getRec().getAccountNo();

            // Account 테이블에 저장
            Account account = Account.builder()
                    .accountNo(accountNo)
                    .password(password)
                    .userId(userId)
                    .build();

            Account savedAccount = accountRepository.save(account);

            // UserAccountRelation 테이블에 저장
            UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                    .userId(userId)
                    .account(savedAccount)
                    .userRole(UserAccountRelation.UserRole.ADMIN)
                    .build();

            userAccountRelationRepository.save(userAccountRelation);

        } catch (CustomFeignClientException e) {
            responseCode = e.getErrorResponse().getResponseCode();
            responseMessage = e.getErrorResponse().getResponseMessage();
        }
        return CreateDomesticTravelAccountResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

    // 통장 목록 조회 서비스 로직
    public AccountListResponseDTO getAccountList(AccountListRequestDTO requestDTO) {
        String userKey = requestDTO.getUserKey();

        // SSAFY API 계좌 목록 조회 요청
        InquireDemandDepositAccountListRequestDTO inquireDemandDepositAccountListRequestDTO = InquireDemandDepositAccountListRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("inquireDemandDepositAccountList")
                        .userKey(userKey)
                        .build())
                .build();

        ResponseCode responseCode;
        String responseMessage;
        List<AccountListResponseDTO.Account> accountList;

        try {
            InquireDemandDepositAccountListResponseDTO inquireDemandDepositAccountListResponseDTO = domesticClient.inquireDemandDepositAccountList(inquireDemandDepositAccountListRequestDTO);

            responseCode = inquireDemandDepositAccountListResponseDTO.getHeader().getResponseCode();
            responseMessage = inquireDemandDepositAccountListResponseDTO.getHeader().getResponseMessage();
            accountList = getAccountList(inquireDemandDepositAccountListResponseDTO);

        } catch (CustomFeignClientException e) {
            responseCode = e.getErrorResponse().getResponseCode();
            responseMessage = e.getErrorResponse().getResponseMessage();
            accountList = new ArrayList<>();
        }
        return AccountListResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .accountList(accountList)
                .build();
    }

    // 통장 계좌번호 조회 서비스 로직
    public AccountNoResponseDTO getAccountNoById(AccountNoRequestDTO requestDTO) {
        Long accountId = requestDTO.getAccountId();

        String accountNo = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("해당 계좌를 찾을 수 없습니다."))
                .getAccountNo();

        return AccountNoResponseDTO.builder()
                .message("통장 계좌번호 조회 성공")
                .accountNo(accountNo)
                .build();
    }

    // 통장 권한 조회 서비스 로직
    public UserRoleResponseDTO getUserRoleByUserIdAndAccountId(UserRoleRequestDTO requestDTO) {
        Long userId = requestDTO.getUserId();
        Long accountId = requestDTO.getAccountId();

        UserAccountRelation.UserRole userRole = userAccountRelationRepository.findByUserIdAndAccountId(userId, accountId)
                .orElseThrow(() -> new UserAccountRelationNotFoundException("잘못된 요청입니다."))
                .getUserRole();

        return UserRoleResponseDTO.builder()
                .message("통장 권한 조회 성공")
                .userRole(userRole)
                .build();
    }

    // SSAFY API 통장 목록 조회 responseDTO -> 통장 목록 리스트
    private List<AccountListResponseDTO.Account> getAccountList(InquireDemandDepositAccountListResponseDTO inquireDemandDepositAccountListResponseDTO) {

        return inquireDemandDepositAccountListResponseDTO.getRec().stream()
                .map(rec -> {
                    Long accountId = accountRepository.findByAccountNo(rec.getAccountNo())
                            .orElseThrow(() -> new AccountNotFoundException("해당 계좌를 찾을 수 없습니다."))
                            .getAccountId();

                    return AccountListResponseDTO.Account.builder()
                            .accountId(accountId)
                            .bankCode(rec.getBankCode())
                            .bankName(rec.getBankName())
                            .accountNo(rec.getAccountNo())
                            .accountBalance(rec.getAccountBalance())
                            .build();
                })
                .collect(Collectors.toList());
    }

}
