package com.trabean.user.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "refresh_token")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="token_id", nullable = false)
	private Long token_id;  // SQL 테이블의 BIGINT 타입 id와 매핑됨

	@Column(nullable = false, length = 100)
	private String email;  // SQL 테이블의 email과 매핑됨

	@Setter
    @Column(name = "refresh_token", nullable = false, length = 512)
	private String refreshToken;  // SQL 테이블의 refresh_token과 매핑됨

	@Column(name="user_id",nullable = false)
	private Long userId;
	public RefreshToken updateValue(String token) {
		this.refreshToken = token;
		return this;
	}

}
