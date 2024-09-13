package com.trabean.user.user.controller;
import com.fasterxml.jackson.databind.ObjectMapper; // JSON 파싱을 위한 라이브러리
import com.trabean.user.user.service.ExternalApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.trabean.user.user.dto.AddUserRequest;
import com.trabean.user.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/user")
@RequiredArgsConstructor
@RestController
public class UserApiController {

	private final UserService userService;
	private final ExternalApiService externalApiService;
	private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파서

	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody AddUserRequest request) {
		try {
			// 외부 API로 데이터를 전송하고 응답 데이터를 받음
			String externalApiResponse = userService.save(request);

			// 외부 API에서 받은 JSON 응답 데이터를 파싱 (문자열 형태로 저장된 JSON 응답을 처리)
			Object jsonResponse = objectMapper.readValue(externalApiResponse, Object.class);

			// 회원가입 성공 메시지 + 외부 API 응답 데이터 반환 (JSON 포맷으로 그대로 출력)
			return ResponseEntity.ok().body("회원가입에 성공하였습니다. 응답 데이터: " + objectMapper.writeValueAsString(jsonResponse));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}

	@GetMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
		return ResponseEntity.ok().body("로그아웃");
	}
}
