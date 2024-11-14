package com.sparta.plate.service.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.jwt.JwtTokenProvider;
import com.sparta.plate.repository.RefreshRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReIssueService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshRepository refreshRepository;

	public String reissueAccessToken(String refreshToken) {
		validateToken(refreshToken);

		//DB에 저장되어 있는지 확인
		// Boolean isExist = refreshRepository.existsByRefresh(refresh);
		// if (!isExist) {
		//
		// 	//response body
		// 	return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		// }

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