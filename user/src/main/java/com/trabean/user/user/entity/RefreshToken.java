package com.trabean.user.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
	@Id
	@Column(name = "RT_KEY")
	private String key; //member id 들어감

	@Column(name = "RT_VALUE")
	private String value;// token값 들어감

	public RefreshToken updateValue(String token) {
		this.value = token;
		return this;
	}
}
