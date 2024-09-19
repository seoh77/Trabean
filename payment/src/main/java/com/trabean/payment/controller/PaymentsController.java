package com.trabean.payment.controller;

import com.trabean.payment.dto.request.RequestPaymentRequest;
import com.trabean.payment.dto.request.UpdatePaymentInfoRequest;
import com.trabean.payment.service.PaymentUpdateDetailService;
import com.trabean.payment.service.PaymentsAuthService;
import com.trabean.payment.service.PaymentsRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentsAuthService paymentsAuthService;
    private final PaymentsRequestService paymentsRequestService;
    private final PaymentUpdateDetailService paymentUpdateDetailService;

    // QR인식 후 결제 정보 업데이트
    @PostMapping("/info")
    public ResponseEntity<String> updatePaymentInfo(@RequestBody UpdatePaymentInfoRequest request) {
        // 서비스 호출
        paymentUpdateDetailService.updatePayment(request);

        // 성공적으로 처리된 경우
        return new ResponseEntity<>("결제 정보가 성공적으로 업데이트되었습니다.", HttpStatus.OK);
    }

    // 결제 요청
    @PostMapping("/{accountId}")
    public void requestPayment(@RequestBody RequestPaymentRequest request) {
        System.out.println("Request Data: " + request.getTransactionId());

        // 서비스 호출
        paymentsRequestService.searchExchangeRate("USD");

        // 성공적으로 처리된 경우
//        return new ResponseEntity<>("결제 정보가 성공적으로 업데이트되었습니다.", HttpStatus.OK);
    }
}
