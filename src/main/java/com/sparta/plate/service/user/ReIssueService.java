package com.sparta.plate.service.user;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.jwt.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReIssueService {

	private final JwtTokenProvider jwtTokenProvider;

	public String reissueAccessToken(String refreshToken) {
		validateRefreshToken(refreshToken);

		String loginId = jwtTokenProvider.getLoginIdFromToken(refreshToken);
		System.out.println("login id: hhhh = " + loginId);
		String role = jwtTokenProvider.getRoleFromToken(refreshToken);
		role = role.replace("ROLE_", "");

		return jwtTokenProvider.createAccessToken(loginId, UserRoleEnum.valueOf(role));
	}

	private void validateRefreshToken(String refreshToken) {
		try {
			jwtTokenProvider.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			throw new IllegalArgumentException("Refresh Token is expired");
		}

		if(!jwtTokenProvider.isRefreshToken(refreshToken)) {
			throw new IllegalArgumentException("invalid refresh token");
		}
	}

}
