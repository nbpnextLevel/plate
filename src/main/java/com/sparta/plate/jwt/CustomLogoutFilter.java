package com.sparta.plate.jwt;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.plate.dto.response.ApiResponseDto;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Logout Filter")
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private static final String BLACKLIST_PREFIX = "blacklist:";

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {
		doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException,
		IOException {

		// URL과 메소드 검증
		if (!request.getRequestURI().equals("/api/users/logout") || !request.getMethod().equals("POST")) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = jwtTokenProvider.getAccessTokenFromHeader(request);
		if (accessToken == null) {
			sendErrorResponse(response, "Access 토큰이 존재하지 않습니다.", HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String refresh = getRefreshToken(request);
		if (refresh == null) {
			sendErrorResponse(response, "Refresh 토큰이 존재하지 않습니다.", HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			validateRefresh(response, refresh);

			String loginIdFromToken = jwtTokenProvider.getLoginIdFromToken(refresh);

			if (!redisTemplate.hasKey(loginIdFromToken)) {
				sendErrorResponse(response, "저장된 리프레시 토큰을 찾을 수 없습니다.", HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			// logout
			Boolean isDeleted = redisTemplate.delete(loginIdFromToken);
			if(!isDeleted){
				log.error("[Redis] Refresh token delete failed for userId: {}", loginIdFromToken);
				sendErrorResponse(response, "로그아웃 처리에 실패했습니다.", HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			// 액세스 토큰 블랙리스트 추가
			blacklistAccessToken(accessToken);

			Cookie cookie = new Cookie("refresh", null);
			cookie.setMaxAge(0);
			cookie.setPath("/");
			cookie.setHttpOnly(true);

			response.addCookie(cookie);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			log.error("[Redis] logout failed ");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}

	private void blacklistAccessToken(String accessToken) {
		try {
			// 토큰의 남은 유효시간 계산
			Date expiration = jwtTokenProvider.getExpirationDateFromToken(accessToken);
			long remainingTime = (expiration.getTime() - System.currentTimeMillis()) / 1000;

			if (remainingTime > 0) {
				String blacklistKey = BLACKLIST_PREFIX + accessToken;
				redisTemplate.opsForValue().set(blacklistKey, "logout", remainingTime, TimeUnit.SECONDS);
				log.info("[Redis] Access token added to blacklist. Expires in {} seconds", remainingTime);
			}
		} catch (ExpiredJwtException e) {
			log.info("[Token] Access token is already expired");
		}
	}

	private static String getRefreshToken(HttpServletRequest request) {
		String refresh = null;

		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("refresh")) {
				refresh = cookie.getValue();
			}
		}

		return refresh;
	}

	private void validateRefresh(HttpServletResponse response, String refresh) {

		try {
			jwtTokenProvider.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			log.error("[Token] Refresh token is expired");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		boolean refreshToken = jwtTokenProvider.isRefreshToken(refresh);
		if(!refreshToken) {
			log.error("[Token] This is not a refresh token ");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

	}

	private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
		ApiResponseDto<Void> errorResponse = switch (status) {
			case HttpServletResponse.SC_BAD_REQUEST -> ApiResponseDto.error(message);
			case HttpServletResponse.SC_UNAUTHORIZED -> ApiResponseDto.unauthorized(message);
			case HttpServletResponse.SC_FORBIDDEN -> ApiResponseDto.forbidden(message);
			default -> ApiResponseDto.error(message);
		};

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(status);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(response.getWriter(), errorResponse);
	}

}
