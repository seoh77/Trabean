package com.trabean.user.config;

import com.trabean.user.config.jwt.TokenProvider;
import com.trabean.user.config.CustomAuthenticationFilter;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	private final TokenProvider tokenProvider;
	private final UserDetailsService userService;
	private final RefreshTokenService refreshTokenService;
	// 특정 HTTP 요청에 대한 웹 기반 보안 구성
	


	// 인증 관리자 관련 설정하기
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService); // 사용자 정보 서비스 설정
		authProvider.setPasswordEncoder(bCryptPasswordEncoder()); // 패스워드 인코더 설정
		return new ProviderManager(authProvider);
	}

	// 패스워드 인코더로 사용할 빈 등록하기
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
