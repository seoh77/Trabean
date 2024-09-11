package com.trabean.payment.service;

import com.trabean.payment.entity.Payments;
import com.trabean.payment.repository.PaymentsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PaymentsService {
    private final PaymentsRepository paymentsRepository;

    public PaymentsService(PaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    // 결제 초기 정보 저장 로직
    public Payments createInitialPayment(Long userId, Long accountId) {
        // Payments 엔티티를 생성하고 저장
        Payments payment = Payments.createInitialPayment(userId, accountId);
        return paymentsRepository.save(payment);
    }

    // 추가 결제 정보 업데이트 로직

}
