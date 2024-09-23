package com.trabean.payment.service;

import com.trabean.payment.dto.request.BalanceRequest;
import com.trabean.payment.dto.request.Header;
import com.trabean.payment.dto.request.RequestPaymentRequest;
import com.trabean.payment.dto.response.BalanceResponse;
import com.trabean.payment.entity.Merchants;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.MerchantsRepository;
import com.trabean.payment.repository.PaymentsRepository;
import com.trabean.payment.util.ApiName;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentsRequestService {


    private final PaymentsRepository paymentsRepository;
    private final MerchantsRepository merchantsRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(PaymentsRequestService.class);

    // 유저 키 임시 설정
    @Value("${external.key.userKey}")
    private String userKey;

//    @Value("${external.api.paymentForeignAccountUrl}") // 결제 메인계좌 -> 그에 맞는 외화 계좌id
//    private String paymentForeignAccountUrl;

    @Value("http://j11a604.p.ssafy.io:8081/api/accounts/get-account-number") // 계좌 id -> 계좌번호
    private String accountNumberUrl;

    // 한국 계좌 잔액 조회
    @Value("https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/inquireDemandDepositAccountBalance")
    private String krwBalanceUrl;

    // 계좌 번호 조회
    public String getAccountNumber(Long accountId) throws PaymentsException {
        // HttpHeaders에 Content-Type을 application/json으로 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.APPLICATION_JSON);
        String requestBody = String.format("{\"accountId\":\"%s\"}", accountId);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            // API 호출
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    accountNumberUrl,        // URL
                    HttpMethod.POST,         // HTTP 메서드
                    entity,                  // 요청 본문
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );

            // 응답 처리
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    String message = (String) responseBody.get("message");
                    return (String) responseBody.get("accountNo");
                }
            } else {
                throw new PaymentsException("계좌 번호 조회 실패: " + response.getStatusCode(), HttpStatus.BAD_REQUEST);
            }

        } catch (RestClientException e) {
            // RestTemplate에서 발생한 예외 처리
            throw new PaymentsException("계좌 번호 조회 외부 API 호출 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return requestBody;
    }

    // 잔액 조회 후 검증
    public void validateAmount(Long accountId, RequestPaymentRequest requestPaymentRequest) {
        String apiType;
        String accountNo;

        // Merchant 정보 조회
        Merchants merchant = merchantsRepository.findById(requestPaymentRequest.getMerchantId())
                .orElseThrow(() -> new PaymentsException("잘못된 merchantId값 입니다.", HttpStatus.NOT_FOUND));

        if (merchant.getExchangeCurrency().equals("KRW")) {
            apiType = ApiName.KRW_BALANCE;
        } else {
            apiType = ApiName.FOREIGN_BALANCE;
        }

        accountNo = getAccountNumber(accountId);

        // BalanceRequest 생성
        BalanceRequest balanceRequest = new BalanceRequest(
                Header.builder().apiName(apiType).userKey(userKey).build(),
                accountNo
        );

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 생성 (요청 본문과 헤더 포함)
        HttpEntity<BalanceRequest> entity = new HttpEntity<>(balanceRequest, headers);
        logger.info("계좌 잔액 조회 요청: {}", entity.getBody().getHeader().toString());
        logger.info("계좌 잔액 조회 요청: {}", entity.getBody().getAccountNo());

        try {
            // API 호출: POST 요청으로 데이터 전송
            BalanceResponse response = restTemplate.postForObject(krwBalanceUrl, entity, BalanceResponse.class);

            // 결과 로그 남기기
            if (response != null && response.getRec() != null) {
                logger.info("계좌 잔액 조회: {}", response.getRec().getAccountBalance());

                if (response.getRec().getAccountBalance() < requestPaymentRequest.getForeignAmount()) {
                    logger.info("계좌 잔액 부족");
                    throw new PaymentsException("계좌 잔액이 부족합니다. 현재 잔액: " + response.getRec().getAccountBalance(),
                            HttpStatus.BAD_REQUEST);
                } else {
                    logger.info("계좌 출금 가능");
                }
            } else {
                logger.warn("계좌 잔액 조회 실패");
            }
        } catch (RestClientException e) {
            logger.error("계좌 잔액 조회 API 호출 중 오류 발생: {}", e.getMessage());
            throw new PaymentsException("계좌 잔액 조회 API 호출 중 오류 발생" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
