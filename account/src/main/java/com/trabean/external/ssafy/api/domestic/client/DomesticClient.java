package com.trabean.external.ssafy.api.domestic.client;

import com.trabean.config.FeignClientConfig;
import com.trabean.external.ssafy.api.domestic.dto.request.*;
import com.trabean.external.ssafy.api.domestic.dto.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * SSAFY 금융 API p.29 ~ p.71 수시입출금 관련 요청 처리 클라이언트
 */
@FeignClient(name = "domesticClient", url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit", configuration = FeignClientConfig.class)
public interface DomesticClient {

    /**
     * SSAFY 금융 API p.35 - 계좌 생성
     */
    @PostMapping("/createDemandDepositAccount")
    CreateDemandDepositAccountResponseDTO createDemandDepositAccount(@RequestBody CreateDemandDepositAccountRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.38 - 계좌 목록 조회
     */
    @PostMapping("/inquireDemandDepositAccountList")
    InquireDemandDepositAccountListResponseDTO inquireDemandDepositAccountList(@RequestBody InquireDemandDepositAccountListRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.42 - 계좌 조회 (단건)
     */
    @PostMapping("/inquireDemandDepositAccount")
    InquireDemandDepositAccountResponseDTO inquireDemandDepositAccount(@RequestBody InquireDemandDepositAccountRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.45 - 예금주 조회
     */
    @PostMapping("/inquireDemandDepositAccountHolderName")
    InquireDemandDepositAccountHolderNameResponseDTO inquireDemandDepositAccountHolderName(@RequestBody InquireDemandDepositAccountHolderNameRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.47 - 계좌 잔액 조회
     */
    @PostMapping("/inquireDemandDepositAccountBalance")
    InquireDemandDepositAccountBalanceResponseDTO inquireDemandDepositAccountBalance(@RequestBody InquireDemandDepositAccountBalanceRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.50 - 계좌 출금
     */
    @PostMapping("/updateDemandDepositAccountWithdrawal")
    UpdateDemandDepositAccountWithdrawalResponseDTO updateDemandDepositAccountWithdrawal(@RequestBody UpdateDemandDepositAccountWithdrawalRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.53 - 계좌 입금
     */
    @PostMapping("/updateDemandDepositAccountDeposit")
    UpdateDemandDepositAccountDepositResponseDTO updateDemandDepositAccountDeposit(@RequestBody UpdateDemandDepositAccountDepositRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.56 - 계좌 이체
     */
    @PostMapping("/updateDemandDepositAccountTransfer")
    UpdateDemandDepositAccountTransferResponseDTO updateDemandDepositAccountTransfer(@RequestBody UpdateDemandDepositAccountTransferRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.59 - 계좌 이체 한도 변경
     */
    @PostMapping("/updateTransferLimit")
    UpdateTransferLimitResponseDTO updateTransferLimit(@RequestBody UpdateTransferLimitRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.62 - 계좌 거래 내역 조회
     */
    @PostMapping("/inquireTransactionHistoryList")
    InquireTransactionHistoryListResponseDTO inquireTransactionHistoryList(@RequestBody InquireTransactionHistoryListRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.66 - 계좌 거래 내역 조회 (단건)
     */
    @PostMapping("/inquireTransactionHistory")
    InquireTransactionHistoryResponseDTO inquireTransactionHistory(@RequestBody InquireTransactionHistoryRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.69 - 계좌 해지
     */
    @PostMapping("/deleteDemandDepositAccount")
    DeleteDemandDepositAccountResponseDTO deleteDemandDepositAccount(@RequestBody DeleteDemandDepositAccountRequestDTO requestDTO);
}
