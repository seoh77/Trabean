package com.trabean.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserHeaderInterceptor implements HandlerInterceptor {

    public static ThreadLocal<String> userId = new ThreadLocal<>();
    public static ThreadLocal<String> userKey = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String userIdHeader = request.getHeader("userId");
        String userKeyHeader = request.getHeader("userKey");

        if (userIdHeader != null) {
            userId.set(userIdHeader);
        }

        if (userKeyHeader != null) {
            userKey.set(userKeyHeader);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        userId.remove();
        userKey.remove();
    }
}
