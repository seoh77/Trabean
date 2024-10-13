package com.trabean.payment.service;

import com.trabean.payment.client.UserClient;
import com.trabean.payment.interceptor.UserHeaderInterceptor;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentsUserService {
    private final UserClient userClient;

    public String getUserName(Long userId) {
        Map<String, String> response = userClient.getUserName(userId);
        return response.get("userName");
    }

    public Long getPaymentMainAccount() {
        log.info("토큰 -> userID: " + UserHeaderInterceptor.userId.get());
        Map<String, Long> response = userClient.getPaymentAccount(UserHeaderInterceptor.userId.get());
        return response.get("paymentAccountId");

    }
}
