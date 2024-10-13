package com.trabean.payment.service;

import com.trabean.payment.client.ssafy.ExchangeClient;
import com.trabean.payment.dto.request.ExchangeRateRequest;
import com.trabean.payment.dto.request.Header;
import com.trabean.payment.dto.response.ExchangeRateResponse;
import com.trabean.payment.util.ApiName;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeClient exchangeClient;
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);

    public Long calculateKrw(String currency, Double foreignAmount) {
        // 원화를 넣을 경우 그대로 return
        if (currency.equals("KRW")) {
            return foreignAmount.longValue();
        }

        // ExchangeRateRequest 생성
        ExchangeRateRequest exchangeRateRequest = new ExchangeRateRequest(
                Header.builder().apiName(ApiName.EXCHANGE_RATE).build(),
                currency
        );
        logger.info("ExchangeRateRequest: {}", exchangeRateRequest.getHeader().toString());

        // API 호출: POST 요청으로 데이터 전송
        ExchangeRateResponse response = exchangeClient.getExchangeRate(exchangeRateRequest);

        // 결과 로그 남기기 (환율 정보)
        if (response != null && response.getRec() != null) {
            logger.info("Exchange rate: {}", response.getRec().getExchangeRate());
        } else {
            logger.warn("Failed to retrieve exchange rate information");
        }

        // Response 에서 환율 정보를 가져옴
        String exchangeRateString = Objects.requireNonNull(response).getRec().getExchangeRate().replace(",", "");
        // 문자열을 double 로 변환
        double exchangeRate = Double.parseDouble(exchangeRateString);
        logger.info(exchangeRateString + " 환율로 계산 -> " + Math.round(exchangeRate * foreignAmount));

        // 엔화일 경우의 계산 방식 (원화 100원당 외화 계산)
        if (response.getRec().getCurrency().equals("JPY")) {
            // 엔화는 100원당 외화 환율로 계산, foreignAmount를 100으로 나눔
            return Math.round(exchangeRate * foreignAmount / 100);
        } else {
            // 다른 통화는 일반적인 방식으로 계산
            return Math.round(exchangeRate * foreignAmount);
        }
    }
}
