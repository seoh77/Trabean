package com.trabean.travel.service;

import com.trabean.travel.callApi.client.ExchangeClient;
import com.trabean.travel.callApi.client.ForeignCurrencyClient;
import com.trabean.travel.callApi.dto.request.DepositForeignAccountApiRequestDto;
import com.trabean.travel.callApi.dto.request.ExchangeApiRequestDto;
import com.trabean.travel.callApi.dto.request.ExchangeEstimateApiRequestDto;
import com.trabean.travel.callApi.dto.response.ExchangeApiResponseDto;
import com.trabean.travel.callApi.dto.response.ExchangeEstimateApiResponseDto;
import com.trabean.travel.callApi.dto.response.ExchangeEstimateApiResponseDto.ApiCurrencyDetail;
import com.trabean.travel.dto.request.ExchangeEstimateRequestDto;
import com.trabean.travel.dto.request.ExchangeRequestDto;
import com.trabean.travel.dto.response.ExchangeEstimateResponseDto;
import com.trabean.travel.dto.response.ExchangeEstimateResponseDto.CurrencyDetail;
import com.trabean.travel.dto.response.ExchangeResponseDto;
import com.trabean.travel.dto.response.ExchangeResponseDto.AccountInfo;
import com.trabean.travel.dto.response.ExchangeResponseDto.ExchangeCurrency;
import com.trabean.util.CurrencyUtils;
import com.trabean.util.RequestHeader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeClient exchangeClient;
    private final ForeignCurrencyClient foreignCurrencyClient;

    private String userKey = "9e10349e-91e9-474d-afb4-564b24178d9f";

    public ExchangeEstimateResponseDto exchangeEstimate(ExchangeEstimateRequestDto requestDto) {
        ExchangeEstimateApiRequestDto exchangeEstimateApiRequestDto
                = new ExchangeEstimateApiRequestDto(
                RequestHeader.builder()
                        .apiName("estimate")
                        .userKey(userKey)
                        .build(),
                requestDto.getCurrency(),
                requestDto.getExchangeCurrency(),
                requestDto.getAmount()
        );

        ExchangeEstimateApiResponseDto exchangeEstimateApiResponseDto = exchangeClient.getExchangeEstimate(
                exchangeEstimateApiRequestDto);

        ApiCurrencyDetail returnCurrency = exchangeEstimateApiResponseDto.getRec().getCurrency();
        ApiCurrencyDetail returnExchangeCurrency = exchangeEstimateApiResponseDto.getRec().getExchangeCurrency();

        CurrencyDetail currency = new CurrencyDetail(returnCurrency.getAmount(),
                CurrencyUtils.changeCurrency(returnCurrency.getCurrency()),
                returnCurrency.getCurrency());

        CurrencyDetail exchangeCurrency = new CurrencyDetail(returnExchangeCurrency.getAmount(),
                CurrencyUtils.changeCurrency(returnExchangeCurrency.getCurrency()),
                returnExchangeCurrency.getCurrency());

        return new ExchangeEstimateResponseDto(currency, exchangeCurrency);
    }

    public ExchangeResponseDto exchange(ExchangeRequestDto requestDto) {
        ExchangeApiRequestDto exchangeApiRequestDto = new ExchangeApiRequestDto(
                RequestHeader.builder()
                        .apiName("exchange")
                        .userKey(userKey)
                        .build(),
                requestDto.getWithdrawalAccountNo(),
                requestDto.getExchangeCurrency(),
                requestDto.getExchangeAmount()
        );

        ExchangeApiResponseDto exchangeApiResponseDto = exchangeClient.exchange(exchangeApiRequestDto);

        ExchangeCurrency exchangeCurrency = exchangeApiResponseDto.getRec().getExchangeCurrency();
        AccountInfo accountInfo = exchangeApiResponseDto.getRec().getAccountInfo();
        
        // 외화통장에 입금
        String amount = exchangeCurrency.getAmount();
        String depositAccountNo = requestDto.getDepositAccountNo();

        DepositForeignAccountApiRequestDto depositForeignAccountApiRequestDto = new DepositForeignAccountApiRequestDto(
                RequestHeader.builder()
                        .apiName("updateForeignCurrencyDemandDepositAccountDeposit")
                        .userKey(userKey)
                        .build(),
                depositAccountNo,
                amount,
                "환전"
        );

        foreignCurrencyClient.updateForeignAccountDeposit(depositForeignAccountApiRequestDto);

        return new ExchangeResponseDto(exchangeCurrency, accountInfo);
    }

}
