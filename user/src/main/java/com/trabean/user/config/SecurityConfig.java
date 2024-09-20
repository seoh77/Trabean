package com.trabean.user.config;

import com.trabean.user.config.jwt.JwtAccessDeniedHandler;
import com.trabean.user.config.jwt.JwtAuthenticationEntryPoint;
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
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final UserDetailsService userService;

	@Value("${app.api.url}")
	private String baseUrl;

//	@Value("${app.api.production-url}")
//	private String productionUrl;

	//스프링 시큐리티 기능 비활성화 하기
	@Bean
	public WebSecurityCustomizer configure() {
		return (web -> web.ignoring()
				.requestMatchers(new AntPathRequestMatcher(baseUrl + "/api/**", "POST")));
	}

	//특정 HTTP 요청에 대한 웹 기반 보안 구성
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(new AntPathRequestMatcher(baseUrl + "/api/user/**"),
								new AntPathRequestMatcher("/api/user"),
								new AntPathRequestMatcher(baseUrl+ "/api/**"))
						.permitAll()
						.anyRequest().authenticated())
				.formLogin(formLogin -> formLogin
						.loginPage(baseUrl + "/api/user/login")
						.defaultSuccessUrl(baseUrl+"/api/accounts"))
				.logout(logout -> logout
						.logoutSuccessUrl(baseUrl + "/api/user/login")
						.invalidateHttpSession(true)
				)
				.csrf(AbstractHttpConfigurer::disable) //csrf 비활성화
				.build();
	}

	//인증 관리자 관련 설정하기
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService userDetailsService) throws Exception {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService); //사용자 정보 서비스 설정하기
		authProvider.setPasswordEncoder(bCryptPasswordEncoder);
		return new ProviderManager(authProvider);
	}

	//패스워드 인코더로 사용할 빈 등록하기
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
