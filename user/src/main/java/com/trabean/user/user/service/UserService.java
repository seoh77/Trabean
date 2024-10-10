package com.trabean.user.user.service;

import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trabean.user.config.jwt.TokenProvider;
import com.trabean.user.user.dto.LoginRequest;
import com.trabean.user.user.dto.UserEmailResponse;
import com.trabean.user.user.dto.UserNameResponse;
import com.trabean.user.user.dto.UserPaymentAccountIdResponse;
import com.trabean.user.user.entity.RefreshToken;
import com.trabean.user.user.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.trabean.user.user.dto.AddUserRequest;
import com.trabean.user.user.entity.User;
import com.trabean.user.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ExternalApiService externalApiService; // 외부 API 호출을 위한 서비스 주입
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate = new RestTemplate(); // 외부 API 호출을 위한 RestTemplate

    private static final String API_URL = "https://finopenapi.ssafy.io/ssafy/api/v1/member/search";
    private static final String API_KEY = "1b5cd29adccc46609ff1ce0a589584e0"; // 자동으로 추가될 API 키

    @Transactional
    public String save(AddUserRequest addUserRequestDto) throws Exception {
        String externalApiResponse = externalApiService.sendToExternalApi(addUserRequestDto);
        JsonNode responseJson = objectMapper.readTree(externalApiResponse);

        // 외부 API로 먼저 데이터 전송하여 userKey를 받아옴
        String userKey = responseJson.path("userKey").asText();

        // 받은 userKey와 함께 유저를 로컬 DB에 저장
        return String.valueOf(userRepository.save(User.builder()
                .name(addUserRequestDto.name())
                .email(addUserRequestDto.email())
                .password(bCryptPasswordEncoder.encode(addUserRequestDto.password())) // 패스워드 인코딩
                .user_key(userKey) // 외부 API로부터 받은 userKey 저장
                .build()).getUser_id());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public String refreshTokento;

    // 로그인 처리 로직
    public String login(LoginRequest loginRequest, HttpServletResponse response) {
        // 사용자 이메일로 데이터베이스에서 사용자 찾기
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // Access Token 발급
        String accessToken = tokenProvider.generateToken(user, Duration.ofDays(7));

        // Refresh Token 발급
        String refreshToken = tokenProvider.generateRefreshToken(user, Duration.ofDays(7));
        refreshTokento = refreshToken;
        // 기존 Refresh Token 확인
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(user.getUser_id());

        if (existingToken.isPresent()) {
            // 기존 토큰 업데이트
            RefreshToken tokenToUpdate = existingToken.get();
            tokenToUpdate.setRefreshToken(refreshToken);
            refreshTokenRepository.save(tokenToUpdate);
        } else {
            // 새로운 Refresh Token 저장
            refreshTokenRepository.save(
                    RefreshToken.builder()
                            .userId(user.getUser_id())
                            .email(user.getEmail())
                            .refreshToken(refreshToken)
                            .build());
        }

        // Refresh Token을 쿠키로 추가
        response.addCookie(createCookie("refreshToken", refreshToken));

        return accessToken;
    }

    // 외부 API를 통해 이메일 중복 확인
    private boolean checkEmailWithExternalApi(String email) {
        try {
            // 요청에 포함될 데이터 정의
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("userId", email);
            requestBody.put("apiKey", API_KEY); // 자동으로 apiKey 추가

            // HttpHeaders 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // 요청 본문과 헤더를 포함한 HttpEntity 생성
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 외부 API에 POST 요청 보내기
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity,
                    String.class);

            // 200 OK 응답이 오면 인증 실패(즉, 해당 이메일이 이미 존재)
            return !response.getStatusCode().is2xxSuccessful();

        } catch (HttpClientErrorException e) {
            // 400 Bad Request 또는 에러가 발생하면 인증 성공 (즉, 해당 이메일이 없음)
            if (e.getStatusCode().is4xxClientError()) {
                return true; // 인증 성공
            } else {
                // 그 외의 에러가 발생하면 false로 처리
                return false;
            }
        } catch (Exception e) {
            // 다른 예외 처리
            return false;
        }
    }

    // 내부 DB와 외부 API를 모두 확인하여 이메일 중복 체크
    public boolean checkEmailDuplication(String email) {
        // 내부 DB에서 이메일 중복 확인
        boolean isEmailInDb = !userRepository.existsByEmail(email);

        // 외부 API에서 이메일 중복 확인
        boolean isEmailInExternalApi = checkEmailWithExternalApi(email);

        // 두 조건을 모두 만족해야 중복이 아닌 것으로 처리
        return isEmailInDb && isEmailInExternalApi;
    }

    // 내부 DB와 외부 API를 모두 확인하여 이메일 중복 체크
    public boolean checkEmailDBDuplication(String email) {
        // 내부 DB에서 이메일 중복 확인
        boolean isEmailInDb = !userRepository.existsByEmail(email);

        return isEmailInDb;
    }

    // userId로 사용자 조회 및 payment_account_id 반환
    public UserPaymentAccountIdResponse getUserPaymentAccount(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new UserPaymentAccountIdResponse(user.getPayment_account_id());
        }

        return new UserPaymentAccountIdResponse(null);
    }

    public UserNameResponse getUserName(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new UserNameResponse(user.getUsername());
        }

        return new UserNameResponse(null);
    }

    public UserEmailResponse getUserEmail(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new UserEmailResponse(user.getUserEmail());
        }

        return new UserEmailResponse(null);
    }

    public boolean updateMainAccountId(Long userId, String mainAccountId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setMainAccountId(mainAccountId);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public String getMainAccountIdByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(User::getMain_account_id).orElse(null);
    }

    public String getMainAccountIdByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(User::getMain_account_id).orElse(null);
    }

    public User findByUserId(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
