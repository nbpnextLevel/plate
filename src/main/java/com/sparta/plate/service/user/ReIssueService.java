package com.sparta.plate.service.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.jwt.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Reissue Service")
@Service
@RequiredArgsConstructor
public class ReIssueService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	public Map<String, String> reissueToken(String refreshToken) {
		Map<String, String> tokenMap = new HashMap<>();

		validateToken(refreshToken);

		String loginIdFromToken = jwtTokenProvider.getLoginIdFromToken(refreshToken);
		String role = jwtTokenProvider.getRoleFromToken(refreshToken);

		String savedToken = redisTemplate.opsForValue().get(loginIdFromToken);
		if (savedToken == null) {
			log.error("[Redis] Login id not found : {}", loginIdFromToken);
			return null;
		} else {
			try {
				redisTemplate.delete(refreshToken);
				log.info("[Redis] Refresh token is deleted successfully : userId {} ", loginIdFromToken);
			} catch (RedisConnectionFailureException e) {
				log.error("[Redis] Redis Connection failed ");
			}
		}

		tokenMap.put("accessToken", reissueAccessToken(loginIdFromToken, role));
		tokenMap.put("refreshToken", reissueRefreshToken(loginIdFromToken, role));

		return tokenMap;
	}

	public String reissueAccessToken(String loginId, String role) {
		return jwtTokenProvider.createAccessToken(loginId, UserRoleEnum.valueOf(role.replace("ROLE_", "")));
	}

	public String reissueRefreshToken(String loginId, String role) {
		return jwtTokenProvider.createRefreshToken(loginId, UserRoleEnum.valueOf(role.replace("ROLE_", "")));
	}

	private void validateToken(String refreshToken) {
		try {
			jwtTokenProvider.validateToken(refreshToken);
		} catch (ExpiredJwtException e) {
			log.error("[Redis] Refresh token is expired : {}", refreshToken);
			throw new IllegalArgumentException("[Redis] Refresh token is expired");
		}

		if(!jwtTokenProvider.isRefreshToken(refreshToken)) {
			throw new IllegalArgumentException("[Token] This is not a refresh token ");
		}
	}

}