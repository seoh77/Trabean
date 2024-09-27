package com.trabean.user.user.controller;

import com.trabean.user.user.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper; // JSON 파싱을 위한 라이브러리
import com.trabean.user.user.entity.User;
import com.trabean.user.user.service.ExternalApiService;
import com.trabean.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@RequiredArgsConstructor
@RestController
public class UserApiController {

	private static final Logger logger = LoggerFactory.getLogger(UserApiController.class); // 로그를 위한 Logger 추가
	private final UserService userService;
	private final ExternalApiService externalApiService;
	private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파서


	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody AddUserRequest request) {
		try {
			// 외부 API로 데이터를 전송하고 응답 데이터를 받음
			String externalApiResponse = userService.save(request);

			// 응답을 로그로 출력하여 확인
			logger.info("외부 API 응답: {}", externalApiResponse);

			// 응답 데이터가 JSON 형식인지 확인
			if (externalApiResponse == null || externalApiResponse.isEmpty()) {
				logger.error("외부 API 응답이 비어 있습니다.");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("외부 API 응답이 비어 있습니다.");
			}

			// 외부 API에서 받은 JSON 응답 데이터를 파싱
			Object jsonResponse = objectMapper.readValue(externalApiResponse, Object.class);

			// 회원가입 성공 메시지 + 외부 API 응답 데이터 반환 (JSON 포맷으로 그대로 출력)
			return ResponseEntity.ok().body("회원가입에 성공하였습니다. 응답 데이터: " + objectMapper.writeValueAsString(jsonResponse));
		} catch (IllegalArgumentException e) {
			logger.error("회원가입 실패: {}", e.getMessage());
			return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
		} catch (Exception e) {
			// 예외 메시지와 스택 트레이스를 로그에 남김
			logger.error("서버 오류 발생: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다: " + e.getMessage());
		}
	}

	// 로그인 요청 처리
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		try {
			// 로그인 로직 호출 및 Access Token 반환
			String accessToken = userService.login(loginRequest);
			return ResponseEntity.ok().body("Bearer " + accessToken);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	// 사용자 정보 조회 및 payment_account_id 반환
	@GetMapping("/paymentaccount/{userId}")
	public ResponseEntity<UserPaymentAccountIdResponse> getUserPaymentAccount(@PathVariable Long userId) {
		UserPaymentAccountIdResponse userPaymentAccountIdResponse = userService.getUserPaymentAccount(userId);

		// 만약 payment_account_id가 null이면, null로 설정
		if (userPaymentAccountIdResponse.getPaymentAccountId() == null) {
			userPaymentAccountIdResponse.setPaymentAccountId(null);
		}

		return ResponseEntity.ok(userPaymentAccountIdResponse);
	}
	// 사용자 정보 조회 및 name 반환
	@GetMapping("/name/{userId}")
	public ResponseEntity<UserNameResponse> getUserName(@PathVariable Long userId) {
		UserNameResponse userNameResponse = userService.getUserName(userId);

		// 만약 name가 null이면, null로 설정
		if (userNameResponse.getUserName() == null) {
			userNameResponse.setUserName(null);
		}

		return ResponseEntity.ok(userNameResponse);
	}

	@PostMapping("/getuserkey")
	public ResponseEntity<GetUserKeyResponse> getUserKey(@RequestBody GetUserKeyRequest request) {
		User user = userService.findByUserId(request.getUserId());

		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		GetUserKeyResponse response = new GetUserKeyResponse(user.getUser_key());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/email/{email}")
	public ResponseEntity<Boolean> checkEmailDuplication(@PathVariable String email) {
		return ResponseEntity.ok(userService.checkEmailDuplication(email));
	}
}