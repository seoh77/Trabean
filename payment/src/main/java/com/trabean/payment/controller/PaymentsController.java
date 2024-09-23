package com.trabean.payment.controller;

import com.trabean.payment.dto.request.RequestPaymentRequest;
import com.trabean.payment.dto.request.UpdatePaymentInfoRequest;
import com.trabean.payment.dto.response.PaymentUpdateResponse;
import com.trabean.payment.enums.PaymentStatus;
import com.trabean.payment.service.PaymentUpdateDetailService;
import com.trabean.payment.service.PaymentsAuthService;
import com.trabean.payment.service.PaymentsRequestService;
import com.trabean.payment.service.PaymentsService;
import com.trabean.payment.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentsAuthService paymentsAuthService;
    private final PaymentsService paymentsService;
    private final PaymentsRequestService paymentsRequestService;
    private final PaymentUpdateDetailService paymentUpdateDetailService;
    private final WithdrawalService withdrawalService;

    // QR인식 후 결제 정보 업데이트
    @PostMapping("/info")
    public PaymentUpdateResponse updatePaymentInfo(@RequestBody UpdatePaymentInfoRequest request) {
        // 서비스 호출
        return paymentUpdateDetailService.updatePayment(request);
    }

    // 결제 요청
    @PostMapping("/{accountId}")
    public ResponseEntity<String> requestPayment(@PathVariable Long accountId,
                                                 @RequestBody RequestPaymentRequest request) {

        // transactionId 검증
        paymentsService.validateTransactionId(request.getTransactionId(), request.getPayId());

        // 잔액 검증
        paymentsRequestService.validateAmount(accountId, request);

        // 데이터 무결성 검증
        paymentsService.validatePaymentData(request.getPayId(), request.getMerchantId(), request.getForeignAmount());

        // 출금 처리
        withdrawalService.withdrawaltoPay(request, accountId);

        // 결제 상태 완료로 변경
        paymentsService.updatePaymentStatus(request.getPayId(), PaymentStatus.SUCCESS);

        // 성공적으로 처리된 경우
        return new ResponseEntity<>("결제 정보가 성공적으로 업데이트되었습니다.", HttpStatus.OK);
    }
}
