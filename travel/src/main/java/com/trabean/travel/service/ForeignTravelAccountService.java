package com.trabean.travel.service;

import static com.trabean.util.CurrencyUtils.changeCurrency;

import com.trabean.travel.callApi.client.AccountClient;
import com.trabean.travel.callApi.client.ForeignCurrencyClient;
import com.trabean.travel.callApi.dto.request.AccountHistoryRequestDto;
import com.trabean.travel.callApi.dto.request.GetAccountNumberRequestDto;
import com.trabean.travel.callApi.dto.response.AccountHistoryResponseDto;
import com.trabean.travel.callApi.dto.response.GetAccountNumberResponseDto;
import com.trabean.travel.dto.request.ForeignAccountHistoryRequestDto;
import com.trabean.travel.dto.request.SaveForeignAccountRequestDto;
import com.trabean.travel.dto.response.AccountHistoryDetail;
import com.trabean.travel.dto.response.ForeignAccountHistoryResponseDto;
import com.trabean.travel.entity.ForeignTravelAccount;
import com.trabean.travel.entity.KrwTravelAccount;
import com.trabean.travel.repository.ForeignTravelAccountRepository;
import com.trabean.travel.repository.KrwTravelAccountRepository;
import com.trabean.util.RequestHeader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForeignTravelAccountService {

    private final ForeignTravelAccountRepository foreignTravelAccountRepository;
    private final KrwTravelAccountRepository krwTravelAccountRepository;

    private final AccountClient accountClient;
    private final ForeignCurrencyClient foreignCurrencyClient;

    private final CommonAccountService commonAccountService;

    @Value("${api.userKey}")
    private String userKey;

    @Transactional
    public void save(SaveForeignAccountRequestDto saveForeignAccountRequestDto) {
        Long parentAccountId = saveForeignAccountRequestDto.getParentAccountId();
        KrwTravelAccount parentAccount = krwTravelAccountRepository.findByAccountId(parentAccountId);

        ForeignTravelAccount foreignTravelAccount = new ForeignTravelAccount();
        foreignTravelAccount.setAccountId(saveForeignAccountRequestDto.getAccountId());
        foreignTravelAccount.setExchangeCurrency(saveForeignAccountRequestDto.getExchangeCurrency());
        foreignTravelAccount.setParentAccount(parentAccount);
        foreignTravelAccountRepository.save(foreignTravelAccount);
    }

    public ForeignAccountHistoryResponseDto findForeignAccountHistory(
            ForeignAccountHistoryRequestDto foreignAccountHistoryRequestDto) {
        Long accountId = foreignAccountHistoryRequestDto.getAccountId();

        // 화폐단위 조회
        String exchangeCurrency = foreignTravelAccountRepository.findByAccountId(accountId).getExchangeCurrency();
        String country = changeCurrency(exchangeCurrency);

        // 계좌번호 조회
        GetAccountNumberResponseDto getAccountNumberResponseDto = accountClient.getAccount(
                new GetAccountNumberRequestDto(accountId));
        String accountNo = getAccountNumberResponseDto.getAccountNo();

        // 잔액조회
        Double accountBalance = commonAccountService.getForeignAccountBalance(accountNo);

        // 외화 계좌 거래 내역 조회
        AccountHistoryRequestDto accountHistoryRequestDto =
                new AccountHistoryRequestDto(
                        RequestHeader.builder()
                                .apiName("inquireForeignCurrencyTransactionHistoryList")
                                .userKey(userKey)
                                .build(),
                        accountNo,
                        foreignAccountHistoryRequestDto.getStartDate(),
                        foreignAccountHistoryRequestDto.getEndDate(),
                        foreignAccountHistoryRequestDto.getTransactionType(),
                        "ASC");

        AccountHistoryResponseDto accountHistoryResponseDto = foreignCurrencyClient.getForeignAccountHistoryList(
                accountHistoryRequestDto);

        String totalCount = accountHistoryResponseDto.getRec().getTotalCount();
        List<AccountHistoryDetail> list = accountHistoryResponseDto.getRec().getList();

        return new ForeignAccountHistoryResponseDto(
                country, exchangeCurrency, accountBalance, totalCount, list);
    }


}
