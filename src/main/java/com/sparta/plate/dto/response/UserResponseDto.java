package com.sparta.plate.dto.response;

import java.time.LocalDateTime;
import com.sparta.plate.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class UserResponseDto {

	private String loginId;
	private String nickname;
	private String email;
	private String phone;
	private String address;
	private String role;
	private LocalDateTime createdAt;

	public static UserResponseDto of(User user) {
		return UserResponseDto.builder()
			.loginId(user.getLoginId())
			.nickname(user.getNickname())
			.email(user.getEmail())
			.phone(user.getPhone())
			.address(user.getAddress())
			.role(user.getRole().name())
			.createdAt(user.getCreatedAt())
			.build();
	}
}
