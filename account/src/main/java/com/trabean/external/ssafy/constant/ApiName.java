package com.trabean.external.ssafy.constant;

/**
 * SSAFY 금융 API Name
 */
public enum ApiName {

    /**
     * SSAFY 금융 API p.35 - 계좌 생성
     */
    createDemandDepositAccount,

    /**
     * SSAFY 금융 API p.38 - 계좌 목록 조회
     */
    inquireDemandDepositAccountList,

    /**
     * SSAFY 금융 API p.42 - 계좌 조회 (단건)
     */
    inquireDemandDepositAccount,

    /**
     * SSAFY 금융 API p.45 - 예금주 조회
     */
    inquireDemandDepositAccountHolderName,

    /**
     * SSAFY 금융 API p.47 - 계좌 잔액 조회
     */
    inquireDemandDepositAccountBalance,

    /**
     * SSAFY 금융 API p.50 - 계좌 출금
     */
    updateDemandDepositAccountWithdrawal,

    /**
     * SSAFY 금융 API p.53 - 계좌 입금
     */
    updateDemandDepositAccountDeposit,

    /**
     * SSAFY 금융 API p.56 - 계좌 이체
     */
    updateDemandDepositAccountTransfer,

    /**
     * SSAFY 금융 API p.59 - 계좌 이체 한도 변경
     */
    updateTransferLimit,

    /**
     * SSAFY 금융 API p.62 - 계좌 거래 내역 조회
     */
    inquireTransactionHistoryList,

    /**
     * SSAFY 금융 API p.66 - 계좌 거래 내역 조회 (단건)
     */
    inquireTransactionHistory,

    /**
     * SSAFY 금융 API p.69 - 계좌 해지
     */
    deleteDemandDepositAccount,

    /**
     * SSAFY 금융 API p.202 - 1원 송금
     */
    openAccountAuth,

    /**
     * SSAFY 금융 API p.205 - 1원 송금 검증
     */
    checkAuthCode,

    /**
     * SSAFY 금융 API p.228 - 외화 계좌 생성
     */
    createForeignCurrencyDemandDepositAccount,

    /**
     * SSAFY 금융 API p.235 - 외화 계좌 조회 (단건)
     */
    inquireForeignCurrencyDemandDepositAccount,

    /**
     * SSAFY 금융 API p.266 - 거래내역 메모
     */
    transactionMemo
}
