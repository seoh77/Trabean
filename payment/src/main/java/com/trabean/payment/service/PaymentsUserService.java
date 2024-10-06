package com.trabean.payment.service;

import com.trabean.payment.client.UserClient;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.interceptor.UserHeaderInterceptor;
import feign.FeignException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentsUserService {
    private final UserClient userClient;

    public String getUserName(Long userId) {
        try {
            Map<String, String> response = userClient.getUserName(userId);
            return response.get("userName");
        } catch (FeignException e) {
            throw new PaymentsException("유저 이름을 가져오는 데 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Long getPaymentMainAccount() {
        log.info("토큰 -> userID: " + UserHeaderInterceptor.userId.get());
        try {
            Map<String, Long> response = userClient.getPaymentAccount(UserHeaderInterceptor.userId.get());
            return response.get("paymentAccountId");
        } catch (RuntimeException e) {
            if (UserHeaderInterceptor.userId.get() == null) {
                throw new PaymentsException("토큰으로 유저 ID를 불러오는 데 실패했습니다.", HttpStatus.BAD_GATEWAY);
            }
            throw new PaymentsException("유저 메인 결제 계좌 불러올 때 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
