package com.trabean.account.service;

import com.trabean.account.domain.Account;
import com.trabean.account.domain.Account.AccountType;
import com.trabean.account.domain.UserAccountRelation;
import com.trabean.account.domain.UserAccountRelation.UserRole;
import com.trabean.account.dto.request.*;
import com.trabean.account.dto.response.*;
import com.trabean.account.dto.response.DomesticTravelAccountMemberListResponseDTO.Member;
import com.trabean.account.repository.AccountRepository;
import com.trabean.account.repository.UserAccountRelationRepository;
import com.trabean.common.InternalServerSuccessResponseDTO;
import com.trabean.exception.custom.ExternalServerErrorException;
import com.trabean.exception.custom.InvalidPasswordException;
import com.trabean.exception.custom.UserAccountRelationNotFoundException;
import com.trabean.external.msa.notification.client.NotificationClient;
import com.trabean.external.msa.notification.dto.request.NotificationRequestDTO;
import com.trabean.external.msa.travel.client.TravelClient;
import com.trabean.external.msa.travel.dto.request.SaveDomesticTravelAccountRequestDTO;
import com.trabean.external.msa.travel.dto.request.SaveForeignTravelAccountRequestDTO;
import com.trabean.external.msa.travel.dto.response.DomesticTravelAccountInfoResponseDTO;
import com.trabean.external.msa.user.client.UserClient;
import com.trabean.external.msa.user.dto.request.UpdateMainAccountIdRequestDTO;
import com.trabean.external.msa.user.dto.response.UserNameResponseDTO;
import com.trabean.external.ssafy.api.domestic.client.DomesticClient;
import com.trabean.external.ssafy.api.domestic.dto.request.*;
import com.trabean.external.ssafy.api.domestic.dto.response.*;
import com.trabean.external.ssafy.api.foriegn.client.ForeignClient;
import com.trabean.external.ssafy.api.foriegn.dto.request.CreateForeignCurrencyDemandDepositAccountRequestDTO;
import com.trabean.external.ssafy.api.foriegn.dto.request.InquireForeignCurrencyDemandDepositAccountRequestDTO;
import com.trabean.external.ssafy.api.foriegn.dto.response.CreateForeignCurrencyDemandDepositAccountResponseDTO;
import com.trabean.external.ssafy.api.foriegn.dto.response.InquireForeignCurrencyDemandDepositAccountResponseDTO;
import com.trabean.external.ssafy.api.memo.client.MemoClient;
import com.trabean.external.ssafy.api.memo.dto.request.TransactionMemoRequestDTO;
import com.trabean.external.ssafy.common.SsafyApiResponseDTO;
import com.trabean.external.ssafy.common.SsafyApiResponseDTOFactory;
import com.trabean.external.ssafy.util.RequestHeader;
import com.trabean.interceptor.UserHeaderInterceptor;
import com.trabean.util.ValidateInputDTO;
import com.trabean.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.trabean.account.domain.Account.AccountType.*;
import static com.trabean.account.domain.UserAccountRelation.UserRole.ADMIN;
import static com.trabean.constant.Constant.*;
import static com.trabean.external.msa.notification.dto.request.NotificationRequestDTO.Type.DEPOSIT;
import static com.trabean.external.msa.notification.dto.request.NotificationRequestDTO.Type.WITHDRAW;
import static com.trabean.external.ssafy.constant.ApiName.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final AccountHelperService accountHelperService;

    private final AccountRepository accountRepository;
    private final UserAccountRelationRepository userAccountRelationRepository;

    private final DomesticClient domesticClient;
    private final ForeignClient foreignClient;
    private final MemoClient memoClient;

    private final UserClient userClient;
    private final TravelClient travelClient;
    private final NotificationClient notificationClient;

    private final PasswordEncoder passwordEncoder;

    // 통장 목록 조회 서비스 로직
    public AccountListResponseDTO getAccountList() {

        // SSAFY 금융 API 계좌 목록 조회 요청
        InquireDemandDepositAccountListRequestDTO inquireDemandDepositAccountListRequestDTO = InquireDemandDepositAccountListRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(inquireDemandDepositAccountList)
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .build();
        InquireDemandDepositAccountListResponseDTO inquireDemandDepositAccountListResponseDTO = domesticClient.inquireDemandDepositAccountList(inquireDemandDepositAccountListRequestDTO);

        // 본인이 여행통장 주인이 아닌 통장들에 대한 로직
        List<UserAccountRelation> userAccountRelationList = ValidationUtil.validateUserAccountRelationList(userAccountRelationRepository.findAllByUserId(UserHeaderInterceptor.userId.get()));

        // 한화 여행통장(멤버)
        List<UserAccountRelation> adminRelationsForDomesticNonAdminAccounts = userAccountRelationList.stream()
                .filter(relation -> relation.getAccount().getAccountType() == DOMESTIC &&
                        relation.getUserRole() != ADMIN)
                .map(relation -> userAccountRelationRepository.findByAccount_AccountIdAndUserRole(relation.getAccount().getAccountId(), ADMIN))
                .flatMap(List::stream)
                .toList();

        for (UserAccountRelation u : adminRelationsForDomesticNonAdminAccounts) {

            // SSAFY 금융 API 계좌 조회 (단건) 요청
            InquireDemandDepositAccountRequestDTO inquireDemandDepositAccountRequestDTO = InquireDemandDepositAccountRequestDTO.builder()
                    .header(RequestHeader.builder()
                            .apiName(inquireDemandDepositAccount)
                            .userKey(accountHelperService.getAdminUserKeyByAccountId(u.getAccount().getAccountId()))
                            .build())
                    .accountNo(ValidationUtil.validateAccount(accountRepository.findById(u.getAccount().getAccountId())).getAccountNo())
                    .build();
            InquireDemandDepositAccountResponseDTO inquireDemandDepositAccountResponseDTO = domesticClient.inquireDemandDepositAccount(inquireDemandDepositAccountRequestDTO);

            inquireDemandDepositAccountListResponseDTO.getRec().add((inquireDemandDepositAccountResponseDTO.getRec()));
        }

        if (inquireDemandDepositAccountListResponseDTO.getRec() == null) {
            return AccountListResponseDTO.builder()
                    .mainAccount(null)
                    .accountList(new ArrayList<>())
                    .build();
        }
        return getAccountList(inquireDemandDepositAccountListResponseDTO);
    }

    // SSAFY 금융 API 게좌 목록 responseDTO -> 통장 목록 조회 responseDTO
    private AccountListResponseDTO getAccountList(InquireDemandDepositAccountListResponseDTO inquireDemandDepositAccountListResponseDTO) {
        AccountListResponseDTO.Account mainAccount = null;
        List<AccountListResponseDTO.Account> accountList = new ArrayList<>();

        // User 서버에 userId로 mainAccountId 반환 요청
        Long mainAccountId = userClient.getMainAccountId(UserHeaderInterceptor.userId.get()).getMainAccountId();

        // Travel 서버에 한화 여행통장이름 이름 반환 요청
        for (InquireDemandDepositAccountListResponseDTO.REC account : inquireDemandDepositAccountListResponseDTO.getRec()) {
            Account savedAccount = ValidationUtil.validateAccount(accountRepository.findByAccountNo(account.getAccountNo()));
            String savedAccountName = savedAccount.getAccountType() == DOMESTIC
                    ? travelClient.getDomesticTravelAccountInfo(savedAccount.getAccountId()).getAccountName()
                    : "개인 입출금 통장";

            AccountListResponseDTO.Account dtoAccount = AccountListResponseDTO.Account.builder()
                    .accountId(savedAccount.getAccountId())
                    .accountNo(account.getAccountNo())
                    .accountName(savedAccountName)
                    .bankName(account.getBankName())
                    .accountBalance(account.getAccountBalance())
                    .accountType(savedAccount.getAccountType())
                    .build();

            if (savedAccount.getAccountId().equals(mainAccountId)) {
                mainAccount = dtoAccount;
            } else {
                accountList.add(dtoAccount);
            }
        }

        return AccountListResponseDTO.builder()
                .mainAccount(mainAccount)
                .accountList(accountList)
                .build();
    }

    // 최근 이체 목록 조회 서비스 로직
    public RecentTransactionListResponseDTO getRecentTransactionList(Long accountId, String startDate, String endDate) {

        String accountNo = ValidationUtil.validateInput(ValidateInputDTO.builder()
                    .account(accountRepository.findById(accountId))
                    .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                    .isPayable(true)
                    .build())
                .getAccountNo();

        // SSAFY 금융 API 계좌 거래 내역 조회 요청
        InquireTransactionHistoryListRequestDTO inquireTransactionHistoryListRequestDTO = InquireTransactionHistoryListRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(inquireTransactionHistoryList)
                        .userKey(accountHelperService.getAdminUserKeyByAccountId(accountId))
                        .build())
                .accountNo(accountNo)
                .startDate(startDate)
                .endDate(endDate)
                .transactionType("D")
                .orderByType("DESC")
                .build();
        InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO = domesticClient.inquireTransactionHistoryList(inquireTransactionHistoryListRequestDTO);

        return getUniqueLastTransactionList(inquireTransactionHistoryListResponseDTO);
    }

    // SSAFY 금융 API 계좌 거래 내역 responseDTO -> 최근 이체 목록 조회 responseDTO
    public RecentTransactionListResponseDTO getUniqueLastTransactionList(InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO) {
        List<RecentTransactionListResponseDTO.Info> accountList = inquireTransactionHistoryListResponseDTO.getRec().getList().stream()
                .filter(transactionHistory -> transactionHistory.getTransactionAccountNo() != null && !transactionHistory.getTransactionAccountNo().trim().isEmpty())
                .map(transactionHistory -> {
                    Account account = ValidationUtil.validateAccount(accountRepository.findByAccountNo(transactionHistory.getTransactionAccountNo()));

                    List<UserAccountRelation> userAccountRelationList = ValidationUtil.validateUserAccountRelationList(userAccountRelationRepository.findAllByAccountId(account.getAccountId()));

                    Long userId = userAccountRelationList.stream()
                            .filter(relation -> relation.getUserRole() == ADMIN)
                            .map(UserAccountRelation::getUserId)
                            .findFirst()
                            .orElseThrow(UserAccountRelationNotFoundException::getInstance);

                    // User 서버에 userName 반환 요청
                    UserNameResponseDTO userNameResponseDTO = userClient.getUserName(userId);

                    // SSAFY 금융 API 계좌 조회 (단건) 요청
                    InquireDemandDepositAccountRequestDTO inquireDemandDepositAccountRequestDTO = InquireDemandDepositAccountRequestDTO.builder()
                            .header(RequestHeader.builder()
                                    .apiName(inquireDemandDepositAccount)
                                    .userKey(accountHelperService.getAdminUserKeyByAccountId(account.getAccountId()))
                                    .build())
                            .accountNo(account.getAccountNo())
                            .build();
                    InquireDemandDepositAccountResponseDTO inquireDemandDepositAccountResponseDTO = domesticClient.inquireDemandDepositAccount(inquireDemandDepositAccountRequestDTO);

                    return RecentTransactionListResponseDTO.Info.builder()
                            .accountId(account.getAccountId())
                            .accountNo(transactionHistory.getTransactionAccountNo())
                            .adminName(userNameResponseDTO.getUserName())
                            .bankName(inquireDemandDepositAccountResponseDTO.getRec().getBankName())
                            .build();
                })
                .distinct()
                .limit(6)
                .collect(Collectors.toList());

        return RecentTransactionListResponseDTO.builder()
                .accountList(accountList)
                .build();
    }

    // 계좌 이체 한도 변경 서비스 로직
    public SsafyApiResponseDTO updateTransferLimit(Long accountId, UpdateAccountTransferLimitRequestDTO requestDTO) {

        String accountNo = ValidationUtil.validateInput(ValidateInputDTO.builder()
                        .account(accountRepository.findById(accountId))
                        .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                        .userRole(ADMIN)
                        .build())
                .getAccountNo();

        // SSAFY 금융 API 계좌 이체 한도 변경 요청
        UpdateTransferLimitRequestDTO updateTransferLimitRequestDTO = UpdateTransferLimitRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(updateTransferLimit)
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .accountNo(accountNo)
                .oneTimeTransferLimit(requestDTO.getOneTimeTransferLimit())
                .dailyTransferLimit(requestDTO.getDailyTransferLimit())
                .build();
        UpdateTransferLimitResponseDTO updateTransferLimitResponseDTO = domesticClient.updateTransferLimit(updateTransferLimitRequestDTO);

        return SsafyApiResponseDTOFactory.create(updateTransferLimitResponseDTO.getHeader());
    }

    // 개인 통장 생성 서비스 로직
    @Transactional
    public SsafyApiResponseDTO createPersonalAccount(CreatePersonalAccountRequestDTO requestDTO) {

        // SSAFY 금융 API 계좌 생성 요청
        CreateDemandDepositAccountRequestDTO createDemandDepositAccountRequestDTO = CreateDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(createDemandDepositAccount)
                        .userKey(UserHeaderInterceptor.userKey.get())
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
                .accountType(PERSONAL)
                .build();
        Account savedAccount = accountRepository.save(account);

        // UserAccountRelation 테이블에 저장
        UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                .userId(UserHeaderInterceptor.userId.get())
                .account(savedAccount)
                .userRole(ADMIN)
                .build();
        userAccountRelationRepository.save(userAccountRelation);

        // User 서버에 mainAccountId가 존재하는지 조회해서 존재 안하면 mainAccount로 저장
        if (userClient.getMainAccountId(UserHeaderInterceptor.userId.get()).getMainAccountId() == null) {
            UpdateMainAccountIdRequestDTO updateMainAccountIdRequestDTO = UpdateMainAccountIdRequestDTO.builder()
                    .userId(UserHeaderInterceptor.userId.get())
                    .mainAccountId(savedAccount.getAccountId())
                    .build();
            userClient.updateMainAccountId(updateMainAccountIdRequestDTO);
        }

        return SsafyApiResponseDTOFactory.create(createDemandDepositAccountResponseDTO.getHeader());
    }

    // 개인 통장 상세 조회 서비스 로직
    public PersonalAccountDetailResponseDTO getPersonalAccountDetail(Long accountId, String startDate, String endDate, String transactionType) {

        String accountNo = ValidationUtil.validateInput(ValidateInputDTO.builder()
                    .account(accountRepository.findById(accountId))
                    .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                    .accountType(PERSONAL)
                    .userRole(ADMIN)
                    .build())
                .getAccountNo();

        // SSAFY 금융 API 계좌 조회 (단건) 요청
        InquireDemandDepositAccountRequestDTO inquireDemandDepositAccountRequestDTO = InquireDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(inquireDemandDepositAccount)
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .accountNo(accountNo)
                .build();
        InquireDemandDepositAccountResponseDTO inquireDemandDepositAccountResponseDTO = domesticClient.inquireDemandDepositAccount(inquireDemandDepositAccountRequestDTO);

        // SSAFY 금융 API 계좌 거래 내역 조회 요청
        InquireTransactionHistoryListRequestDTO inquireTransactionHistoryListRequestDTO = InquireTransactionHistoryListRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(inquireTransactionHistoryList)
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .accountNo(accountNo)
                .startDate(startDate)
                .endDate(endDate)
                .transactionType(transactionType)
                .orderByType("DESC")
                .build();
        InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO = domesticClient.inquireTransactionHistoryList(inquireTransactionHistoryListRequestDTO);

        return PersonalAccountDetailResponseDTO.builder()
                .accountId(accountId)
                .accountNo(accountNo)
                .accountBalance(inquireDemandDepositAccountResponseDTO.getRec().getAccountBalance())
                .accountName("개인 입출금 통장")
                .bankName(inquireDemandDepositAccountResponseDTO.getRec().getBankName())
                .transactionList(getPersonalAccountTransactionList(inquireTransactionHistoryListResponseDTO))
                .build();
    }

    // SSAFY 금융 API 계좌 거래 내역 responseDTO -> 개인 통장 거래 내역 리스트
    private List<PersonalAccountDetailResponseDTO.Transaction> getPersonalAccountTransactionList(InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO) {
        return inquireTransactionHistoryListResponseDTO.getRec().getList().stream()
                .map(transaction -> PersonalAccountDetailResponseDTO.Transaction.builder()
                        .transactionType(transaction.getTransactionType())
                        .transactionSummary(transaction.getTransactionSummary())
                        .transactionDate(transaction.getTransactionDate())
                        .transactionTime(transaction.getTransactionTime())
                        .transactionBalance(transaction.getTransactionBalance())
                        .transactionAfterBalance(transaction.getTransactionAfterBalance())
                        .transactionMemo(transaction.getTransactionMemo())
                        .build())
                .collect(Collectors.toList());
    }

    // 개인 통장 생성일 조회 서비스 로직
    public PersonalAccountCreatedDateResponseDTO getPersonalAccountCreatedDate(Long accountId) {

        String accountNo = ValidationUtil.validateInput(ValidateInputDTO.builder()
                        .account(accountRepository.findById(accountId))
                        .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                        .accountType(PERSONAL)
                        .userRole(ADMIN)
                        .build())
                .getAccountNo();

        // SSAFY 금융 API 계좌 조회 (단건) 요청
        InquireDemandDepositAccountRequestDTO inquireDemandDepositAccountRequestDTO = InquireDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(inquireDemandDepositAccount)
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .accountNo(accountNo)
                .build();
        InquireDemandDepositAccountResponseDTO inquireDemandDepositAccountResponseDTO = domesticClient.inquireDemandDepositAccount(inquireDemandDepositAccountRequestDTO);

        return PersonalAccountCreatedDateResponseDTO.builder()
                .accountCreatedDate(inquireDemandDepositAccountResponseDTO.getRec().getAccountCreatedDate())
                .build();
    }

    // 개인 통장 계좌 이체 서비스 로직
    public SsafyApiResponseDTO transferPersonalAccount(Long accountId, TransferPersonalAccountRequestDTO requestDTO) {

        ValidationUtil.validateInput(ValidateInputDTO.builder()
                .account(accountRepository.findById(accountId))
                .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                .accountType(PERSONAL)
                .userRole(ADMIN)
                .isPayable(true)
                .build());

        String depositTransactionSummary = getAccountName(requestDTO.getWithdrawalAccountNo()).getName();
        String withdrawalTransactionSummary = getAccountName(requestDTO.getDepositAccountNo()).getName();

        // SSAFY 금융 API 계좌 이체 요청
        UpdateDemandDepositAccountTransferRequestDTO updateDemandDepositAccountTransferRequestDTO = UpdateDemandDepositAccountTransferRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(updateDemandDepositAccountTransfer)
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .depositAccountNo(requestDTO.getDepositAccountNo())
                .depositTransactionSummary(depositTransactionSummary)
                .transactionBalance(requestDTO.getTransactionBalance())
                .withdrawalAccountNo(requestDTO.getWithdrawalAccountNo())
                .withdrawalTransactionSummary(withdrawalTransactionSummary)
                .build();
        UpdateDemandDepositAccountTransferResponseDTO updateDemandDepositAccountTransferResponseDTO = domesticClient.updateDemandDepositAccountTransfer(updateDemandDepositAccountTransferRequestDTO);

        Long depositAccountId = ValidationUtil.validateAccount(accountRepository.findByAccountNo(requestDTO.getDepositAccountNo())).getAccountId();
        Long withdrawalAccountId = ValidationUtil.validateAccount(accountRepository.findByAccountNo(requestDTO.getWithdrawalAccountNo())).getAccountId();

        // SSAFY 금융 API 거래내역 메모 요청 (출금 메모)
        TransactionMemoRequestDTO transactionWithdrawalMemoRequestDTO = TransactionMemoRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(transactionMemo)
                        .userKey(accountHelperService.getAdminUserKeyByAccountId(withdrawalAccountId))
                        .build())
                .accountNo(requestDTO.getWithdrawalAccountNo())
                .transactionUniqueNo(updateDemandDepositAccountTransferResponseDTO.getRec().get(0).getTransactionUniqueNo())
                .transactionMemo(String.valueOf(UserHeaderInterceptor.userId.get()))
                .build();
        memoClient.transactionMemo(transactionWithdrawalMemoRequestDTO);

        // SSAFY 금융 API 거래내역 메모 요청 (입금 메모)
        TransactionMemoRequestDTO transactionDepositMemoRequestDTO = TransactionMemoRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(transactionMemo)
                        .userKey(accountHelperService.getAdminUserKeyByAccountId(depositAccountId))
                        .build())
                .accountNo(requestDTO.getDepositAccountNo())
                .transactionUniqueNo(updateDemandDepositAccountTransferResponseDTO.getRec().get(1).getTransactionUniqueNo())
                .transactionMemo(String.valueOf(UserHeaderInterceptor.userId.get()))
                .build();
        memoClient.transactionMemo(transactionDepositMemoRequestDTO);

        // Notification 서버 입출금 시 알림 생성 요청
        NotificationRequestDTO notificationWithdrawalRequestDTO = NotificationRequestDTO.builder()
                .senderId(UserHeaderInterceptor.userId.get())
                .receiverIdList(List.of(UserHeaderInterceptor.userId.get()))
                .accountId(withdrawalAccountId)
                .notificationType(WITHDRAW)
                .amount(requestDTO.getTransactionBalance())
                .build();
        notificationClient.sendNotification(notificationWithdrawalRequestDTO);

        // Notification 서버 입출금 시 알림 생성 요청
        NotificationRequestDTO notificationDepositRequestDTO = NotificationRequestDTO.builder()
                .senderId(UserHeaderInterceptor.userId.get())
                .receiverIdList(accountHelperService.getAccountMemberIdList(depositAccountId))
                .accountId(depositAccountId)
                .notificationType(DEPOSIT)
                .amount(requestDTO.getTransactionBalance())
                .build();
        notificationClient.sendNotification(notificationDepositRequestDTO);

        return SsafyApiResponseDTOFactory.create(updateDemandDepositAccountTransferResponseDTO.getHeader());
    }

    // 개인 통장 계좌 이체 비밀번호 검증 서비스 로직
    public InternalServerSuccessResponseDTO verifyPersonalAccountPassword(Long accountId, VerifyAccountPasswordRequestDTO requestDTO) {

        String savedPassword = ValidationUtil.validateInput(ValidateInputDTO.builder()
                        .account(accountRepository.findById(accountId))
                        .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                        .accountType(PERSONAL)
                        .userRole(ADMIN)
                        .isPayable(true)
                        .build())
                .getPassword();

        if (!passwordEncoder.matches(requestDTO.getPassword() + PEPPER, savedPassword)) {
            throw InvalidPasswordException.getInstance();
        }

        return InternalServerSuccessResponseDTO.builder()
                .message("통장 비밀번호 검증 성공")
                .build();
    }

    // 한화 여행통장 생성 서비스 로직
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public SsafyApiResponseDTO createDomesticTravelAccount(CreateDomesticTravelAccountRequestDTO requestDTO) {

        // SSAFY 금융 API 계좌 생성 요청
        CreateDemandDepositAccountRequestDTO createDemandDepositAccountRequestDTO = CreateDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(createDemandDepositAccount)
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .accountTypeUniqueNo(DOMESTIC_TRAVEL_ACCOUNT_TYPE_UNIQUE_NO)
                .build();
        CreateDemandDepositAccountResponseDTO createDemandDepositAccountResponseDTO = domesticClient.createDemandDepositAccount(createDemandDepositAccountRequestDTO);

        String accountNo = createDemandDepositAccountResponseDTO.getRec().getAccountNo();
        String hashedPassword = passwordEncoder.encode(requestDTO.getPassword() + PEPPER);

        Account savedAccount = null;
        UserAccountRelation savedUserAccountRelation = null;

        try {
            // Account 테이블에 저장
            Account account = Account.builder()
                    .accountNo(accountNo)
                    .password(hashedPassword)
                    .accountType(DOMESTIC)
                    .build();
            savedAccount = accountRepository.save(account);

            // UserAccountRelation 테이블에 저장
            UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                    .userId(UserHeaderInterceptor.userId.get())
                    .account(savedAccount)
                    .userRole(ADMIN)
                    .build();
            savedUserAccountRelation = userAccountRelationRepository.save(userAccountRelation);

            // Travel 서버에 원화계좌 생성시 원화여행계좌 테이블에 정보 저장 요청
            SaveDomesticTravelAccountRequestDTO saveDomesticTravelAccountRequestDTO = SaveDomesticTravelAccountRequestDTO.builder()
                    .accountId(savedAccount.getAccountId())
                    .accountName(requestDTO.getAccountName())
                    .targetAmount(requestDTO.getTargetAmount())
                    .build();
            travelClient.saveDomesticTravelAccount(saveDomesticTravelAccountRequestDTO);

        } catch (ExternalServerErrorException e) {
            if (savedUserAccountRelation != null) {
                userAccountRelationRepository.delete(savedUserAccountRelation);
            }

            if (savedAccount != null) {
                accountRepository.delete(savedAccount);
            }

            throw e;
        }

        return SsafyApiResponseDTOFactory.create(createDemandDepositAccountResponseDTO.getHeader());
    }

    // 한화 여행통장 상세 조회 서비스 로직
    public DomesticTravelAccountDetailResponseDTO getDomesticTravelAccountDetail(Long accountId, String startDate, String endDate, String transactionType, String selectedUserId) {

        String accountNo = ValidationUtil.validateInput(ValidateInputDTO.builder()
                        .account(accountRepository.findById(accountId))
                        .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                        .accountType(DOMESTIC)
                        .build())
                .getAccountNo();

        // Travel 서버에 한화 여행통장 ID로 이름과 목표금액 반환 요청
        DomesticTravelAccountInfoResponseDTO domesticTravelAccountInfo = travelClient.getDomesticTravelAccountInfo(accountId);

        // SSAFY 금융 API 계좌 조회 (단건) 요청
        InquireDemandDepositAccountRequestDTO inquireDemandDepositAccountRequestDTO = InquireDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(inquireDemandDepositAccount)
                        .userKey(accountHelperService.getAdminUserKeyByAccountId(accountId))
                        .build())
                .accountNo(accountNo)
                .build();
        InquireDemandDepositAccountResponseDTO inquireDemandDepositAccountResponseDTO = domesticClient.inquireDemandDepositAccount(inquireDemandDepositAccountRequestDTO);

        // SSAFY 금융 API 계좌 거래 내역 조회 요청
        InquireTransactionHistoryListRequestDTO inquireTransactionHistoryListRequestDTO = InquireTransactionHistoryListRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(inquireTransactionHistoryList)
                        .userKey(accountHelperService.getAdminUserKeyByAccountId(accountId))
                        .build())
                .accountNo(accountNo)
                .startDate(startDate)
                .endDate(endDate)
                .transactionType(transactionType)
                .orderByType("DESC")
                .build();
        InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO = domesticClient.inquireTransactionHistoryList(inquireTransactionHistoryListRequestDTO);

        return DomesticTravelAccountDetailResponseDTO.builder()
                .accountName(domesticTravelAccountInfo.getAccountName())
                .targetAmount(domesticTravelAccountInfo.getTargetAmount())
                .accountId(accountId)
                .accountNo(accountNo)
                .accountBalance(inquireDemandDepositAccountResponseDTO.getRec().getAccountBalance())
                .bankName(inquireDemandDepositAccountResponseDTO.getRec().getBankName())
                .transactionList(getDomesticTravelAccountTransactionList(inquireTransactionHistoryListResponseDTO, selectedUserId))
                .build();
    }

    // SSAFY 금융 API 계좌 거래 내역 responseDTO -> 한화 여행 통장 거래 내역 리스트
    private List<DomesticTravelAccountDetailResponseDTO.Transaction> getDomesticTravelAccountTransactionList(InquireTransactionHistoryListResponseDTO inquireTransactionHistoryListResponseDTO, String selectedUserId) {
        return inquireTransactionHistoryListResponseDTO.getRec().getList().stream()
                .filter(transaction -> {
                    String transactionMemo = transaction.getTransactionMemo();

                    if ("-1".equals(selectedUserId)) {
                        return true;
                    }

                    return selectedUserId.equals(transactionMemo);
                })
                .map(transaction -> DomesticTravelAccountDetailResponseDTO.Transaction.builder()
                        .transactionType(transaction.getTransactionType())
                        .transactionSummary(transaction.getTransactionSummary())
                        .transactionDate(transaction.getTransactionDate())
                        .transactionTime(transaction.getTransactionTime())
                        .transactionBalance(transaction.getTransactionBalance())
                        .transactionAfterBalance(transaction.getTransactionAfterBalance())
                        .transactionMemo(transaction.getTransactionMemo())
                        .build())
                .collect(Collectors.toList());
    }

    // 한화 여행통장 생성일 조회 서비스 로직
    public DomesticTravelAccountCreatedDateResponseDTO getDomesticTravelAccountCreatedDate(Long accountId) {

        String accountNo = ValidationUtil.validateInput(ValidateInputDTO.builder()
                        .account(accountRepository.findById(accountId))
                        .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                        .accountType(DOMESTIC)
                        .build())
                .getAccountNo();

        // SSAFY 금융 API 계좌 조회 (단건) 요청
        InquireDemandDepositAccountRequestDTO inquireDemandDepositAccountRequestDTO = InquireDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(inquireDemandDepositAccount)
                        .userKey(accountHelperService.getAdminUserKeyByAccountId(accountId))
                        .build())
                .accountNo(accountNo)
                .build();
        InquireDemandDepositAccountResponseDTO inquireDemandDepositAccountResponseDTO = domesticClient.inquireDemandDepositAccount(inquireDemandDepositAccountRequestDTO);

        return DomesticTravelAccountCreatedDateResponseDTO.builder()
                .accountCreatedDate(inquireDemandDepositAccountResponseDTO.getRec().getAccountCreatedDate())
                .build();
    }

    // 한화 여행통장 잔액 조회 서비스 로직
    public DomesticTravelAccountBalanceResponseDTO getDomesticTravelAccountBalance(Long accountId) {

        String accountNo = ValidationUtil.validateInput(ValidateInputDTO.builder()
                        .account(accountRepository.findById(accountId))
                        .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                        .accountType(DOMESTIC)
                        .build())
                .getAccountNo();

        // SSAFY 금융 API 계좌 잔액 조회 요청
        InquireDemandDepositAccountBalanceRequestDTO inquireDemandDepositAccountBalanceRequestDTO = InquireDemandDepositAccountBalanceRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(inquireDemandDepositAccountBalance)
                        .userKey(accountHelperService.getAdminUserKeyByAccountId(accountId))
                        .build())
                .accountNo(accountNo)
                .build();
        InquireDemandDepositAccountBalanceResponseDTO inquireDemandDepositAccountBalanceResponseDTO = domesticClient.inquireDemandDepositAccountBalance(inquireDemandDepositAccountBalanceRequestDTO);

        return DomesticTravelAccountBalanceResponseDTO.builder()
                .accountBalance(inquireDemandDepositAccountBalanceResponseDTO.getRec().getAccountBalance())
                .build();
    }

    // 한화 여행통장 계좌 이체 서비스 로직
    public SsafyApiResponseDTO transferDomesticTravelAccount(Long accountId, TransferDomesticTravelAccountRequestDTO requestDTO) {

        ValidationUtil.validateInput(ValidateInputDTO.builder()
                .account(accountRepository.findById(accountId))
                .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                .accountType(DOMESTIC)
                .isPayable(true)
                .build());

        String depositTransactionSummary = getAccountName(requestDTO.getWithdrawalAccountNo()).getName();
        String withdrawalTransactionSummary = getAccountName(requestDTO.getDepositAccountNo()).getName();

        // SSAFY 금융 API 계좌 이체 요청
        UpdateDemandDepositAccountTransferRequestDTO updateDemandDepositAccountTransferRequestDTO = UpdateDemandDepositAccountTransferRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(updateDemandDepositAccountTransfer)
                        .userKey(accountHelperService.getAdminUserKeyByAccountId(accountId))
                        .build())
                .depositAccountNo(requestDTO.getDepositAccountNo())
                .depositTransactionSummary(depositTransactionSummary)
                .transactionBalance(requestDTO.getTransactionBalance())
                .withdrawalAccountNo(requestDTO.getWithdrawalAccountNo())
                .withdrawalTransactionSummary(withdrawalTransactionSummary)
                .build();
        UpdateDemandDepositAccountTransferResponseDTO updateDemandDepositAccountTransferResponseDTO = domesticClient.updateDemandDepositAccountTransfer(updateDemandDepositAccountTransferRequestDTO);

        Long depositAccountId = ValidationUtil.validateAccount(accountRepository.findByAccountNo(requestDTO.getDepositAccountNo())).getAccountId();
        Long withdrawalAccountId = ValidationUtil.validateAccount(accountRepository.findByAccountNo(requestDTO.getWithdrawalAccountNo())).getAccountId();

        // SSAFY 금융 API 거래내역 메모 요청 (출금 메모)
        TransactionMemoRequestDTO transactionWithdrawalMemoRequestDTO = TransactionMemoRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(transactionMemo)
                        .userKey(accountHelperService.getAdminUserKeyByAccountId(withdrawalAccountId))
                        .build())
                .accountNo(requestDTO.getWithdrawalAccountNo())
                .transactionUniqueNo(updateDemandDepositAccountTransferResponseDTO.getRec().get(0).getTransactionUniqueNo())
                .transactionMemo(String.valueOf(UserHeaderInterceptor.userId.get()))
                .build();
        memoClient.transactionMemo(transactionWithdrawalMemoRequestDTO);

        // SSAFY 금융 API 거래내역 메모 요청 (입금 메모)
        TransactionMemoRequestDTO transactionDepositMemoRequestDTO = TransactionMemoRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(transactionMemo)
                        .userKey(accountHelperService.getAdminUserKeyByAccountId(depositAccountId))
                        .build())
                .accountNo(requestDTO.getDepositAccountNo())
                .transactionUniqueNo(updateDemandDepositAccountTransferResponseDTO.getRec().get(1).getTransactionUniqueNo())
                .transactionMemo(String.valueOf(UserHeaderInterceptor.userId.get()))
                .build();
        memoClient.transactionMemo(transactionDepositMemoRequestDTO);

        // Notification 서버 입출금 시 알림 생성 요청
        NotificationRequestDTO notificationWithdrawalRequestDTO = NotificationRequestDTO.builder()
                .senderId(UserHeaderInterceptor.userId.get())
                .receiverIdList(List.of(UserHeaderInterceptor.userId.get()))
                .accountId(withdrawalAccountId)
                .notificationType(WITHDRAW)
                .amount(requestDTO.getTransactionBalance())
                .build();
        notificationClient.sendNotification(notificationWithdrawalRequestDTO);

        // Notification 서버 입출금 시 알림 생성 요청
        NotificationRequestDTO notificationDepositRequestDTO = NotificationRequestDTO.builder()
                .senderId(UserHeaderInterceptor.userId.get())
                .receiverIdList(accountHelperService.getAccountMemberIdList(depositAccountId))
                .accountId(depositAccountId)
                .notificationType(DEPOSIT)
                .amount(requestDTO.getTransactionBalance())
                .build();
        notificationClient.sendNotification(notificationDepositRequestDTO);

        return SsafyApiResponseDTOFactory.create(updateDemandDepositAccountTransferResponseDTO.getHeader());
    }

    // 한화 여행통장 계좌 이체 비밀번호 검증 서비스 로직
    public InternalServerSuccessResponseDTO verifyDomesticTravelAccountPassword(Long accountId, VerifyAccountPasswordRequestDTO requestDTO) {

        String savedPassword = ValidationUtil.validateInput(ValidateInputDTO.builder()
                        .account(accountRepository.findById(accountId))
                        .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                        .accountType(DOMESTIC)
                        .isPayable(true)
                        .build())
                .getPassword();

        if (!passwordEncoder.matches(requestDTO.getPassword() + PEPPER, savedPassword)) {
            throw InvalidPasswordException.getInstance();
        }

        return InternalServerSuccessResponseDTO.builder()
                .message("통장 비밀번호 검증 성공")
                .build();
    }

    // 한화 여행통장 멤버 목록 조회 서비스 로직 (민채)
    public DomesticTravelAccountMemberListResponseDTO getDomesticTravelAccountMemberList(Long accountId) {

        ValidationUtil.validateInput(ValidateInputDTO.builder()
                .account(accountRepository.findById(accountId))
                .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                .accountType(DOMESTIC)
                .build());

        List<UserAccountRelation> userAccountRelationList = ValidationUtil.validateUserAccountRelationList(userAccountRelationRepository.findAllByAccountId(accountId));
        List<Member> members = new ArrayList<>();

        // User 서버에 userId로 userName 조회하는 요청을 모든 멤버에 대해 보냄
        for (UserAccountRelation member : userAccountRelationList) {
            members.add(Member.builder()
                    .userId(member.getUserId())
                    .userName(userClient.getUserName(member.getUserId()).getUserName())
                    .role(member.getUserRole())
                    .build());
        }

        return DomesticTravelAccountMemberListResponseDTO.builder()
                .userId(UserHeaderInterceptor.userId.get())
                .memberCount((long) members.size())
                .members(members).build();
    }

    // 통장 권한 조회 서비스 로직
    public DomesticTravelAccountUserRoleResponseDTO getDomesticTravelAccountUserRole(Long accountId) {

        UserRole userRole = ValidationUtil.validateUserAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId)).getUserRole();

        return DomesticTravelAccountUserRoleResponseDTO.builder()
                .userRole(userRole)
                .build();
    }

    // 외화 여행통장 생성 서비스 로직
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CreateForeignTravelAccountResponseDTO createForeignTravelAccount(CreateForeignTravelAccountRequestDTO requestDTO) {

        Account verifiedAccount = ValidationUtil.validateInput(ValidateInputDTO.builder()
                    .account(accountRepository.findById(requestDTO.getDomesticAccountId()))
                    .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), requestDTO.getDomesticAccountId()))
                    .accountType(DOMESTIC)
                    .userRole(ADMIN)
                .build());

        // SSAFY 금융 API 계좌 생성 요청
        CreateForeignCurrencyDemandDepositAccountRequestDTO createForeignCurrencyDemandDepositAccountRequestDTO = CreateForeignCurrencyDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(createForeignCurrencyDemandDepositAccount)
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .accountTypeUniqueNo(FOREIGN_TRAVEL_ACCOUNT_TYPE_UNIQUE_NO)
                .currency(requestDTO.getCurrency())
                .build();
        CreateForeignCurrencyDemandDepositAccountResponseDTO createForeignCurrencyDemandDepositAccountResponseDTO = foreignClient.createForeignCurrencyDemandDepositAccount(createForeignCurrencyDemandDepositAccountRequestDTO);

        String accountNo = createForeignCurrencyDemandDepositAccountResponseDTO.getRec().getAccountNo();
        String currency = createForeignCurrencyDemandDepositAccountResponseDTO.getRec().getCurrency().getCurrency();

        Account savedAccount = null;
        UserAccountRelation savedUserAccountRelation = null;
        List<UserAccountRelation> savedUserAccountRelationList = new ArrayList<>();

        try {
            // Account 테이블에 저장
            Account account = Account.builder()
                    .accountNo(accountNo)
                    .password(verifiedAccount.getPassword())
                    .accountType(FOREIGN)
                    .build();
            savedAccount = accountRepository.save(account);

            // UserAccountRelation 테이블에 저장
            UserAccountRelation userAccountRelation = UserAccountRelation.builder()
                    .userId(UserHeaderInterceptor.userId.get())
                    .account(savedAccount)
                    .userRole(ADMIN)
                    .build();
            savedUserAccountRelation = userAccountRelationRepository.save(userAccountRelation);

            List<UserAccountRelation> userAccountRelationList = ValidationUtil.validateUserAccountRelationList(userAccountRelationRepository.findAllByAccountId(requestDTO.getDomesticAccountId()));

            for (UserAccountRelation u : userAccountRelationList) {
                if (u.getUserRole() == ADMIN) continue;

                // UserAccountRelation 테이블에 저장
                UserAccountRelation savedTempUserAccountRelation = userAccountRelationRepository.save(UserAccountRelation.builder()
                        .userId(u.getUserId())
                        .account(savedAccount)
                        .userRole(u.getUserRole())
                        .build());

                savedUserAccountRelationList.add(savedTempUserAccountRelation);
            }

            // Travel 서버에 외화계좌 생성시 외화여행계좌 테이블에 정보 저장 요청
            SaveForeignTravelAccountRequestDTO saveForeignTravelAccountRequestDTO = SaveForeignTravelAccountRequestDTO.builder()
                    .foreignAccountId(savedAccount.getAccountId())
                    .domesticAccountId(requestDTO.getDomesticAccountId())
                    .currency(currency)
                    .build();
            travelClient.saveForeignTravelAccount(saveForeignTravelAccountRequestDTO);

        } catch (ExternalServerErrorException e) {
            if (!savedUserAccountRelationList.isEmpty()) {
                userAccountRelationRepository.deleteAll(savedUserAccountRelationList);
            }

            if (savedUserAccountRelation != null) {
                userAccountRelationRepository.delete(savedUserAccountRelation);
            }

            if (savedAccount != null) {
                accountRepository.delete(savedAccount);
            }

            throw e;
        }

        return CreateForeignTravelAccountResponseDTO.builder()
                .domesticAccountId(verifiedAccount.getAccountId())
                .domesticAccountNo(verifiedAccount.getAccountNo())
                .foreignAccountId(savedAccount.getAccountId())
                .foreignAccountNo(savedAccount.getAccountNo())
                .build();
    }

    // 외화 여행통장 생성일 조회 서비스 로직
    public ForeignTravelAccountCreatedDateResponseDTO getForeignTravelAccountCreatedDate(Long accountId) {

        String accountNo = ValidationUtil.validateInput(ValidateInputDTO.builder()
                        .account(accountRepository.findById(accountId))
                        .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), accountId))
                        .accountType(FOREIGN)
                        .build())
                .getAccountNo();

        // SSAFY 금융 API 외화 계좌 조회 (단건) 요청
        InquireForeignCurrencyDemandDepositAccountRequestDTO inquireForeignCurrencyDemandDepositAccountRequestDTO = InquireForeignCurrencyDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(inquireForeignCurrencyDemandDepositAccount)
                        .userKey(accountHelperService.getAdminUserKeyByAccountId(accountId))
                        .build())
                .accountNo(accountNo)
                .build();
        InquireForeignCurrencyDemandDepositAccountResponseDTO inquireForeignCurrencyDemandDepositAccountResponseDTO = foreignClient.inquireForeignCurrencyDemandDepositAccount(inquireForeignCurrencyDemandDepositAccountRequestDTO);

        return ForeignTravelAccountCreatedDateResponseDTO.builder()
                .accountCreatedDate(inquireForeignCurrencyDemandDepositAccountResponseDTO.getRec().getAccountCreatedDate())
                .build();
    }

    // 외화 여행통장 및 연결된 한화 여행통장 식별자와 계좌번호 조회 서비스 로직
    public TravelAccountCoupleResponseDTO getTravelAccountCoupleResponseDTO(Long foreignAccountId) {

        String foreignAccountNo = ValidationUtil.validateInput(ValidateInputDTO.builder()
                    .account(accountRepository.findById(foreignAccountId))
                    .userAccountRelation(userAccountRelationRepository.findByUserIdAndAccountId(UserHeaderInterceptor.userId.get(), foreignAccountId))
                    .accountType(FOREIGN)
                    .build())
                .getAccountNo();

        // Travel 서버에 외화 여행통장 ID로 한화 여행통장 ID 반환 요청
        Long domesticAccountId = travelClient.getParentAccountId(foreignAccountId).getParentAccountId();

        String domesticAccountNo = ValidationUtil.validateAccount(accountRepository.findById(domesticAccountId)).getAccountNo();

        return TravelAccountCoupleResponseDTO.builder()
                .domesticAccountId(domesticAccountId)
                .domesticAccountNo(domesticAccountNo)
                .foreignAccountId(foreignAccountId)
                .foreignAccountNo(foreignAccountNo)
                .build();
    }

    // 통장 주인 이름 조회 서비스 로직
    public AccountAdminNameResponseDTO getAccountName(String accountNo) {

        Long accountId = ValidationUtil.validateAccount(accountRepository.findByAccountNo(accountNo)).getAccountId();

        List<UserAccountRelation> userAccountRelationList = ValidationUtil.validateUserAccountRelationList(userAccountRelationRepository.findAllByAccountId(accountId));

        Long userId = userAccountRelationList.stream()
                .filter(relation -> relation.getUserRole() == ADMIN)
                .map(UserAccountRelation::getUserId)
                .findFirst()
                .orElseThrow(UserAccountRelationNotFoundException::getInstance);

        AccountType accountType = ValidationUtil.validateAccount(accountRepository.findById(accountId)).getAccountType();

        if (accountType == PERSONAL) {
            return AccountAdminNameResponseDTO.builder()
                    .name(userClient.getUserName(userId).getUserName())
                    .build();
        } else if (accountType == DOMESTIC) {
            return AccountAdminNameResponseDTO.builder()
                    .name(travelClient.getDomesticTravelAccountInfo(accountId).getAccountName())
                    .build();
        } else if (accountType == FOREIGN) {
            Long parentAccountId = travelClient.getParentAccountId(userId).getParentAccountId();

            return AccountAdminNameResponseDTO.builder()
                    .name(travelClient.getDomesticTravelAccountInfo(parentAccountId).getAccountName())
                    .build();
        } else {
            throw UserAccountRelationNotFoundException.getInstance();
        }
    }

}
