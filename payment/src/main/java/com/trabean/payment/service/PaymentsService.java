package com.trabean.payment.service;

import com.trabean.payment.entity.Payments;
import com.trabean.payment.enums.PaymentStatus;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.PaymentsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;

    // 결제 상태 조회 메서드
    public PaymentStatus getPaymentStatus(Long payId) {
        // 결제 ID로 결제 정보 조회
        Payments payment = paymentsRepository.findById(payId)
                .orElseThrow(() -> new PaymentsException("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 결제 상태 반환
        return payment.getPaymentStatus();
    }
}
