package com.trabean.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trabean.user.config.jwt.TokenProvider;
import com.trabean.user.user.dto.LoginRequest;
import com.trabean.user.user.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
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
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService; // RefreshToken 서비스 추가
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
        // 인증 성공 시 accessToken 발급
        String accessToken = tokenProvider.generateToken((User) authResult.getPrincipal(), java.time.Duration.ofMinutes(30));

        // refreshToken 발급
        String refreshToken = tokenProvider.generateRefreshToken((User) authResult.getPrincipal(), java.time.Duration.ofDays(7)); // Refresh Token은 더 긴 만료 기간 설정

        // 현재 인증된 사용자 정보에서 userKey 가져오기
        User loggedInUser = (User) authResult.getPrincipal();
        String userKey = loggedInUser.getUser_key();

        // Refresh Token을 DB에 저장
        refreshTokenService.saveRefreshToken(loggedInUser.getEmail(), refreshToken);

        // 응답 헤더에 accessToken 설정
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setContentType("application/json");

        // Refresh Token을 HTTPOnly 쿠키에 저장
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS에서만 전송
        refreshTokenCookie.setPath("/"); // 전체 도메인에서 사용 가능
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 만료 시간 설정 (1주일)
        response.addCookie(refreshTokenCookie);

        // 응답 바디에는 사용자 정보만 포함 (토큰은 제외)
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("userKey", userKey);
//        responseBody.put("refreshToken", refreshToken);
//        responseBody.put("accessToken", accessToken);

        // JSON으로 변환하여 응답에 기록
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
