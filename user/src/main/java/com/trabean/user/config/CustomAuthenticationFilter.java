package com.trabean.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trabean.user.config.jwt.TokenProvider;
import com.trabean.user.user.dto.LoginRequest;
import com.trabean.user.user.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.trabean.user.user.entity.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // 요청에서 로그인 정보를 읽어 DTO로 변환
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );
            // 인증 시도
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        // 현재 인증된 사용자 정보에서 userKey 가져오기
        User loggedInUser = (User) authResult.getPrincipal();
        String userKey = loggedInUser.getUser_key();
    
        // Refresh Token이 유효한지 확인하는 로직은 그대로 유지
        String existingRefreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    existingRefreshToken = cookie.getValue();
                    break;
                }
            }
        }
    
        boolean isRefreshTokenValid = existingRefreshToken != null && tokenProvider.validToken(existingRefreshToken);
    
        String accessToken;
        String refreshToken;
    
        if (existingRefreshToken != null && !isRefreshTokenValid) {
            // Refresh Token이 유효하지 않으면 삭제하고 새로 발급
            log.info("Invalid refresh token detected, deleting and generating new tokens");
            refreshTokenService.deleteRefreshToken(String.valueOf(loggedInUser.getUser_id()));
            existingRefreshToken = null;
        }
    
        if (isRefreshTokenValid) {
            refreshToken = existingRefreshToken;
            accessToken = tokenProvider.generateToken(loggedInUser, java.time.Duration.ofDays(7));
        } else {
            // 새로운 토큰 발급
            accessToken = tokenProvider.generateToken(loggedInUser, java.time.Duration.ofDays(7));
            refreshToken = tokenProvider.generateRefreshToken(loggedInUser, java.time.Duration.ofDays(7));
            refreshTokenService.saveRefreshToken(loggedInUser.getUser_id(), loggedInUser.getEmail(), refreshToken);
        }
    
        // 쿠키 설정 (accessToken)
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS에서만 동작
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(7*24*60 * 60); // 30분
    
        // 쿠키 설정 (refreshToken)
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS에서만 동작
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 1주일
    
        // Set-Cookie 헤더에 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    
        // 응답 본문에 userKey를 포함하여 JSON으로 반환
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("userKey", userKey);
    
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
        response.addHeader("Authorization","Bearer "+ accessToken);
        response.addCookie(createCookie("refreshToken", refreshToken));  
    }
    
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        // 인증 실패 시 처리
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("error", "Authentication failed")));
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
