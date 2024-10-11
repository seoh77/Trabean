package com.trabean.payment.service;

import com.trabean.payment.dto.request.UpdatePaymentInfoRequest;
import com.trabean.payment.dto.response.PaymentUpdateResponse;
import com.trabean.payment.dto.response.PaymentUpdateResponse.PaymentData;
import com.trabean.payment.entity.Merchants;
import com.trabean.payment.entity.Payments;
import com.trabean.payment.enums.PaymentStatus;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.interceptor.UserHeaderInterceptor;
import com.trabean.payment.repository.MerchantsRepository;
import com.trabean.payment.repository.PaymentsRepository;
import jakarta.transaction.Transactional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Transactional
@Slf4j
public class PaymentsUpdateInfoService {

    private final PaymentsRepository paymentsRepository;
    private final ExchangeRateService exchangeRateService;
    private final MerchantsRepository merchantsRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 스케줄러 초기화

    // 결제 정보 업데이트
    public PaymentUpdateResponse updatePayment(UpdatePaymentInfoRequest request) {

        // Merchant 정보 조회
        Merchants merchant = merchantsRepository.findById(request.getMerchantId())
                .orElseThrow(() -> new PaymentsException("잘못된 merchantId값 입니다.", HttpStatus.NOT_FOUND));

        log.info("유저 아이디 가져오기" + UserHeaderInterceptor.userId.get());
        // 업데이트
        Payments payment = Payments.createInitialPayment(UserHeaderInterceptor.userId.get(),
                request.getAccountId(), merchant,
                // 한화
                request.getKrwAmount() == null ? exchangeRateService.calculateKrw(merchant.getExchangeCurrency(),
                        request.getForeignAmount()) : request.getKrwAmount(),
                // 외화
                request.getForeignAmount() == null ? null : request.getForeignAmount());
        paymentsRepository.save(payment);

        // 유효기간: 5분
        schedulePendingToCancelPayment(payment, 10); // 10분 동안 결제 진행 안 할 경우 취소

        PaymentData data = new PaymentData(payment.getPayId(), payment.getAccountId(), payment.getForeignAmount(),
                payment.getKrwAmount(), payment.getMerchant().getName());
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

    public void updatePaymentStatus(Long payId, PaymentStatus status) {
        // 결제 정보를 레포지토리에서 가져옴
        Payments payment = paymentsRepository.findById(payId)
                .orElseThrow(() -> new PaymentsException("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 결제 상태 변경
        payment.updatePaymentStatus(status);

        // 변경된 정보 저장
        paymentsRepository.save(payment);
    }

    // 결제 성공 시 정보 업데이트
    public void updateSuccess(Long payId) {
        updatePaymentStatus(payId, PaymentStatus.SUCCESS);
        Payments payment = paymentsRepository.findById(payId)
                .orElseThrow(() -> new PaymentsException("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        if (payment.getKrwAmount() == null) {
            Merchants merchant = payment.getMerchant();
            Long krwAmount = exchangeRateService.calculateKrw(merchant.getExchangeCurrency(),
                    payment.getForeignAmount());
            payment.updateKrwAmount(krwAmount);
            paymentsRepository.save(payment);
        }
    }
}
