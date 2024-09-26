package com.trabean.gateway.filter;

import com.trabean.gateway.util.JwtTokenChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class); // 로거 인스턴스 생성
    private final JwtTokenChecker jwtTokenChecker; // JWT 유효성 검사를 위한

    // 생성자에서 JwtTokenChecker를 주입받음
    @Autowired
    public JwtFilter(JwtTokenChecker jwtTokenChecker) {
        super(Config.class); // 설정 클래스를 지정
        this.jwtTokenChecker = jwtTokenChecker; // 주입받은 jwtTokenChecker를 필드에 저장
    }

    /**
     * 필터의 주요 로직을 정의하는 메서드
     */
    @Override
    public GatewayFilter apply(Config config) {
        /**
         * exchange: 요청/응답 정보를 다루는 객체
         * chain: 다음 필터로 요청을 전달하는 객체
         */
        return (exchange, chain) -> {
            logger.info("jwt filter!"); // 필터가 실행됨을 알림

            HttpHeaders headers = exchange.getRequest().getHeaders(); // 요청의 HTTP 헤더를 가져옴
            String accessToken = headers.getFirst("accessToken"); // 'accessToken' 헤더에서 토큰 값을 가져옴

            // accessToken이 존재하지 않을 경우
            if (accessToken == null) {
                logger.info("accessToken is null!"); // accessToken이 없음을 로그에 기록
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST); // 400 Bad Request 응답 설정
                return exchange.getResponse().setComplete(); // 응답을 완료 상태로 설정
            }

            // accessToken의 유효성을 검사
            if (!jwtTokenChecker.checkTokenValidation(accessToken)) {
                logger.warn("accessToken is not valid."); // 유효하지 않은 accessToken 로그 출력
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST); // 400 Bad Request 응답 설정
                return exchange.getResponse().setComplete(); // 응답을 완료 상태로 설정
            }

            logger.info("accessToken has been validated."); // 유효한 토큰 로그 출력
            /**
             * JWT는 userId값을 암호화하여 보관한다.
             * JWT를 까서 userId값을 USER 마이크로서비스에 필요한 값을 달라고 요청
             * 응답받은 데이터를 헤더에 넣어서 다음 요청으로 전송
             */

            return chain.filter(exchange); // 다음 필터로 요청을 전달
        };
    }

    // 필터 설정을 위한 내부 클래스
    public static class Config {
        // 필요한 경우 설정 속성을 여기에 추가
    }
}
