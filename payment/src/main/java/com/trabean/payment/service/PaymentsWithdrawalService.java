package com.trabean.payment.service;

import com.trabean.payment.dto.request.Header;
import com.trabean.payment.dto.request.RequestPaymentRequest;
import com.trabean.payment.dto.request.WithdrawalRequest;
import com.trabean.payment.dto.response.WithdrawalResponse;
import com.trabean.payment.entity.Merchants;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.MerchantsRepository;
import com.trabean.payment.util.ApiName;
import jakarta.transaction.Transactional;
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
@Transactional
public class PaymentsWithdrawalService {

    private final RestTemplate restTemplate;
    private final MerchantsRepository merchantsRepository;
    private final PaymentsAccountService paymentsAccountService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentsWithdrawalService.class);

    @Value("https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/updateDemandDepositAccountWithdrawal")
    private String withdrawalKrwUrl;

    // 유저 키 임시 설정
    @Value("${external.key.userKey}")
    private String userKey;

    public void withdrawaltoPay(RequestPaymentRequest request, Long accountId) {
        // Merchant 정보 조회
        Merchants merchant = merchantsRepository.findById(request.getMerchantId())
                .orElseThrow(() -> new PaymentsException("잘못된 merchantId값 입니다.", HttpStatus.NOT_FOUND));

        // 계좌 정보 조회
        String accountNo = paymentsAccountService.getAccountNumber(accountId);

        // 한화 출금일 경우
        if (merchant.getExchangeCurrency().equals("KRW")) {

            WithdrawalRequest withdrawalRequest = new WithdrawalRequest(
                    Header.builder().apiName(ApiName.KRW_WITHDRAW).userKey(userKey).build(),
                    accountNo, (long) request.getForeignAmount().doubleValue(), "(수시 입출금): 출금"
            );

            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HttpEntity 생성 (요청 본문과 헤더 포함)
            HttpEntity<WithdrawalRequest> entity = new HttpEntity<>(withdrawalRequest, headers);
            logger.info("한국 계좌 출금 요청: {}", entity.getBody().getHeader().toString());
            logger.info("한국 계좌 출금 요청: {}", entity.getBody().getAccountNo());

            try {
                // API 호출: POST 요청으로 데이터 전송
                WithdrawalResponse response = restTemplate.postForObject(withdrawalKrwUrl, entity,
                        WithdrawalResponse.class);

                // 결과 로그 남기기
                if (response != null && response.getRec() != null) {
                    logger.info("한화 출금 성공: {}", response.getRec().getTransactionDate());
                } else {
                    logger.warn("한화 출금 return 값이 null 입니다.");
                }
            } catch (RestClientException e) {
                logger.error("한화 출금 API 호출 중 오류 발생: {}", e.getMessage());
                throw new PaymentsException("한화 출금 API 호출 중 오류 발생" + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
