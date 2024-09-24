package com.trabean.payment.controller;

import com.trabean.payment.dto.request.RequestPaymentRequest;
import com.trabean.payment.dto.request.UpdatePaymentInfoRequest;
import com.trabean.payment.dto.response.PaymentResponse;
import com.trabean.payment.dto.response.PaymentUpdateResponse;
import com.trabean.payment.service.PaymentsAccountService;
import com.trabean.payment.service.PaymentsAuthService;
import com.trabean.payment.service.PaymentsService;
import com.trabean.payment.service.PaymentsUpdateInfoService;
import com.trabean.payment.service.PaymentsValidateService;
import com.trabean.payment.service.PaymentsWithdrawalService;
import lombok.RequiredArgsConstructor;
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
    private final PaymentsValidateService paymentsValidateService;
    private final PaymentsAccountService paymentsAccountService;
    private final PaymentsUpdateInfoService paymentsUpdateInfoService;
    private final PaymentsWithdrawalService paymentsWithdrawalService;
    private final PaymentsService paymentsService;

    // QR 인식 후 결제 정보 업데이트
    @PostMapping("/info")
    public PaymentUpdateResponse updatePaymentInfo(@RequestBody UpdatePaymentInfoRequest request) {

        return paymentsUpdateInfoService.updatePayment(request);
    }

    // 결제 요청
    @PostMapping("/{accountId}")
    public PaymentResponse requestPayment(@PathVariable Long accountId,
                                          @RequestBody RequestPaymentRequest request) {
        return paymentsService.requestPayment(accountId, request);
    }

    // 재 결제 요청
    @PostMapping("/retry/{accountId}")
    public PaymentResponse reRequestPayment(@PathVariable Long accountId,
                                            @RequestBody RequestPaymentRequest request) {
        return paymentsService.requestPayment(accountId, request);
    }
}
