package com.trabean.user.config;

import com.trabean.user.config.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
	private final TokenProvider tokenProvider;
	private final UserDetailsService userService;

	@Value("${app.api.url}")
	private String baseUrl;

	// 특정 HTTP 요청에 대한 웹 기반 보안 구성
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								new AntPathRequestMatcher("/api/user/signup"), // 회원가입 URL 허용
								new AntPathRequestMatcher("/api/user/login"), // 로그인 URL 허용
								new AntPathRequestMatcher("/api/token")) // 토큰 발급 URL 허용
						.permitAll() // 위 경로들은 인증 없이 접근 가능
						.anyRequest().authenticated()) // 나머지 모든 요청은 인증 필요
				.formLogin(formLogin -> formLogin
						.loginPage("/api/user/login") // 사용자 정의 로그인 페이지 설정
						.loginProcessingUrl("/api/user/login") // 로그인 폼 제출 URL
						.defaultSuccessUrl("/api/user/profile", true) // 로그인 성공 후 이동할 경로
						.failureUrl("/api/user/login?error=true") // 로그인 실패 시 이동할 경로
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/api/user/logout") // 로그아웃 처리 URL
						.logoutSuccessUrl("/api/user/login") // 로그아웃 성공 후 이동할 경로
						.invalidateHttpSession(true) // 세션 무효화
						.deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
						.permitAll())
				.csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (API 사용 시 필요에 따라 활성화)
				.build();
	}

	// 인증 관리자 관련 설정하기
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService userDetailsService) throws Exception {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService); // 사용자 정보 서비스 설정
		authProvider.setPasswordEncoder(bCryptPasswordEncoder); // 패스워드 인코더 설정
		return new ProviderManager(authProvider);
	}

	// 패스워드 인코더로 사용할 빈 등록하기
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
