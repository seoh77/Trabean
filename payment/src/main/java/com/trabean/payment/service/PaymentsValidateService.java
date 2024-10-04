package com.trabean.payment.service;

import com.trabean.payment.entity.Payments;
import com.trabean.payment.enums.PaymentStatus;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.interceptor.UserHeaderInterceptor;
import com.trabean.payment.repository.PaymentsRepository;
import jakarta.transaction.Transactional;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentsValidateService {

    private final PaymentsRepository paymentsRepository;

    // 결제 상태 조회 메서드
    public PaymentStatus getPaymentStatus(Long payId) {
        // 결제 ID로 결제 정보 조회
        Payments payment = paymentsRepository.findById(payId)
                .orElseThrow(() -> new PaymentsException("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 결제 상태 반환
        return payment.getPaymentStatus();
    }

    // transactionID 검증 메서드
    public void validateTransactionId(String transactionId, Long payId) {
        if (!getPaymentStatus(payId).equals(PaymentStatus.PENDING)) {
            throw new PaymentsException("결제 상태가 유효하지 않습니다 : " + getPaymentStatus(payId),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            Payments payment = paymentsRepository.findById(payId)
                    .orElseThrow(() -> new PaymentsException("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
            if (!payment.getTransactionId().equals(transactionId)) {
                throw new PaymentsException("transactionId가 유효하지 않습니다",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    // 데이터 무결성 검사
    public void validatePaymentData(Long payId, Long merchantId, Double foreignAmount, Long krwAmount) {
        Payments payment = paymentsRepository.findById(payId)
                .orElseThrow(() -> new PaymentsException("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (payment.getMerchant() == null) {
            throw new NullPointerException("Merchant 정보가 null 입니다.");
        }
        if (!Objects.equals(payment.getMerchant().getMerchantId(), merchantId)) {
            throw new PaymentsException("가맹점 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // foreignAmount 또는 krwAmount 중 하나를 선택하여 검증
        if (foreignAmount != null && payment.getForeignAmount() != null) {
            if (!Objects.equals(payment.getForeignAmount(), foreignAmount)) {
                throw new PaymentsException("외화 가격 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        } else if (krwAmount != null && payment.getKrwAmount() != null) {
            if (!Objects.equals(payment.getKrwAmount(), krwAmount)) {
                throw new PaymentsException("KRW 가격 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new PaymentsException("가격 정보가 부족합니다.", HttpStatus.BAD_REQUEST);
        }

        // 유저 정보 일치 확인
        if (!payment.getUserId().equals(UserHeaderInterceptor.userId.get())) {
            throw new PaymentsException("유저 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
