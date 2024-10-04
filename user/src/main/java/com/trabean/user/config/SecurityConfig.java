package com.trabean.user.config;

import com.trabean.user.config.jwt.TokenProvider;
import com.trabean.user.user.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	private final TokenProvider tokenProvider;
	private final UserDetailsService userService;
	private final RefreshTokenService refreshTokenService;
	// 특정 HTTP 요청에 대한 웹 기반 보안 구성
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		// CustomAuthenticationFilter를 설정하고 필터 체인에 추가
		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager(httpSecurity), tokenProvider, refreshTokenService);
		customAuthenticationFilter.setFilterProcessesUrl("/login"); // 로그인 처리 URL 설정

		return httpSecurity.httpBasic(HttpBasicConfigurer::disable)
				.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (API 사용 시 필요에 따라 활성화)
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(
								"/api/user/signup", // 회원가입 URL 허용
								"/api/user/login", // 로그인 URL 허용
								"/api/user/**", // 로그인 URL 허용
								"/api/token",
								"/login",
								"/signup",
								"/api/user/email/send-verification-code",
								"/api/user/email/verify-code").permitAll() // 토큰 발급 URL 허용
						.anyRequest().authenticated()) // 나머지 모든 요청은 인증 필요
				.addFilterBefore(new TokenAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class) // 기존 토큰 인증 필터 추가
				.addFilter(customAuthenticationFilter) // 커스텀 인증 필터 추가
				.build();
	}

	// 인증 관리자 관련 설정하기
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService); // 사용자 정보 서비스 설정
		authProvider.setPasswordEncoder(bCryptPasswordEncoder()); // 패스워드 인코더 설정
		return new ProviderManager(authProvider);
	}

	/* CORS 설정을 위한 새로운 메서드 작성  */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("http://localhost:3000");
		configuration.addAllowedOrigin("https://j11a604.p.ssafy.io");
		// configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","http://localhost:8080/api/user/login","https://j11a604.p.ssafy.io/api/user/login","https://j11a604.p.ssafy.io/login","http://j11a604.p.ssafy.io/login","http://j11a604.p.ssafy.io:8888/login","https://j11a604.p.ssafy.io:8888/login")); // 허용할 origin 설정
		configuration.addAllowedMethod("*"); // 모든 HTTP 메소드 허용
		configuration.addAllowedHeader("*"); // 모든 헤더 허용
		configuration.setAllowCredentials(true); // 쿠키 허용

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위 설정 적용
		return source;
	}

	// 패스워드 인코더로 사용할 빈 등록하기
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
