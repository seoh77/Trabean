package com.trabean.travel.service;

import com.trabean.travel.callApi.client.AccountClient;
import com.trabean.travel.callApi.client.ForeignCurrencyClient;
import com.trabean.travel.callApi.dto.request.GetAccountBalanceRequestDto;
import com.trabean.travel.callApi.dto.request.GetAccountNumberRequestDto;
import com.trabean.travel.callApi.dto.response.GetAccountBalanceResponseDto;
import com.trabean.travel.callApi.dto.response.GetAccountNumberResponseDto;
import com.trabean.util.RequestHeader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonAccountService {

    private final AccountClient accountClient;
    private final ForeignCurrencyClient foreignCurrencyClient;

    @Value("${api.userKey}")
    private String userKey;

    /**
     * 계좌번호 조회
     */
    public String getAccountNo(Long accountId) {
        GetAccountNumberResponseDto getAccountNumberResponseDto = accountClient.getAccount(
                new GetAccountNumberRequestDto(accountId));
        return getAccountNumberResponseDto.getAccountNo();
    }

    /**
     * 외화 계좌 잔액 조회
     */
    public Double getForeignAccountBalance(String accountNo) {
        GetAccountBalanceRequestDto getAccountBalanceRequestDto = new GetAccountBalanceRequestDto(
                RequestHeader.builder()
                        .apiName("inquireForeignCurrencyDemandDepositAccountBalance")
                        .userKey(userKey)
                        .build(),
                accountNo);

        GetAccountBalanceResponseDto getAccountBalanceResponseDto
                = foreignCurrencyClient.getForeignAccountBalance(getAccountBalanceRequestDto);

        return (double) getAccountBalanceResponseDto.getRec().getAccountBalance();
    }

}
