package com.trabean.user.user.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trabean.user.config.jwt.TokenProvider;
import com.trabean.user.user.dto.LoginRequest;
import com.trabean.user.user.dto.UserNameResponse;
import com.trabean.user.user.dto.UserPaymentAccountIdResponse;
import com.trabean.user.user.entity.RefreshToken;
import com.trabean.user.user.repository.RefreshTokenRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trabean.user.user.controller.UserApiController;
import com.trabean.user.user.dto.AddUserRequest;
import com.trabean.user.user.entity.User;
import com.trabean.user.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ExternalApiService externalApiService; // 외부 API 호출을 위한 서비스 주입
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper
	private static final Logger logger = LoggerFactory.getLogger(UserApiController.class); // 로그를 위한 Logger 추가

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String save(AddUserRequest addUserRequestDto) throws Exception {
        String externalApiResponse = externalApiService.sendToExternalApi(addUserRequestDto);
        JsonNode responseJson = objectMapper.readTree(externalApiResponse);

        // 외부 API로 먼저 데이터 전송하여 userKey를 받아옴
//        String userKey = externalApiService.sendToExternalApi(addUserRequestDto);
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

    // 로그인 처리 로직
    public String login(LoginRequest loginRequest) {
        // 사용자 이메일로 데이터베이스에서 사용자 찾기
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // Access Token 발급
        String accessToken = tokenProvider.generateToken(user, Duration.ofMinutes(30));

        // Refresh Token 발급 및 저장
        String refreshToken = tokenProvider.generateToken(user, Duration.ofDays(7));
        logger.info("여기왔지롱");

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .email(user.getEmail())
                        .refreshToken(refreshToken)
                        .build()
        );

        return accessToken;
    }

    // userId로 사용자 조회 및 payment_account_id 반환
    public UserPaymentAccountIdResponse getUserPaymentAccount(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // User 객체를 UserResponse로 변환
            return new UserPaymentAccountIdResponse(user.getPayment_account_id());
        }

        // 사용자를 찾지 못한 경우 기본값을 반환
        return new UserPaymentAccountIdResponse(null);
    }

    public UserNameResponse getUserName(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // User 객체를 UserResponse로 변환
            return new UserNameResponse(user.getUsername());
        }

        // 사용자를 찾지 못한 경우 기본값을 반환
        return new UserNameResponse(null);
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
    public boolean checkEmailDuplication(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByUserId(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
