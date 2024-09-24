package com.trabean.payment.service;

import com.trabean.payment.dto.request.RequestPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentsRetryService {

    public void retryPayment(String errorMessage, RequestPaymentRequest request) {
        // 외화 계좌가 없는 경우
        if (errorMessage.equals("FOREIGN_ACCOUNT_NOT_FOUND")) {

        }
    }
}
