package com.trabean.payment.service;

import com.trabean.payment.dto.request.ExchangeRateRequest;
import com.trabean.payment.dto.request.Header;
import com.trabean.payment.dto.response.ExchangeRateResponse;
import com.trabean.payment.exception.PaymentsException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);

    @Value("https://finopenapi.ssafy.io/ssafy/api/v1/edu/exchangeRate/search") // 환율조회 api
    private String exchangeUrl;

    public Long calculateKrw(String currency, Double foreignAmount) {
        // 원화를 넣을 경우 그대로 return
        if (currency.equals("KRW")) {
            return foreignAmount.longValue();
        }

        // ExchangeRateRequest 생성
        ExchangeRateRequest exchangeRateRequest = new ExchangeRateRequest(
                Header.builder().apiName("exchangeRate").build(),
                currency
        );

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 생성 (요청 본문과 헤더 포함)
        HttpEntity<ExchangeRateRequest> entity = new HttpEntity<>(exchangeRateRequest, headers);

        try {
            // API 호출: POST 요청으로 데이터 전송
            ExchangeRateResponse response = restTemplate.postForObject(exchangeUrl, entity, ExchangeRateResponse.class);

            // 결과 로그 남기기 (환율 정보)
            if (response != null && response.getRec() != null) {
                logger.info("Exchange rate: {}", response.getRec().getExchangeRate());
            } else {
                logger.warn("Failed to retrieve exchange rate information");
            }

            // 계산 후 리턴
            return (Long) (long) (Double.parseDouble(Objects.requireNonNull(response).getRec().getExchangeRate())
                    * foreignAmount);

        } catch (RestClientException e) {
            logger.error("환율 조회 API 호출 중 오류 발생: {}", e.getMessage());
            throw new PaymentsException("환율 조회 API 호출 중 오류 발생" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
