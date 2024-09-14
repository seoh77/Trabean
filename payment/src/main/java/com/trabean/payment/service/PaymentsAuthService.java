package com.trabean.payment.service;

import com.trabean.payment.dto.response.PaymentsAuthResponse;
import com.trabean.payment.dto.response.UserRoleResponse;
import com.trabean.payment.entity.Payments;
import com.trabean.payment.enums.PaymentStatus;
import com.trabean.payment.enums.UserRole;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.PaymentsRepository;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class PaymentsAuthService {

    private final PaymentsRepository paymentsRepository;
    private final RestTemplate restTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 스케줄러 초기화

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

        // 결제 처리 로직 실행: 결제 정보를 먼저 저장 (초기 상태 PENDING)
        Payments payment = paymentsRepository.save(
                Payments.createInitialPayment(userRoleResponse.getUserId(), accountId)
        );

        // 10분 후에 PENDING 상태를 확인하여 결제 상태를 CANCEL로 변경
        schedulePendingToCancelPayment(payment);

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

    // 결제 상태를 확인하고 10분 후에 PENDING 상태면 CANCEL로 변경하는 작업 스케줄링
    private void schedulePendingToCancelPayment(Payments payment) {
        scheduler.schedule(() -> {
            Payments currentPayment = paymentsRepository.findById(payment.getPayId()).orElseThrow();

            if (currentPayment.getPaymentStatus() == PaymentStatus.PENDING) {
                currentPayment.updatePaymentStatus(PaymentStatus.CANCEL);  // 결제 상태를 CANCEL로 변경
                paymentsRepository.save(currentPayment);  // 상태 변경 저장
            }
        }, 10, TimeUnit.MINUTES); // 10분 후에 실행
    }
}
