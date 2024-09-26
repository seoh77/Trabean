package com.trabean.gateway.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component // 이 클래스가 Spring의 컴포넌트로 등록됨을 나타냄
public class JwtTokenChecker {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenChecker.class); // 로거 인스턴스 생성

    /**
     * JWT 토큰을 생성하는 과정에서 알고리즘(예: HMAC SHA256)과 key가 결합되어 Signature가 생성됩니다.
     * 때문에 서명 생성 및 검증 과정에서 동일한 key를 사용해야 JWT가 올바르게 처리될 수 있습니다.
     */
    private final String key = "여기에들어오는값은토큰을생성했던값과일치해야한다"; // JWT 서명을 위한 비밀 키

    // 변조 검사 및 유효성 검사를 수행하는 메서드
    public boolean checkTokenValidation(String accessToken) {
        try {
            // JWT 파서 빌더를 사용하여 토큰을 검증
            Jwts.parserBuilder()
                    .setSigningKey(key) // 비밀 키를 설정
                    .build() // 파서 빌더 구축
                    .parseClaimsJws(accessToken); // 토큰을 검증하고 클레임을 파싱

            return true; // 토큰이 유효한 경우 true 반환
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰일 경우
            logger.warn("Unsupported JWT token"); // 로그 경고 출력
            return false; // 유효하지 않은 토큰
        } catch (MalformedJwtException e) {
            // 형식이 잘못된 JWT 토큰일 경우
            logger.warn("Malformed JWT token"); // 로그 경고 출력
            return false; // 유효하지 않은 토큰
        } catch (SignatureException e) {
            // 서명이 유효하지 않은 JWT 토큰일 경우
            logger.warn("Invalid JWT token"); // 로그 경고 출력
            return false; // 유효하지 않은 토큰
        } catch (ExpiredJwtException e) {
            // 만료된 JWT 토큰일 경우
            logger.warn("Expired JWT token"); // 로그 경고 출력
            return false; // 유효하지 않은 토큰
        }

    }
}
