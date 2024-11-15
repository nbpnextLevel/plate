package com.sparta.plate.jwt;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.GenericFilterBean;

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

		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("refresh")) {
				refresh = cookie.getValue();
			}
		}

		if (refresh == null) {
			log.error("Refresh cookie not found");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			validateRefresh(response, refresh);

			String loginIdFromToken = jwtTokenProvider.getLoginIdFromToken(refresh);

			if (!redisTemplate.hasKey(loginIdFromToken)) {
				log.error("[Redis] Login id not found : {} ", loginIdFromToken);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			// logout
			Boolean isDeleted = redisTemplate.delete(loginIdFromToken);
			if(!isDeleted){
				log.error("[Redis] refreshToken delete failed for key: " + loginIdFromToken);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

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

	private void validateRefresh(HttpServletResponse response, String refresh) {

		try {
			jwtTokenProvider.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			log.error("[Token] Refresh token expired");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		boolean refreshToken = jwtTokenProvider.isRefreshToken(refresh);
		if(!refreshToken) {
			log.error("[Token] Refresh token type validation failed ");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

	}

}
