package com.trabean.payment.service;

import com.trabean.payment.dto.request.UpdatePaymentInfoRequest;
import com.trabean.payment.entity.Merchants;
import com.trabean.payment.entity.Payments;
import com.trabean.payment.enums.PaymentStatus;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.MerchantsRepository;
import com.trabean.payment.repository.PaymentsRepository;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class PaymentUpdateDetailService {

    private final PaymentsRepository paymentsRepository;
    private final MerchantsRepository merchantsRepository;
    private final PaymentsAuthService paymentsAuthService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 스케줄러 초기화

    // 결제 정보 업데이트
    public void updatePayment(UpdatePaymentInfoRequest updatePaymentInfoRequest) {

        // 권한 검증
        Long userId = paymentsAuthService.checkAuthPayment(updatePaymentInfoRequest.getUserKey(),
                updatePaymentInfoRequest.getAccountId());

        // Merchant 정보 조회
        Merchants merchant = merchantsRepository.findById(updatePaymentInfoRequest.getMerchantId())
                .orElseThrow(() -> new PaymentsException("잘못된 merchantId값 입니다.", HttpStatus.NOT_FOUND));

        Payments payment = Payments.createInitialPayment(userId, updatePaymentInfoRequest.getAccountId(), merchant,
                updatePaymentInfoRequest.getKrwAmount(),
                updatePaymentInfoRequest.getForeignAmount());
        paymentsRepository.save(payment);

        schedulePendingToCancelPayment(payment, 5);
    }

    // 결제 상태를 확인하고 N분 후에 PENDING 상태면 CANCEL로 변경하는 작업 스케줄링
    private void schedulePendingToCancelPayment(Payments payment, Integer delay) {
        scheduler.schedule(() -> {
            Payments currentPayment = paymentsRepository.findById(payment.getPayId()).orElseThrow();

            if (currentPayment.getPaymentStatus() == PaymentStatus.PENDING) {
                currentPayment.updatePaymentStatus(PaymentStatus.CANCEL);  // 결제 상태를 CANCEL로 변경
                paymentsRepository.save(currentPayment);  // 상태 변경 저장
            }
        }, delay, TimeUnit.MINUTES);
    }
}
