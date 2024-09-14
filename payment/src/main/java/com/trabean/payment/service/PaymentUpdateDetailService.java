package com.trabean.payment.service;

import com.trabean.payment.dto.request.UpdatePaymentInfoRequest;
import com.trabean.payment.entity.Merchants;
import com.trabean.payment.entity.Payments;
import com.trabean.payment.enums.PaymentStatus;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.MerchantsRepository;
import com.trabean.payment.repository.PaymentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentUpdateDetailService {

    private final PaymentsRepository paymentsRepository;
    private final MerchantsRepository merchantsRepository;

    // 결제 정보 업데이트
    public void updatePayment(UpdatePaymentInfoRequest updatePaymentInfoRequest) {
        // payId값 조회
        long paymentId = updatePaymentInfoRequest.getPayId();
        if (paymentsRepository.findById(paymentId).isEmpty()) {
            throw new PaymentsException("잘못된 payId값 입니다.", HttpStatus.NOT_FOUND);
        }

        // 트랜잭션id 일치하는 지 검증
        Payments payment = paymentsRepository.findById(paymentId).get();
        if (!payment.getTransactionId().equals(updatePaymentInfoRequest.getTransactionId())) {
            throw new PaymentsException("트랜잭션ID 가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 유효한 상태값인지 검증
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new PaymentsException("올바르지 않은 결제 상태입니다.", HttpStatus.BAD_REQUEST);
        }

        // Merchant 정보 조회
        Merchants merchant = merchantsRepository.findById(updatePaymentInfoRequest.getMerchantId())
                .orElseThrow(() -> new PaymentsException("잘못된 merchantId값 입니다.", HttpStatus.NOT_FOUND));

        // 결제 정보 업데이트
        payment.updatePaymentDetails(
                updatePaymentInfoRequest.getKrwAmount(),
                updatePaymentInfoRequest.getForeignAmount(),
                merchant
        );

        paymentsRepository.save(payment); // 저장
    }
}
