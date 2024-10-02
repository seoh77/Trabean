package com.trabean.payment.service;

import com.trabean.payment.client.UserClient;
import com.trabean.payment.exception.PaymentsException;
import feign.FeignException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
}
