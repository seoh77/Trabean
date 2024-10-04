package com.trabean.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;


@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${app.api.url}")
	private String baseUrl;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")  // 모든 경로에 대해 CORS 허용
				.allowedOrigins("http://localhost:3000", "https://j11a604.p.ssafy.io","http://j11a604.p.ssafy.io")  // 프론트엔드 도메인
				// .allowedOriginPatterns(baseUrl,"http://localhost:3000","http://localhost:8080/api/user/login","https://j11a604.p.ssafy.io/api/user/login","https://j11a604.p.ssafy.io/login","http://j11a604.p.ssafy.io/login","http://j11a604.p.ssafy.io:8888/login","https://j11a604.p.ssafy.io:8888/login") // 허용할 origin 설정
				.allowedMethods("*")  // 모든 HTTP 메소드 허용 (GET, POST 등)
				.allowedHeaders("*")  // 모든 헤더 허용
				.allowCredentials(true);  // 쿠키 인증 요청 허용
	}
}