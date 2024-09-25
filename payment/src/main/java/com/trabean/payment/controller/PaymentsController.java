package com.trabean.payment.controller;

import com.trabean.payment.dto.request.RequestPaymentRequest;
import com.trabean.payment.dto.request.UpdatePaymentInfoRequest;
import com.trabean.payment.dto.response.PaymentResponse;
import com.trabean.payment.dto.response.PaymentUpdateResponse;
import com.trabean.payment.dto.response.PaymentsHistoryResponse;
import com.trabean.payment.service.PaymentsHistoryService;
import com.trabean.payment.service.PaymentsService;
import com.trabean.payment.service.PaymentsUpdateInfoService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentsUpdateInfoService paymentsUpdateInfoService;
    private final PaymentsService paymentsService;
    private final PaymentsHistoryService paymentsHistoryService;

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

    // 전체 결제 내역 조회
    @GetMapping("/{travelAccountId}")
    public ResponseEntity<PaymentsHistoryResponse> getPayments(
            @PathVariable Long travelAccountId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyMMdd") LocalDate startdate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyMMdd") LocalDate enddate,
            @RequestParam(defaultValue = "1") int page) {

        int actualPage = (page > 0) ? page - 1 : 0;

        // startdate가 null 이면 과거 무한대값으로 설정
        if (startdate == null) {
            startdate = LocalDate.of(1970, 1, 1);  // 과거의 무한대
        }

        // enddate가 null 이면 오늘 날짜로 설정
        if (enddate == null) {
            enddate = LocalDate.now();  // 오늘 날짜
        }

        // 서비스 호출해서 결제 내역 가져오기
        PaymentsHistoryResponse response = paymentsHistoryService.getPaymentHistory(travelAccountId, startdate, enddate,
                actualPage);

        return ResponseEntity.ok(response);
    }
}
