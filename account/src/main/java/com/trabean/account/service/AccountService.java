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
import com.trabean.util.RequestHeader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserAccountRelationRepository userAccountRelationRepository;

    private final DomesticClient domesticClient;

    // 개인 통장 상품 고유번호
    private final String PERSONAL_ACCOUNT_TYPE_UNIQUE_NO = "001-1-45f35a47cc6649";

    // 한화 여행통장 상품 고유번호
    private final String DOMESTIC_TRAVEL_ACCOUNT_TYPE_UNIQUE_NO = "001-1-2e58332128994c";

    // 개인 통장 생성 서비스 로직
    public CreatePersonalAccountResponseDTO createPersonalAccount(CreatePersonalAccountRequestDTO requestDTO) {
        String userKey = requestDTO.getUserKey();
        Long userId = requestDTO.getUserId();
        String password = requestDTO.getPassword();

        CreateDemandDepositAccountRequestDTO createDemandDepositAccountRequestDTO = CreateDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("createDemandDepositAccount")
                        .userKey(userKey)
                        .build())
                .accountTypeUniqueNo(PERSONAL_ACCOUNT_TYPE_UNIQUE_NO)
                .build();

        CreateDemandDepositAccountResponseDTO createDemandDepositAccountResponseDTO = domesticClient.createDemandDepositAccount(createDemandDepositAccountRequestDTO);

        String accountNo = createDemandDepositAccountResponseDTO.getRec().getAccountNo();

        Account account = Account.builder()
                .accountNo(accountNo)
                .password(password)
                .userId(userId)
                .build();

        Account savedAccount = accountRepository.save(account);

        UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                .userId(userId)
                .account(savedAccount)
                .userRole(UserAccountRelation.UserRole.ADMIN)
                .build();

        userAccountRelationRepository.save(userAccountRelation);

        return CreatePersonalAccountResponseDTO.builder()
                .message("개인 통장 생성 성공")
                .build();
    }

    // 한화 여행통장 생성 서비스 로직
    public CreateDomesticTravelAccountResponseDTO createDomesticTravelAccount(CreateDomesticTravelAccountRequestDTO requestDTO) {
        String userKey = requestDTO.getUserKey();
        Long userId = requestDTO.getUserId();
        String password = requestDTO.getPassword();

        CreateDemandDepositAccountRequestDTO createDemandDepositAccountRequestDTO = CreateDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("createDemandDepositAccount")
                        .userKey(userKey)
                        .build())
                .accountTypeUniqueNo(DOMESTIC_TRAVEL_ACCOUNT_TYPE_UNIQUE_NO)
                .build();

        CreateDemandDepositAccountResponseDTO createDemandDepositAccountResponseDTO = domesticClient.createDemandDepositAccount(createDemandDepositAccountRequestDTO);

        String accountNo = createDemandDepositAccountResponseDTO.getRec().getAccountNo();

        Account account = Account.builder()
                .accountNo(accountNo)
                .password(password)
                .userId(userId)
                .build();

        Account savedAccount = accountRepository.save(account);

        UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                .userId(userId)
                .account(savedAccount)
                .userRole(UserAccountRelation.UserRole.ADMIN)
                .build();

        userAccountRelationRepository.save(userAccountRelation);

        return CreateDomesticTravelAccountResponseDTO.builder()
                .message("한화 여행통장 생성 성공")
                .build();
    }

    // 통장 목록 조회 서비스 로직
    public AccountListResponseDTO getAccountList(AccountListRequestDTO requestDTO) {
        String userKey = requestDTO.getUserKey();

        InquireDemandDepositAccountListRequestDTO inquireDemandDepositAccountListRequestDTO = InquireDemandDepositAccountListRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("inquireDemandDepositAccountList")
                        .userKey(userKey)
                        .build())
                .build();

        InquireDemandDepositAccountListResponseDTO inquireDemandDepositAccountListResponseDTO = domesticClient.inquireDemandDepositAccountList(inquireDemandDepositAccountListRequestDTO);

        return convertInquireDemandDepositAccountListResponseDTOToAccountListResponseDTO(inquireDemandDepositAccountListResponseDTO);
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

        String userRole = userAccountRelationRepository.findByUserIdAndAccountId(userId, accountId)
                .orElseThrow(() -> new UserAccountRelationNotFoundException("잘못된 요청입니다."))
                .getUserRole()
                .name();

        return UserRoleResponseDTO.builder()
                .message("통장 권한 조회 성공")
                .userRole(userRole)
                .build();
    }

    // SSAFY API 통장 목록 조회 responseDTO -> Trabean 통장 목록 조회 responseDTO
    private AccountListResponseDTO convertInquireDemandDepositAccountListResponseDTOToAccountListResponseDTO(InquireDemandDepositAccountListResponseDTO inquireDemandDepositAccountListResponseDTO) {
        List<AccountListResponseDTO.Account> accountList = inquireDemandDepositAccountListResponseDTO.getRec().stream()
                .map(rec -> {
                    Long accountId = accountRepository.findByAccountNo(rec.getAccountNo())
                            .orElseThrow(() -> new IllegalArgumentException("걍 버그 : " + rec.getAccountNo()))
                            .getAccountId();

                    return AccountListResponseDTO.Account.builder()
                            .accountId(accountId)
                            .accountNo(rec.getAccountNo())
                            .accountName(rec.getAccountName())
                            .accountBalance(rec.getAccountBalance())
                            .build();
                })
                .collect(Collectors.toList());

        return AccountListResponseDTO.builder()
                .message("통장 목록 조회 성공")
                .accountList(accountList)
                .build();
    }

}
