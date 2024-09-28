package com.trabean.account.service;

import com.trabean.account.domain.Account;
import com.trabean.account.domain.Account.AccountType;
import com.trabean.account.domain.UserAccountRelation;
import com.trabean.account.domain.UserAccountRelation.UserRole;
import com.trabean.account.dto.request.*;
import com.trabean.account.dto.response.DomesticTravelAccountDetailResponseDTO;
import com.trabean.account.dto.response.DomesticTravelAccountMemberListResponseDTO;
import com.trabean.account.dto.response.DomesticTravelAccountMemberListResponseDTO.Member;
import com.trabean.account.dto.response.PersonalAccountDetailResponseDTO;
import com.trabean.account.repository.AccountRepository;
import com.trabean.account.repository.UserAccountRelationRepository;
import com.trabean.common.InternalServerSuccessResponseDTO;
import com.trabean.common.ResponseCode;
import com.trabean.common.SsafySuccessResponseDTO;
import com.trabean.exception.*;
import com.trabean.external.msa.travel.client.TravelClient;
import com.trabean.external.msa.travel.dto.requestDTO.SaveDomesticTravelAccountRequestDTO;
import com.trabean.external.msa.travel.dto.requestDTO.SaveForeignTravelAccountRequestDTO;
import com.trabean.external.msa.travel.dto.responseDTO.DomesticTravelAccountInfoResponseDTO;
import com.trabean.external.msa.user.client.UserClient;
import com.trabean.external.msa.user.dto.response.UserNameResponseDTO;
import com.trabean.external.ssafy.domestic.client.DomesticClient;
import com.trabean.external.ssafy.domestic.dto.requestDTO.CreateDemandDepositAccountRequestDTO;
import com.trabean.external.ssafy.domestic.dto.requestDTO.InquireDemandDepositAccountRequestDTO;
import com.trabean.external.ssafy.domestic.dto.requestDTO.InquireTransactionHistoryListRequestDTO;
import com.trabean.external.ssafy.domestic.dto.requestDTO.UpdateDemandDepositAccountTransferRequestDTO;
import com.trabean.external.ssafy.domestic.dto.responseDTO.CreateDemandDepositAccountResponseDTO;
import com.trabean.external.ssafy.domestic.dto.responseDTO.InquireDemandDepositAccountResponseDTO;
import com.trabean.external.ssafy.domestic.dto.responseDTO.InquireTransactionHistoryListResponseDTO;
import com.trabean.external.ssafy.domestic.dto.responseDTO.UpdateDemandDepositAccountTransferResponseDTO;
import com.trabean.external.ssafy.foriegn.client.ForeignClient;
import com.trabean.external.ssafy.foriegn.dto.requestDTO.CreateForeignCurrencyDemandDepositAccountRequestDTO;
import com.trabean.external.ssafy.foriegn.dto.responseDTO.CreateForeignCurrencyDemandDepositAccountResponseDTO;
import com.trabean.util.RequestHeader;
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
    private final ForeignClient foreignClient;

    private final UserClient userClient;
    private final TravelClient travelClient;

    private final PasswordEncoder passwordEncoder;

    // 개인 통장 생성 서비스 로직
    public SsafySuccessResponseDTO createPersonalAccount(Long userId, String userKey, CreatePersonalAccountRequestDTO requestDTO) {

        // SSAFY 금융 API 계좌 생성 요청
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
                .accountType(AccountType.PERSONAL)
                .build();

        Account savedAccount = accountRepository.save(account);

        // UserAccountRelation 테이블에 저장
        UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                .userId(userId)
                .account(savedAccount)
                .userRole(UserRole.ADMIN)
                .build();

        userAccountRelationRepository.save(userAccountRelation);

        ResponseCode responseCode = createDemandDepositAccountResponseDTO.getHeader().getResponseCode();
        String responseMessage = createDemandDepositAccountResponseDTO.getHeader().getResponseMessage();

        return SsafySuccessResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

    // 한화 여행통장 생성 서비스 로직
    public SsafySuccessResponseDTO createDomesticTravelAccount(Long userId, String userKey, CreateDomesticTravelAccountRequestDTO requestDTO) {

        // SSAFY 금융 API 계좌 생성 요청
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
                .accountType(AccountType.DOMESTIC)
                .build();

        Account savedAccount = accountRepository.save(account);

        // UserAccountRelation 테이블에 저장
        UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                .userId(userId)
                .account(savedAccount)
                .userRole(UserRole.ADMIN)
                .build();

        userAccountRelationRepository.save(userAccountRelation);

        // Travel 서버에 원화계좌 생성시 원화여행계좌 테이블에 정보 저장 요청
        SaveDomesticTravelAccountRequestDTO saveDomesticTravelAccountRequestDTO = SaveDomesticTravelAccountRequestDTO.builder()
                .accountId(savedAccount.getAccountId())
                .accountName(requestDTO.getAccountName())
                .targetAmount(requestDTO.getTargetAmount())
                .build();

        travelClient.saveDomesticTravelAccount(saveDomesticTravelAccountRequestDTO);

        ResponseCode responseCode = createDemandDepositAccountResponseDTO.getHeader().getResponseCode();
        String responseMessage = createDemandDepositAccountResponseDTO.getHeader().getResponseMessage();

        return SsafySuccessResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

    // 외화 여행통장 생성 서비스 로직
    public SsafySuccessResponseDTO createForeignTravelAccount(Long userId, String userKey, CreateForeignTravelAccountRequestDTO requestDTO) {

        // 관계 테이블에서 조회가 안되는 관계면 예외 던짐
        UserRole userRole = userAccountRelationRepository.findByUserIdAndAccountId(userId, requestDTO.getDomesticAccountId())
                .orElseThrow(UserAccountRelationNotFoundException::getInstance)
                .getUserRole();

        if(userRole != UserRole.ADMIN) {
            throw UnauthorizedUserRoleException.getInstance();
        }

        // 계좌 테이블에서 조회가 안되는 계좌이거나 조회되도 한화 여행통장이 아니면 예외 던짐
        String savedPassword = accountRepository.findById(requestDTO.getDomesticAccountId())
                .filter(account -> account.getAccountType() == AccountType.DOMESTIC)
                .orElseThrow(() -> {
                    if (!accountRepository.existsById(requestDTO.getDomesticAccountId())) {
                        return AccountNotFoundException.getInstance();
                    } else {
                        return InvalidAccountTypeException.getInstance();
                    }
                })
                .getPassword();

        // SSAFY 금융 API 계좌 생성 요청
        CreateForeignCurrencyDemandDepositAccountRequestDTO createForeignCurrencyDemandDepositAccountRequestDTO = CreateForeignCurrencyDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("createForeignCurrencyDemandDepositAccount")
                        .userKey(userKey)
                        .build())
                .accountTypeUniqueNo(FOREIGN_TRAVEL_ACCOUNT_TYPE_UNIQUE_NO)
                .currency(requestDTO.getCurrency())
                .build();

        CreateForeignCurrencyDemandDepositAccountResponseDTO createForeignCurrencyDemandDepositAccountResponseDTO = foreignClient.createForeignCurrencyDemandDepositAccount(createForeignCurrencyDemandDepositAccountRequestDTO);

        String accountNo = createForeignCurrencyDemandDepositAccountResponseDTO.getRec().getAccountNo();
        String currency = createForeignCurrencyDemandDepositAccountResponseDTO.getRec().getCurrency().getCurrency();

        // Account 테이블에 저장
        Account account = Account.builder()
                .accountNo(accountNo)
                .password(savedPassword)
                .accountType(AccountType.FOREIGN)
                .build();

        Account savedAccount = accountRepository.save(account);

        // UserAccountRelation 테이블에 저장
        UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                .userId(userId)
                .account(savedAccount)
                .userRole(UserRole.ADMIN)
                .build();

        userAccountRelationRepository.save(userAccountRelation);

        // Travel 서버에 외화계좌 생성시 외화여행계좌 테이블에 정보 저장 요청
        SaveForeignTravelAccountRequestDTO saveForeignTravelAccountRequestDTO = SaveForeignTravelAccountRequestDTO.builder()
                .foreignAccountId(savedAccount.getAccountId())
                .domesticAccountId(requestDTO.getDomesticAccountId())
                .currency(currency)
                .build();

        travelClient.saveForeignTravelAccount(saveForeignTravelAccountRequestDTO);

        ResponseCode responseCode = createForeignCurrencyDemandDepositAccountResponseDTO.getHeader().getResponseCode();
        String responseMessage = createForeignCurrencyDemandDepositAccountResponseDTO.getHeader().getResponseMessage();

        return SsafySuccessResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

    // 개인 통장 상세 조회 서비스 로직
    public PersonalAccountDetailResponseDTO getPersonalAccountDetail(Long userId, String userKey, Long accountId, String startDate, String endDate, String transactionType) {

        // 관계 테이블에서 조회가 안되는 관계면 예외 던짐
        userAccountRelationRepository.findByUserIdAndAccountId(userId, accountId)
                .orElseThrow(UserAccountRelationNotFoundException::getInstance);

        // 계좌 테이블에서 조회가 안되는 계좌이거나 조회되도 개인 통장이 아니면 예외 던짐
        String accountNo = accountRepository.findById(accountId)
                .filter(account -> account.getAccountType() == AccountType.PERSONAL)
                .orElseThrow(() -> {
                    if (!accountRepository.existsById(accountId)) {
                        return AccountNotFoundException.getInstance();
                    } else {
                        return InvalidAccountTypeException.getInstance();
                    }
                })
                .getAccountNo();

        // SSAFY 금융 API 계좌 조회 (단건) 요청
        InquireDemandDepositAccountRequestDTO inquireDemandDepositAccountRequestDTO = InquireDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("inquireDemandDepositAccount")
                        .userKey(userKey)
                        .build())
                .accountNo(accountNo)
                .build();

        InquireDemandDepositAccountResponseDTO inquireDemandDepositAccountResponseDTO = domesticClient.inquireDemandDepositAccount(inquireDemandDepositAccountRequestDTO);

        // SSAFY 금융 API 계좌 거래 내역 조회 요청
        InquireTransactionHistoryListRequestDTO inquireTransactionHistoryListRequestDTO = InquireTransactionHistoryListRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("inquireTransactionHistoryList")
                        .userKey(userKey)
                        .build())
                .accountNo(accountNo)
                .startDate(startDate)
                .endDate(endDate)
                .transactionType(transactionType)
                .orderByType("DESC")
                .build();

        InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO = domesticClient.inquireTransactionHistoryList(inquireTransactionHistoryListRequestDTO);

        Long accountBalance = inquireDemandDepositAccountResponseDTO.getRec().getAccountBalance();
        String bankName = inquireDemandDepositAccountResponseDTO.getRec().getBankName();

        List<PersonalAccountDetailResponseDTO.Transaction> transactionList = getPersonalAccountTransactionList(inquireTransactionHistoryListResponseDTO);

        return PersonalAccountDetailResponseDTO.builder()
                .accountId(accountId)
                .accountNo(accountNo)
                .accountBalance(accountBalance)
                .bankName(bankName)
                .transactionList(transactionList)
                .build();
    }

    // 한화 여행통장 상세 조회 서비스 로직
    public DomesticTravelAccountDetailResponseDTO getDomesticTravelAccountDetail(Long userId, String userKey, Long accountId, String startDate, String endDate, String transactionType) {

        // 관계 테이블에서 조회가 안되는 관계면 예외 던짐
        userAccountRelationRepository.findByUserIdAndAccountId(userId, accountId)
                .orElseThrow(UserAccountRelationNotFoundException::getInstance);

        // 계좌 테이블에서 조회가 안되는 계좌이거나 조회되도 한화 여행통장이 아니면 예외 던짐
        String accountNo = accountRepository.findById(accountId)
                .filter(account -> account.getAccountType() == AccountType.DOMESTIC)
                .orElseThrow(() -> {
                    if (!accountRepository.existsById(accountId)) {
                        return AccountNotFoundException.getInstance();
                    } else {
                        return InvalidAccountTypeException.getInstance();
                    }
                })
                .getAccountNo();

        // Travel 서버에 한화 여행통장 ID로 이름과 목표금액 반환 요청
        DomesticTravelAccountInfoResponseDTO domesticTravelAccountInfo = travelClient.getDomesticTravelAccountInfo(accountId);

        // SSAFY 금융 API 계좌 조회 (단건) 요청
        InquireDemandDepositAccountRequestDTO inquireDemandDepositAccountRequestDTO = InquireDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("inquireDemandDepositAccount")
                        .userKey(userKey)
                        .build())
                .accountNo(accountNo)
                .build();

        InquireDemandDepositAccountResponseDTO inquireDemandDepositAccountResponseDTO = domesticClient.inquireDemandDepositAccount(inquireDemandDepositAccountRequestDTO);

        // SSAFY 금융 API 계좌 거래 내역 조회 요청
        InquireTransactionHistoryListRequestDTO inquireTransactionHistoryListRequestDTO = InquireTransactionHistoryListRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("inquireTransactionHistoryList")
                        .userKey(userKey)
                        .build())
                .accountNo(accountNo)
                .startDate(startDate)
                .endDate(endDate)
                .transactionType(transactionType)
                .orderByType("DESC")
                .build();

        InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO = domesticClient.inquireTransactionHistoryList(inquireTransactionHistoryListRequestDTO);

        String accountName = domesticTravelAccountInfo.getAccountName();
        Long targetBalance = domesticTravelAccountInfo.getTargetAmount();

        Long accountBalance = inquireDemandDepositAccountResponseDTO.getRec().getAccountBalance();
        String bankName = inquireDemandDepositAccountResponseDTO.getRec().getBankName();

        List<DomesticTravelAccountDetailResponseDTO.Transaction> transactionList = getDomesticTravelAccountTransactionList(inquireTransactionHistoryListResponseDTO);

        return DomesticTravelAccountDetailResponseDTO.builder()
                .accountName(accountName)
                .targetAmount(targetBalance)
                .accountId(accountId)
                .accountNo(accountNo)
                .accountBalance(accountBalance)
                .bankName(bankName)
                .transactionList(transactionList)
                .build();
    }

    // 개인 통장 계좌 이체 비밀번호 검증 API
    public InternalServerSuccessResponseDTO verifyPersonalAccountPassword(Long userId, Long accountId, VerifyAccountPasswordRequestDTO requestDTO) {

        // 관계 테이블에서 조회가 안되는 관계면 예외 던짐
        UserRole userRole = userAccountRelationRepository.findByUserIdAndAccountId(userId, accountId)
                .orElseThrow(UserAccountRelationNotFoundException::getInstance)
                .getUserRole();

        if(userRole != UserRole.ADMIN) {
            throw UserAccountRelationNotFoundException.getInstance();
        }

        // 계좌 테이블에서 조회가 안되는 계좌이거나 조회되도 개인 통장이 아니면 예외 던짐
        String savedPassword = accountRepository.findById(accountId)
                .filter(account -> account.getAccountType() == AccountType.PERSONAL)
                .orElseThrow(() -> {
                    if (!accountRepository.existsById(accountId)) {
                        return AccountNotFoundException.getInstance();
                    } else {
                        return InvalidAccountTypeException.getInstance();
                    }
                })
                .getPassword();

        if(passwordEncoder.matches(requestDTO.getPassword() + PEPPER, savedPassword)){
            return InternalServerSuccessResponseDTO.builder()
                    .message("통장 비밀번호 검증 성공")
                    .build();
        }
        else{
            throw InvalidPasswordException.getInstance();
        }
    }

    // 한화 여행통장 계좌 이체 비밀번호 검증 API
    public InternalServerSuccessResponseDTO verifyDomesticTravelAccountPassword(Long userId, Long accountId, VerifyAccountPasswordRequestDTO requestDTO) {

        // 관계 테이블에서 조회가 안되는 관계면 예외 던짐
        UserRole userRole = userAccountRelationRepository.findByUserIdAndAccountId(userId, accountId)
                .orElseThrow(UserAccountRelationNotFoundException::getInstance)
                .getUserRole();

        // 조회가 되도 권한이 없는 멤버면 예외 던짐
        if(userRole == UserRole.NONE_PAYER) {
            throw UnauthorizedUserRoleException.getInstance();
        }

        // 계좌 테이블에서 조회가 안되는 계좌이거나 조회되도 한화 여행통장이 아니면 예외 던짐
        String savedPassword = accountRepository.findById(accountId)
                .filter(account -> account.getAccountType() == AccountType.DOMESTIC)
                .orElseThrow(() -> {
                    if (!accountRepository.existsById(accountId)) {
                        return AccountNotFoundException.getInstance();
                    } else {
                        return InvalidAccountTypeException.getInstance();
                    }
                })
                .getPassword();

        if(passwordEncoder.matches(requestDTO.getPassword() + PEPPER, savedPassword)){
            return InternalServerSuccessResponseDTO.builder()
                    .message("통장 비밀번호 검증 성공")
                    .build();
        }
        else{
            throw InvalidPasswordException.getInstance();
        }
    }

    // 한화 여행통장 멤버 목록 조회 서비스 로직 (민채)
    public DomesticTravelAccountMemberListResponseDTO getDomesticTravelAccountMemberList(Long userId, Long accountId) {

        // 관계 테이블에서 조회가 안되는 관계면 예외 던짐
        userAccountRelationRepository.findByUserIdAndAccountId(userId, accountId)
                .orElseThrow(UserAccountRelationNotFoundException::getInstance);

        // 계좌 테이블에서 조회가 안되는 계좌이거나 조회되도 한화 여행통장이 아니면 예외 던짐
        accountRepository.findById(accountId)
                .filter(account -> account.getAccountType() == AccountType.DOMESTIC)
                .orElseThrow(() -> {
                    if (!accountRepository.existsById(accountId)) {
                        return AccountNotFoundException.getInstance();
                    } else {
                        return InvalidAccountTypeException.getInstance();
                    }
                });

        List<UserAccountRelation> userAccountRelations = userAccountRelationRepository.findAllByAccountId(accountId)
                .orElse(new ArrayList<>());

        List<Member> members = new ArrayList<>();

        // User 서버에 userId로 userName 조회하는 요청을 모든 멤베에 대해 보냄
        for (UserAccountRelation member : userAccountRelations) {
            UserNameResponseDTO userNameResponseDTO = userClient.getUserName(member.getUserId());

            members.add(DomesticTravelAccountMemberListResponseDTO.Member.builder()
                    .userId(member.getUserId())
                    .userName(userNameResponseDTO.getUserName())
                    .role(member.getUserRole())
                    .build());
        }

        return DomesticTravelAccountMemberListResponseDTO.builder()
                .memberCount((long) members.size())
                .members(members).build();
    }

    // 개인 통장 계좌 이체 서비스 로직
    public SsafySuccessResponseDTO transferPersonalAccount(Long userId, String userKey, Long accountId, TransferPersonalAccountRequestDTO requestDTO) {

        // 관계 테이블에서 조회가 안되는 관계면 예외 던짐
        UserRole userRole = userAccountRelationRepository.findByUserIdAndAccountId(userId, accountId)
                .orElseThrow(UserAccountRelationNotFoundException::getInstance)
                .getUserRole();

        if(userRole != UserRole.ADMIN) {
            throw UserAccountRelationNotFoundException.getInstance();
        }

        // 계좌 테이블에서 조회가 안되는 계좌이거나 조회되도 개인 통장이 아니면 예외 던짐
        accountRepository.findById(accountId)
                .filter(account -> account.getAccountType() == AccountType.PERSONAL)
                .orElseThrow(() -> {
                    if (!accountRepository.existsById(accountId)) {
                        return AccountNotFoundException.getInstance();
                    } else {
                        return InvalidAccountTypeException.getInstance();
                    }
                });

        // SSAFY 금융 API 계좌 이체 요청
        UpdateDemandDepositAccountTransferRequestDTO updateDemandDepositAccountTransferRequestDTO = UpdateDemandDepositAccountTransferRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("updateDemandDepositAccountTransfer")
                        .userKey(userKey)
                        .build())
                .depositAccountNo(requestDTO.getDepositAccountNo())
                .depositTransactionSummary(requestDTO.getDepositTransactionSummary())
                .transactionBalance(requestDTO.getTransactionBalance())
                .withdrawalAccountNo(requestDTO.getWithdrawalAccountNo())
                .withdrawalTransactionSummary(requestDTO.getWithdrawalTransactionSummary())
                .build();

        UpdateDemandDepositAccountTransferResponseDTO updateDemandDepositAccountTransferResponseDTO = domesticClient.updateDemandDepositAccountTransfer(updateDemandDepositAccountTransferRequestDTO);

        ResponseCode responseCode = updateDemandDepositAccountTransferResponseDTO.getHeader().getResponseCode();
        String responseMessage = updateDemandDepositAccountTransferResponseDTO.getHeader().getResponseMessage();

        return SsafySuccessResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

    // 한화 여행통장 계좌 이체 서비스 로직
    public SsafySuccessResponseDTO transferDomesticTravelAccount(Long userId, String userKey, Long accountId, TransferDomesticTravelAccountRequestDTO requestDTO) {

        // 관계 테이블에서 조회가 안되는 관계면 예외 던짐
        UserRole userRole = userAccountRelationRepository.findByUserIdAndAccountId(userId, accountId)
                .orElseThrow(UserAccountRelationNotFoundException::getInstance)
                .getUserRole();

        // 조회가 되도 권한이 없는 멤버면 예외 던짐
        if(userRole == UserRole.NONE_PAYER) {
            throw UnauthorizedUserRoleException.getInstance();
        }

        // 계좌 테이블에서 조회가 안되는 계좌이거나 조회되도 한화 여행통장이 아니면 예외 던짐
        accountRepository.findById(accountId)
                .filter(account -> account.getAccountType() == AccountType.DOMESTIC)
                .orElseThrow(() -> {
                    if (!accountRepository.existsById(accountId)) {
                        return AccountNotFoundException.getInstance();
                    } else {
                        return InvalidAccountTypeException.getInstance();
                    }
                });

        // SSAFY 금융 API 계좌 이체 요청
        UpdateDemandDepositAccountTransferRequestDTO updateDemandDepositAccountTransferRequestDTO = UpdateDemandDepositAccountTransferRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("updateDemandDepositAccountTransfer")
                        .userKey(userKey)
                        .build())
                .depositAccountNo(requestDTO.getDepositAccountNo())
                .depositTransactionSummary(requestDTO.getDepositTransactionSummary())
                .transactionBalance(requestDTO.getTransactionBalance())
                .withdrawalAccountNo(requestDTO.getWithdrawalAccountNo())
                .withdrawalTransactionSummary(requestDTO.getWithdrawalTransactionSummary())
                .build();

        UpdateDemandDepositAccountTransferResponseDTO updateDemandDepositAccountTransferResponseDTO = domesticClient.updateDemandDepositAccountTransfer(updateDemandDepositAccountTransferRequestDTO);

        ResponseCode responseCode = updateDemandDepositAccountTransferResponseDTO.getHeader().getResponseCode();
        String responseMessage = updateDemandDepositAccountTransferResponseDTO.getHeader().getResponseMessage();

        return SsafySuccessResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

    // SSAFY 금융 API 계좌 거래 내역 responseDTO -> 개인 통장 거래 내역 리스트
    private List<PersonalAccountDetailResponseDTO.Transaction> getPersonalAccountTransactionList(InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO) {
        return inquireTransactionHistoryListResponseDTO.getRec().getList().stream()
                .map(item -> PersonalAccountDetailResponseDTO.Transaction.builder()
                        .transactionType(item.getTransactionType())
                        .transactionSummary(item.getTransactionSummary())
                        .transactionDate(item.getTransactionDate())
                        .transactionTime(item.getTransactionTime())
                        .transactionBalance(item.getTransactionBalance())
                        .transactionAfterBalance(item.getTransactionAfterBalance())
                        .build())
                .collect(Collectors.toList());
    }

    // SSAFY 금융 API 계좌 거래 내역 responseDTO -> 한화 여행 통장 거래 내역 리스트
    private List<DomesticTravelAccountDetailResponseDTO.Transaction> getDomesticTravelAccountTransactionList(InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO) {
        return inquireTransactionHistoryListResponseDTO.getRec().getList().stream()
                .map(item -> DomesticTravelAccountDetailResponseDTO.Transaction.builder()
                        .transactionType(item.getTransactionType())
                        .transactionSummary(item.getTransactionSummary())
                        .transactionDate(item.getTransactionDate())
                        .transactionTime(item.getTransactionTime())
                        .transactionBalance(item.getTransactionBalance())
                        .transactionAfterBalance(item.getTransactionAfterBalance())
                        .build())
                .collect(Collectors.toList());
    }

}
