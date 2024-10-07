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

        // 기존 Refresh Token이 있는지 확인 (쿠키에서 확인)
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

        // Refresh Token이 유효한지 확인
        boolean isRefreshTokenValid = existingRefreshToken != null && tokenProvider.validToken(existingRefreshToken);

        String accessToken;
        String refreshToken;

        if (existingRefreshToken != null && !isRefreshTokenValid) {
            // Refresh Token이 유효하지 않으면 삭제하고 새로 발급
            log.info("Invalid refresh token detected, deleting and generating new tokens");
            refreshTokenService.deleteRefreshToken(String.valueOf(loggedInUser.getUser_id())); // DB에서 해당 Refresh Token 삭제 로직
            existingRefreshToken = null; // 기존 쿠키에서의 토큰도 무효화
        }

        if (isRefreshTokenValid) {
            // Refresh Token이 유효하면 기존 토큰을 그대로 사용
            log.info("Using existing refresh token");
            refreshToken = existingRefreshToken;
            accessToken = tokenProvider.generateToken(loggedInUser, java.time.Duration.ofMinutes(30));  // 새로운 access token 발급
        } else {
            // Refresh Token이 없거나 유효하지 않으면 새로운 토큰 발급
            log.info("Generating new tokens");
            accessToken = tokenProvider.generateToken(loggedInUser, java.time.Duration.ofMinutes(30));
            refreshToken = tokenProvider.generateRefreshToken(loggedInUser, java.time.Duration.ofDays(7));

            // 새로운 Refresh Token을 DB에 저장
            refreshTokenService.saveRefreshToken(loggedInUser.getUser_id(), loggedInUser.getEmail(), refreshToken);
        }

        // 헤더에 accessToken과 refreshToken 추가
        response.addHeader("Authorization", "Bearer " + refreshToken);
        response.addHeader("Refresh-Token", refreshToken);

        // 응답 본문에 userKey를 포함하여 JSON으로 반환
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("userKey", userKey);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        // 인증 실패 시 처리
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("error", "Authentication failed")));
    }
}
