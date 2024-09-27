package com.trabean.account.service;

import com.trabean.account.domain.Account;
import com.trabean.account.domain.UserAccountRelation;
import com.trabean.account.dto.request.*;
import com.trabean.account.dto.response.*;
import com.trabean.account.dto.response.DomesticTravelAccountMemberResponseDTO.Member;
import com.trabean.account.repository.AccountRepository;
import com.trabean.account.repository.UserAccountRelationRepository;
import com.trabean.common.ResponseCode;
import com.trabean.exception.AccountNotFoundException;
import com.trabean.exception.InvalidPasswordException;
import com.trabean.exception.UserAccountRelationNotFoundException;
import com.trabean.external.msa.travel.client.TravelClient;
import com.trabean.external.msa.travel.client.UserClient;
import com.trabean.external.msa.travel.dto.requestDTO.SaveDomesticTravelAccountRequestDTO;
import com.trabean.external.ssafy.domestic.client.DomesticClient;
import com.trabean.external.ssafy.domestic.dto.requestDTO.CreateDemandDepositAccountRequestDTO;
import com.trabean.external.ssafy.domestic.dto.responseDTO.CreateDemandDepositAccountResponseDTO;
import com.trabean.internal.dto.responseDTO.UserRoleResponseDTO;
import com.trabean.util.RequestHeader;
import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.trabean.constant.Constant.*;

@Service
//@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserAccountRelationRepository userAccountRelationRepository;

    private final DomesticClient domesticClient;

    private final TravelClient travelClient;

    private final PasswordEncoder passwordEncoder;
    
    private final UserClient userClient;

    // 한화 여행 통장 멤버 목록 + 권한 반환 서비스 코드 : 민채
    public DomesticTravelAccountMemberResponseDTO getDomesticTravelMemberList(Long accountId) {
        List<UserAccountRelation> members = userAccountRelationRepository.findAllByAccountId(accountId);
        List<Member> responseMembers = new ArrayList<>();
        for (UserAccountRelation member : members) {
            Map<String, String> temp = userClient.getUserName(member.getUserId());
            responseMembers.add(Member.builder().userId(member.getUserId()).userName(temp.get("userName")).role(member.getUserRole()).build());
        }
        return DomesticTravelAccountMemberResponseDTO.builder().memberCount(members.size()).members(responseMembers).build();
    }



    // 개인 통장 생성 서비스 로직
    public CreatePersonalAccountResponseDTO createPersonalAccount(Long userId, String userKey, CreatePersonalAccountRequestDTO requestDTO) {

        // SSAFY API 계좌 생성 요청
        CreateDemandDepositAccountRequestDTO createDemandDepositAccountRequestDTO = CreateDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("createDemandDepositAccount")
                        .userKey(userKey)
                        .build())
                .accountTypeUniqueNo(PERSONAL_ACCOUNT_TYPE_UNIQUE_NO)
                .build();

        CreateDemandDepositAccountResponseDTO createDemandDepositAccountResponseDTO = domesticClient.createDemandDepositAccount(createDemandDepositAccountRequestDTO);

        String accountNo = createDemandDepositAccountResponseDTO.getRec().getAccountNo();
        String hashedPassword = passwordEncoder.encode(requestDTO.getPassword() + PEPPER);

        // Account 테이블에 저장
        Account account = Account.builder()
                .accountNo(accountNo)
                .password(hashedPassword)
                .accountType(Account.AccountType.PERSONAL)
                .build();

        Account savedAccount = accountRepository.save(account);

        // UserAccountRelation 테이블에 저장
        UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                .userId(userId)
                .account(savedAccount)
                .userRole(UserAccountRelation.UserRole.ADMIN)
                .build();

        userAccountRelationRepository.save(userAccountRelation);

        ResponseCode responseCode = createDemandDepositAccountResponseDTO.getHeader().getResponseCode();
        String responseMessage = createDemandDepositAccountResponseDTO.getHeader().getResponseMessage();

        return CreatePersonalAccountResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

    // 한화 여행통장 생성 서비스 로직
    public CreateDomesticTravelAccountResponseDTO createDomesticTravelAccount(Long userId, String userKey, CreateDomesticTravelAccountRequestDTO requestDTO) {

        // SSAFY API 계좌 생성 요청
        CreateDemandDepositAccountRequestDTO createDemandDepositAccountRequestDTO = CreateDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("createDemandDepositAccount")
                        .userKey(userKey)
                        .build())
                .accountTypeUniqueNo(DOMESTIC_TRAVEL_ACCOUNT_TYPE_UNIQUE_NO)
                .build();

        CreateDemandDepositAccountResponseDTO createDemandDepositAccountResponseDTO = domesticClient.createDemandDepositAccount(createDemandDepositAccountRequestDTO);

        String accountNo = createDemandDepositAccountResponseDTO.getRec().getAccountNo();
        String hashedPassword = passwordEncoder.encode(requestDTO.getPassword() + PEPPER);

        // Account 테이블에 저장
        Account account = Account.builder()
                .accountNo(accountNo)
                .password(hashedPassword)
                .accountType(Account.AccountType.DOMESTIC)
                .build();

        Account savedAccount = accountRepository.save(account);

        // UserAccountRelation 테이블에 저장
        UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                .userId(userId)
                .account(savedAccount)
                .userRole(UserAccountRelation.UserRole.ADMIN)
                .build();

        userAccountRelationRepository.save(userAccountRelation);

        // Travel 서버에 여행통장 생성 요청
        SaveDomesticTravelAccountRequestDTO saveDomesticTravelAccountRequestDTO = SaveDomesticTravelAccountRequestDTO.builder()
                .accountId(savedAccount.getAccountId())
                .accountName(requestDTO.getAccountName())
                .targetAmount(requestDTO.getTargetAmount())
                .build();

        travelClient.saveDomesticTravelAccount(saveDomesticTravelAccountRequestDTO);

        ResponseCode responseCode = createDemandDepositAccountResponseDTO.getHeader().getResponseCode();
        String responseMessage = createDemandDepositAccountResponseDTO.getHeader().getResponseMessage();

        return CreateDomesticTravelAccountResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

//    // 통장 목록 조회 서비스 로직
//    public AccountListResponseDTO getAccountList(AccountListRequestDTO requestDTO) {
//        String userKey = requestDTO.getUserKey();
//
//        // SSAFY API 계좌 목록 조회 요청
//        InquireDemandDepositAccountListRequestDTO inquireDemandDepositAccountListRequestDTO = InquireDemandDepositAccountListRequestDTO.builder()
//                .header(RequestHeader.builder()
//                        .apiName("inquireDemandDepositAccountList")
//                        .userKey(userKey)
//                        .build())
//                .build();
//
//        ResponseCode responseCode;
//        String responseMessage;
//        List<AccountListResponseDTO.Account> accountList;
//
//        try {
//            InquireDemandDepositAccountListResponseDTO inquireDemandDepositAccountListResponseDTO = domesticClient.inquireDemandDepositAccountList(inquireDemandDepositAccountListRequestDTO);
//
//            responseCode = inquireDemandDepositAccountListResponseDTO.getHeader().getResponseCode();
//            responseMessage = inquireDemandDepositAccountListResponseDTO.getHeader().getResponseMessage();
//            accountList = getAccountList(inquireDemandDepositAccountListResponseDTO);
//
//        } catch (CustomFeignClientException e) {
//            responseCode = e.getErrorResponse().getResponseCode();
//            responseMessage = e.getErrorResponse().getResponseMessage();
//            accountList = new ArrayList<>();
//        }
//        return AccountListResponseDTO.builder()
//                .responseCode(responseCode)
//                .responseMessage(responseMessage)
//                .accountList(accountList)
//                .build();
//    }

//    // 개인 통장 상세 조회 서비스 로직
//    public PersonalAccountDetailResponseDTO getAccountDetail(Long userId, String userKey, Long accountId, String startDate, String endDate, String transactionType) {
//
//        Account account = accountRepository.findById(accountId).orElseThrow();
//
//        // SSAFY 계좌 조회 (단건) 요청
//        InquireDemandDepositAccountRequestDTO inquireDemandDepositAccountRequestDTO = InquireDemandDepositAccountRequestDTO.builder()
//                .header(RequestHeader.builder()
//                        .apiName("inquireDemandDepositAccount")
//                        .userKey(userKey)
//                        .build())
//                .accountNo(accountNo)
//                .build();
//
//        try {
//            InquireDemandDepositAccountResponseDTO inquireDemandDepositAccountResponseDTO = domesticClient.inquireDemandDepositAccount(inquireDemandDepositAccountRequestDTO);
//
//            String bankName = inquireDemandDepositAccountResponseDTO.getRec().getBankName();
//            Long accountBalance = inquireDemandDepositAccountResponseDTO.getRec().getAccountBalance();
//
//            // SSAFY 계좌 거래 내역 조회 요청
//            InquireTransactionHistoryListRequestDTO inquireTransactionHistoryListRequestDTO = InquireTransactionHistoryListRequestDTO.builder()
//                    .header(RequestHeader.builder()
//                            .apiName("inquireTransactionHistoryList")
//                            .userKey(userKey)
//                            .build())
//                    .accountNo(accountNo)
//                    .startDate(startDate)
//                    .endDate(endDate)
//                    .transactionType(transactionType)
//                    .orderByType("DESC")
//                    .build();
//
//            InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO = domesticClient.inquireTransactionHistoryList(inquireTransactionHistoryListRequestDTO);
//
//            ResponseCode responseCode = inquireTransactionHistoryListResponseDTO.getHeader().getResponseCode();
//            String responseMessage = inquireTransactionHistoryListResponseDTO.getHeader().getResponseMessage();
//            List<PersonalAccountDetailResponseDTO.Transaction> transactionList = getTransactionList(inquireTransactionHistoryListResponseDTO);
//
//            return PersonalAccountDetailResponseDTO.builder()
//                    .responseCode(responseCode)
//                    .responseMessage(responseMessage)
//                    .bankName(bankName)
//                    .accountBalance(accountBalance)
//                    .transactionList(transactionList)
//                    .build();
//        } catch (CustomFeignClientException e) {
//            ResponseCode responseCode = e.getErrorResponse().getResponseCode();
//            String responseMessage = e.getErrorResponse().getResponseMessage();
//
//            return PersonalAccountDetailResponseDTO.builder()
//                    .responseCode(responseCode)
//                    .responseMessage(responseMessage)
//                    .build();
//        }
//    }

//    // 한화 여행통장 상세 조회 서비스 로직
//    public DomesticTravelAccountDetailResponseDTO getDomesticTravelAccountDetail(DomesticTravelAccountDetailRequestDTO requestDTO, String startDate, String endDate, String transactionType) {
//        String userKey = requestDTO.getUserKey();
//        String accountNo = requestDTO.getAccountNo();
//
//        // SSAFY 계좌 조회 (단건) 요청
//        InquireDemandDepositAccountRequestDTO inquireDemandDepositAccountRequestDTO = InquireDemandDepositAccountRequestDTO.builder()
//                .header(RequestHeader.builder()
//                        .apiName("inquireDemandDepositAccount")
//                        .userKey(userKey)
//                        .build())
//                .accountNo(accountNo)
//                .build();
//
//        try {
//            InquireDemandDepositAccountResponseDTO inquireDemandDepositAccountResponseDTO = domesticClient.inquireDemandDepositAccount(inquireDemandDepositAccountRequestDTO);
//
//            String bankName = inquireDemandDepositAccountResponseDTO.getRec().getBankName();
//            Long accountBalance = inquireDemandDepositAccountResponseDTO.getRec().getAccountBalance();
//
//            // SSAFY 계좌 거래 내역 조회 요청
//            InquireTransactionHistoryListRequestDTO inquireTransactionHistoryListRequestDTO = InquireTransactionHistoryListRequestDTO.builder()
//                    .header(RequestHeader.builder()
//                            .apiName("inquireTransactionHistoryList")
//                            .userKey(userKey)
//                            .build())
//                    .accountNo(accountNo)
//                    .startDate(startDate)
//                    .endDate(endDate)
//                    .transactionType(transactionType)
//                    .orderByType("DESC")
//                    .build();
//
//            InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO = domesticClient.inquireTransactionHistoryList(inquireTransactionHistoryListRequestDTO);
//
//            ResponseCode responseCode = inquireTransactionHistoryListResponseDTO.getHeader().getResponseCode();
//            String responseMessage = inquireTransactionHistoryListResponseDTO.getHeader().getResponseMessage();
//            List<DomesticTravelAccountDetailResponseDTO.Transaction> transactionList = getDomesticTravelAccountTransactionList(inquireTransactionHistoryListResponseDTO);
//
//            return DomesticTravelAccountDetailResponseDTO.builder()
//                    .responseCode(responseCode)
//                    .responseMessage(responseMessage)
//                    .bankName(bankName)
//                    .accountBalance(accountBalance)
//                    .transactionList(transactionList)
//                    .build();
//        } catch (CustomFeignClientException e) {
//            ResponseCode responseCode = e.getErrorResponse().getResponseCode();
//            String responseMessage = e.getErrorResponse().getResponseMessage();
//
//            return DomesticTravelAccountDetailResponseDTO.builder()
//                    .responseCode(responseCode)
//                    .responseMessage(responseMessage)
//                    .build();
//        }
//    }


    // 통장 권한 변경 서비스 로직
    public UpdateUserRoleResponseDTO updateUserRole(UpdateUserRoleRequestDTO requestDTO) {
        Long userId = requestDTO.getUserId();
        Long accountId = requestDTO.getAccountId();
        UserAccountRelation.UserRole userRole = requestDTO.getUserRole();

        int updatedTuples = userAccountRelationRepository.updateUserRoleByUserIdAndAccountId(userId, accountId, userRole);

        if(updatedTuples == 1){
            return UpdateUserRoleResponseDTO.builder()
                    .message("통장 권한 변경 성공")
                    .build();
        }
        else{
            throw UserAccountRelationNotFoundException.getInstance();
        }
    }

//    // SSAFY API 통장 목록 조회 responseDTO -> 통장 목록 리스트
//    private List<AccountListResponseDTO.Account> getAccountList(InquireDemandDepositAccountListResponseDTO inquireDemandDepositAccountListResponseDTO) {
//        return inquireDemandDepositAccountListResponseDTO.getRec().stream()
//                .map(rec -> AccountListResponseDTO.Account.builder()
//                        .bankName(rec.getBankName())
//                        .accountNo(rec.getAccountNo())
//                        .accountBalance(rec.getAccountBalance())
//                        .build())
//                .collect(Collectors.toList());
//    }

//    // SSAFY API 계좌 거래 내역 조회 responseDTO -> 거래 내역 리스트
//    private List<PersonalAccountDetailResponseDTO.Transaction> getTransactionList(InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO) {
//        return inquireTransactionHistoryListResponseDTO.getRec().getList().stream()
//                .map(item -> PersonalAccountDetailResponseDTO.Transaction.builder()
//                        .transactionType(item.getTransactionType())
//                        .transactionSummary(item.getTransactionSummary())
//                        .transactionDate(item.getTransactionDate())
//                        .transactionTime(item.getTransactionTime())
//                        .transactionBalance(item.getTransactionBalance())
//                        .transactionAfterBalance(item.getTransactionAfterBalance())
//                        .build())
//                .collect(Collectors.toList());
//    }

//    // SSAFY API 계좌 거래 내역 조회 responseDTO -> 거래 내역 리스트
//    private List<DomesticTravelAccountDetailResponseDTO.Transaction> getDomesticTravelAccountTransactionList(InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO) {
//        return inquireTransactionHistoryListResponseDTO.getRec().getList().stream()
//                .map(item -> DomesticTravelAccountDetailResponseDTO.Transaction.builder()
//                        .transactionType(item.getTransactionType())
//                        .transactionSummary(item.getTransactionSummary())
//                        .transactionDate(item.getTransactionDate())
//                        .transactionTime(item.getTransactionTime())
//                        .transactionBalance(item.getTransactionBalance())
//                        .transactionAfterBalance(item.getTransactionAfterBalance())
//                        .build())
//                .collect(Collectors.toList());
//    }

}
