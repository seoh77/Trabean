package com.trabean.user.user.service;

import org.springframework.stereotype.Service;

import com.trabean.user.user.entity.RefreshToken;
import com.trabean.user.user.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	// refresh token 저장
	public void saveRefreshToken(Long user_id, String email, String refreshToken) {
		Optional<RefreshToken> existingToken = refreshTokenRepository.findByEmail(email);
		if (existingToken.isPresent()) {
			//기존에 토큰 있음 업데이트!
			existingToken.get().updateValue(refreshToken);
			refreshTokenRepository.save(existingToken.get());
		} else {
			// 새토큰 저장
			RefreshToken newToken = RefreshToken.builder()
					.userId(user_id)
					.email(email)
					.refreshToken(refreshToken)
					.build();
			refreshTokenRepository.save(newToken);

		}
	}

	// 리프레시 토큰 확인
	public boolean validateRefreshToken(String email, String refreshToken) {
		Optional<RefreshToken> storedToken = refreshTokenRepository.findByEmail(email);
		return storedToken.isPresent() && storedToken.get().getRefreshToken().equals(refreshToken);
	}

	// 리프레시 토큰 삭제 (로그아웃 시...혹은 기한후?)
	public void deleteRefreshToken(String email) {
		refreshTokenRepository.deleteByEmail(email);
	}
	public RefreshToken findByRefreshToken(String refreshToken) {
		return refreshTokenRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new IllegalArgumentException("Unexpected Token"));
	}
}
