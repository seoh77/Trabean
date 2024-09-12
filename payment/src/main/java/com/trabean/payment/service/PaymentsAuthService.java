package com.trabean.payment.service;

import com.trabean.payment.dto.response.PaymentsAuthResponse;
import com.trabean.payment.dto.response.UserRoleResponse;
import com.trabean.payment.entity.Payments;
import com.trabean.payment.enums.UserRole;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.PaymentsRepository;
import java.text.SimpleDateFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentsAuthService {

    private final PaymentsRepository paymentsRepository;
    private final RestTemplate restTemplate;
    
    @Value("${external.api.userRoleUrl}")
    private String userRoleUrl;

    public PaymentsAuthResponse processPayment(String userKey, Long accountId) {
        // 유저 권한 확인 API 호출
        String requestBody = String.format("{\"userKey\":\"%s\", \"accountId\":%d}", userKey, accountId);
        HttpEntity<String> entity = new HttpEntity<>(requestBody);

        UserRoleResponse userRoleResponse;

        try {
            // API 호출
            userRoleResponse = restTemplate.postForObject(userRoleUrl, entity, UserRoleResponse.class);
        } catch (RestClientException e) {
            // RestTemplate에서 발생한 예외 처리
            throw new PaymentsException("외부 API 호출 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 유저 권한 확인 후 처리
        if (userRoleResponse == null) {
            throw new PaymentsException("권한을 받아오지 못 했습니다.", HttpStatus.BAD_REQUEST);
        }
        if (userRoleResponse.getUserRole() == UserRole.NONE_PAYER) {
            throw new PaymentsException("권한이 없는 사용자입니다.", HttpStatus.FORBIDDEN);
        }

        // 결제 처리 로직 실행
        Payments payment = paymentsRepository.save(
                Payments.createInitialPayment(userRoleResponse.getUserId(), accountId)
        );

        // 날짜 포맷팅
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(payment.getPaymentDate());

        // PaymentsAuthResponse로 반환
        return new PaymentsAuthResponse(
                payment.getTransactionId(),
                payment.getPayId(),
                formattedDate
        );
    }
}
