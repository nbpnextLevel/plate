package com.sparta.plate.service.user;

import org.springframework.stereotype.Service;

import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReIssueService {

	private final JwtTokenProvider jwtTokenProvider;

	public String reissueAccessToken(String refreshToken) {
		validateToken(refreshToken);

		String loginId = jwtTokenProvider.getLoginIdFromToken(refreshToken);
		String role = jwtTokenProvider.getRoleFromToken(refreshToken);

		return jwtTokenProvider.createAccessToken(loginId, UserRoleEnum.valueOf(role.replace("ROLE_", "")));
	}

	public String reissueRefreshToken(String refreshToken) {
		validateToken(refreshToken);

		String loginId = jwtTokenProvider.getLoginIdFromToken(refreshToken);
		String role = jwtTokenProvider.getRoleFromToken(refreshToken);

		return jwtTokenProvider.createRefreshToken(loginId, UserRoleEnum.valueOf(role.replace("ROLE_", "")));
	}

	private void validateToken(String refreshToken) {
		jwtTokenProvider.validateToken(refreshToken);
		if(!jwtTokenProvider.isRefreshToken(refreshToken)) {
			throw new IllegalArgumentException("invalid refresh token");
		}
	}

}