package com.trabean.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trabean.payment.dto.request.ExchangeRateRequest;
import com.trabean.payment.dto.request.ExchangeRateRequest.Header;
import com.trabean.payment.dto.response.ExchangeRateResponse;
import com.trabean.payment.repository.MerchantsRepository;
import com.trabean.payment.repository.PaymentsRepository;
import com.trabean.payment.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

    @Value("${external.api.paymentForeignAccountUrl}") // 결제 메인계좌 -> 그에 맞는 외화 계좌id
    private String paymentForeignAccountUrl;

    @Value("${external.api.accountNumberUrl}") // 계좌 id -> 계좌번호
    private String accountNumberUrl;

    @Value("${external.api.exchangeUrl}") // 환율조회 api
    private String exchangeUrl;

    @Value("${external.key.ssafyApiKey}")
    private String ssafyApiKey;

    public void searchExchangeRate(String currency) {
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
        System.out.println("Currency: " + exchangeRateRequest.getCurrency());
        System.out.println("Header API Name: " + exchangeRateRequest.getHeader().getApiName());
        System.out.println("Header Transmission Date: " + exchangeRateRequest.getHeader().getTransmissionDate());
        System.out.println(
                "Header Transmission unique Code: " + exchangeRateRequest.getHeader()
                        .getInstitutionTransactionUniqueNo());
        System.out.println("Header API Key: " + exchangeRateRequest.getHeader().getApiKey());

        // 요청 데이터 로그로 출력
        logger.info("Sending request: {}", exchangeRateRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonBody = objectMapper.writeValueAsString(exchangeRateRequest);
            System.out.println("Body 변환 JSON으로: " + jsonBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        System.out.println(headers);
        // HttpEntity 생성 (요청 본문과 헤더 포함)
        HttpEntity<ExchangeRateRequest> entity = new HttpEntity<>(exchangeRateRequest, headers);
        System.out.println(entity);
        try {
            // API 호출: POST 요청으로 데이터 전송
            restTemplate.postForObject(exchangeUrl, entity, ExchangeRateResponse.class);
            // 결과 출력 (환율 정보)
//            if (response != null && response.getREC() != null) {
//                logger.info("Exchange rate: {}", response.getREC().getExchangeRate());
//            } else {
//                logger.warn("Failed to retrieve exchange rate information");
//            }

            // 반환값 리턴
//            return response;
        } catch (RestClientException e) {
            // 외부 API 호출 중 오류 발생 시 처리
            logger.error("API 호출 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    // 다른 서비스나 컨트롤러에서 searchExchangeRate 메서드를 호출하는 방식으로 변경해야 함
//    public void exampleUsage() {
//        // 여기서 searchExchangeRate를 호출
//        ExchangeRateResponse exchangeRateResponse = searchExchangeRate("USD");
//        // 추가적인 처리 로직
//    }
}
