package com.trabean.payment.interceptor;

import com.trabean.payment.exception.PaymentsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class UserHeaderInterceptor implements HandlerInterceptor {
    public static ThreadLocal<Long> userId = new ThreadLocal<>();
    public static ThreadLocal<String> userKey = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String userIdHeader = request.getHeader("userId");
        String userKeyHeader = request.getHeader("userKey");

        if (userIdHeader != null) {
            try {
                userId.set(Long.parseLong(Decryption.decrypt(userIdHeader)));
            } catch (RuntimeException e) {
                throw new PaymentsException(
                        "유저 ID 값을 long으로 변환할 수 없습니다. 헤더 id값: " + request.getHeader("userID") + "헤더 key값"
                                + request.getHeader("userKey"), HttpStatus.BAD_GATEWAY);
            }
        } else {
            throw new PaymentsException("유저 아이디를 불러올 수 없습니다. (인터셉터)", HttpStatus.BAD_GATEWAY);
        }
        log.info("유저 아이디 가져오기 (인터셉터): ", userId.get());

        if (userKeyHeader != null) {
            userKey.set(Decryption.decrypt(userKeyHeader));
        }

        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        userId.remove();
        userKey.remove();
    }
}
