package com.trabean.payment.service;

import com.trabean.payment.dto.request.ExchangeRateRequest;
import com.trabean.payment.dto.request.ExchangeRateRequest.Header;
import com.trabean.payment.dto.response.ExchangeRateResponse;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.MerchantsRepository;
import com.trabean.payment.repository.PaymentsRepository;
import com.trabean.payment.util.DateTimeUtil;
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
public class PaymentsRequestService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsRequestService.class);

    private final PaymentsRepository paymentsRepository;
    private final MerchantsRepository merchantsRepository;
    private final RestTemplate restTemplate;

    @Value("https://0bae4fd6-f141-476f-8cd5-835280aea133.mock.pstmn.io") // 결제 메인계좌 -> 그에 맞는 외화 계좌id
    private String paymentForeignAccountUrl;

    @Value("https://96589213-98b8-4de4-b669-051c560919ea.mock.pstmn.io") // 계좌 id -> 계좌번호
    private String accountNumberUrl;

    @Value("https://finopenapi.ssafy.io/ssafy/api/v1/edu/exchangeRate/search") // 환율조회 api
    private String exchangeUrl;

    @Value("${API_KEY}")
    private String ssafyApiKey;

    public ExchangeRateResponse searchExchangeRate(String currency) {
        // ExchangeRateRequest 생성
        ExchangeRateRequest exchangeRateRequest = new ExchangeRateRequest(
                new Header(
                        "exchangeRateSearch",
                        DateTimeUtil.getTransmissionDate(),
                        DateTimeUtil.getTransmissionTime(),
                        "00100",
                        "001",
                        "exchangeRateSearch",
                        DateTimeUtil.generateUniqueNumber(),
                        ssafyApiKey
                ),
                currency
        );

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 생성 (요청 본문과 헤더 포함)
        HttpEntity<ExchangeRateRequest> entity = new HttpEntity<>(exchangeRateRequest, headers);

        try {
            // API 호출: POST 요청으로 데이터 전송
            return restTemplate.postForObject(exchangeUrl, entity, ExchangeRateResponse.class);
        } catch (RestClientException e) {
            // 외부 API 호출 중 오류 발생 시 처리
            logger.error("API 호출 중 오류 발생: {}", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            throw new PaymentsException("API 호출 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
