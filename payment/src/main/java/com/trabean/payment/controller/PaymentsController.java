package com.trabean.payment.controller;

import com.trabean.payment.dto.response.PaymentsAuthResponse;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.service.PaymentsAuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentsAuthService paymentsAuthService;

    // 초기 결제 요청 처리
    @PostMapping("/{accountId}/auth")
    public ResponseEntity<PaymentsAuthResponse> initiatePayment(
            @PathVariable Long accountId,  // accountId를 경로 변수로 받음
            @RequestBody Map<String, String> request) {

        String userKey = request.get("userKey");

        try {
            // 비즈니스 로직은 서비스로 분리
            PaymentsAuthResponse response = paymentsAuthService.processPayment(userKey, accountId);
            return ResponseEntity.ok(response);  // 성공 시 결제 정보 반환

        } catch (PaymentsException e) {
            System.out.println("커스텀 예외 발생: " + e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(null);

        } catch (Exception e) {
            e.printStackTrace();  // 전체 스택 트레이스 출력
            return ResponseEntity.status(500).body(null);  // 500 Internal Server Error
        }
    }
}
