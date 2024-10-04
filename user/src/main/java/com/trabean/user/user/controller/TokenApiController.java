package com.trabean.user.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.trabean.user.user.dto.CreateAccessTokenRequest;
import com.trabean.user.user.dto.CreateAccessTokenResponse;
import com.trabean.user.user.service.TokenService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
	private final TokenService tokenService;
	private static final Logger logger = LoggerFactory.getLogger(UserApiController.class); // 로그를 위한 Logger 추가


	@PostMapping("/api/token")
	public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request) {
		String newAccessToken = tokenService.createNewAccessToken(request.refreshToken());
		logger.info("여기왔지롱");

		return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAccessTokenResponse(newAccessToken));
	}
}
