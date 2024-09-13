package com.trabean.user.user.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.trabean.user.user.dto.AddUserRequest;
import com.trabean.user.user.entity.User;
import com.trabean.user.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ExternalApiService externalApiService; // 외부 API 호출을 위한 서비스 주입

    public String save(AddUserRequest addUserRequestDto) throws Exception {
        // 외부 API로 먼저 데이터 전송하여 userKey를 받아옴
        String userKey = externalApiService.sendToExternalApi(addUserRequestDto);

        // 받은 userKey와 함께 유저를 로컬 DB에 저장
        return String.valueOf(userRepository.save(User.builder()
                .name(addUserRequestDto.name())
                .email(addUserRequestDto.email())
                .password(bCryptPasswordEncoder.encode(addUserRequestDto.password())) // 패스워드 인코딩
                .user_key(userKey) // 외부 API로부터 받은 userKey 저장
                .build()).getUser_id());
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
