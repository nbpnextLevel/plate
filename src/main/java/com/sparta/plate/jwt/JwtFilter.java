package com.sparta.plate.jwt;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.security.UserDetailsServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 헤더(Authorization)에 있는 토큰을 꺼내 이상이 없는 경우 SecurityContext에 저장
 * Request 이전에 작동
 */

@Slf4j(topic = "AccessToken 검증 및 인가 Filter")
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsServiceImpl userDetailsService;
	private final RedisTemplate<String, String> redisTemplate;
	private static final String BLACKLIST_PREFIX = "blacklist:";

	public JwtFilter(JwtTokenProvider jwtTokenProvider, UserDetailsServiceImpl userDetailsService
	, RedisTemplate<String, String> redisTemplate) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.userDetailsService = userDetailsService;
		this.redisTemplate = redisTemplate;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = jwtTokenProvider.getAccessTokenFromHeader(request);

		if (StringUtils.hasText(token)) {

			// 블랙리스트 체크
			String blacklistKey = BLACKLIST_PREFIX + token;
			if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
				sendErrorResponse(response, "로그아웃된 토큰입니다.", HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			try {
				if (!jwtTokenProvider.validateToken(token)) {
					sendErrorResponse(response, "유효하지 않은 토큰입니다.", HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
			} catch (ExpiredJwtException e) {
				sendErrorResponse(response, "토큰이 만료되었습니다.", HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			if(!jwtTokenProvider.isAccessToken(token)) {
				sendErrorResponse(response, "유효하지 않은 액세스 토큰입니다.", HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			String loginId = jwtTokenProvider.getLoginIdFromToken(token);

			try {
				setAuthentication(loginId);
			} catch (Exception e) {
				sendErrorResponse(response, "인증 처리 중 오류가 발생했습니다.", HttpServletResponse.SC_UNAUTHORIZED);
				log.error(e.getMessage());
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	// 인증 처리
	private void setAuthentication(String loginId) {
		/*
			SecurityContextHolder 는 스프링 시큐리티가 인증된 사용자의 세부 정보를 보관하는 곳으로,
			스프링 시큐리티는 SecurityContextHolder 에 사용자 정보가 어떻게 저장되었는지 신경쓰지 않고
			정보를 담고 있다면 사용자가 인증되었다고 간주
		 */

		// 비어있는 SecurityContext 생성
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		// 인증 객체 생성
		Authentication authentication = createAuthentication(loginId);
		context.setAuthentication(authentication);

		// SecurityContextHolder에 context 직접 세팅
		SecurityContextHolder.setContext(context);
	}

	// 인증 객체 생성 = Authentication
	private Authentication createAuthentication(String loginId) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {

		ApiResponseDto<Void> errorResponse = status == HttpServletResponse.SC_FORBIDDEN
			? ApiResponseDto.forbidden(message)  // 403
			: ApiResponseDto.unauthorized(message);  // 401

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(status);

		String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
		response.getWriter().write(jsonResponse);
	}

}