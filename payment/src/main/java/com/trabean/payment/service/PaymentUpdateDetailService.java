package com.trabean.payment.service;

import com.trabean.payment.dto.request.UpdatePaymentInfoRequest;
import com.trabean.payment.dto.response.PaymentUpdateResponse;
import com.trabean.payment.dto.response.PaymentUpdateResponse.PaymentData;
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
    private final ExchangeRateService exchangeRateService;
    private final MerchantsRepository merchantsRepository;
    private final PaymentsAuthService paymentsAuthService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 스케줄러 초기화

    // 결제 정보 업데이트
    public PaymentUpdateResponse updatePayment(UpdatePaymentInfoRequest updatePaymentInfoRequest) {

        // 권한 검증
        paymentsAuthService.checkAuthPayment(updatePaymentInfoRequest.getUserId(),
                updatePaymentInfoRequest.getAccountId());

        // Merchant 정보 조회
        Merchants merchant = merchantsRepository.findById(updatePaymentInfoRequest.getMerchantId())
                .orElseThrow(() -> new PaymentsException("잘못된 merchantId값 입니다.", HttpStatus.NOT_FOUND));

        Payments payment = Payments.createInitialPayment(updatePaymentInfoRequest.getUserId(),
                updatePaymentInfoRequest.getAccountId(), merchant,
                // 한국돈으로 계산해서 저장
                exchangeRateService.calculateKrw(merchant.getExchangeCurrency(),
                        updatePaymentInfoRequest.getForeignAmount()),
                updatePaymentInfoRequest.getForeignAmount());
        paymentsRepository.save(payment);

        schedulePendingToCancelPayment(payment, 5); // 5분 동안 결제 진행 안 할 경우 취소

        PaymentData data = new PaymentData(payment.getPayId(), payment.getAccountId(), payment.getForeignAmount(),
                payment.getKrwAmount(), payment.getMerchant().getName(), payment.getTransactionId());
        return new PaymentUpdateResponse("SUCCESS", "결제 정보 업데이트에 성공하셨습니다.", data);
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
