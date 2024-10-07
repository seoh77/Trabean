package com.trabean.payment.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserHeaderInterceptor implements HandlerInterceptor {
    public static ThreadLocal<Long> userId = new ThreadLocal<>();
    public static ThreadLocal<String> userKey = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String userIdHeader = request.getHeader("userId");
        String userKeyHeader = request.getHeader("userKey");

        if (userIdHeader != null) {
            try {
                userId.set(Long.parseLong(userIdHeader));
            } catch (RuntimeException e) {
                throw new IllegalArgumentException(
                        "유저 ID 값을 long으로 변환할 수 없습니다. 헤더 id값: " + request.getHeader("userID") + "헤더 key값"
                                + request.getHeader("userKey"));
            }
        } else {
            userId.set(31L); // userId가 null일 경우 3으로 설정
        }

        if (userKeyHeader != null) {
            userKey.set(userKeyHeader);
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
