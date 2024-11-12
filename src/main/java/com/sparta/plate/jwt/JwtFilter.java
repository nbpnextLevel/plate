package com.sparta.plate.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sparta.plate.security.UserDetailsServiceImpl;

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

	public JwtFilter(JwtTokenProvider jwtTokenProvider, UserDetailsServiceImpl userDetailsService) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = jwtTokenProvider.getJwtFromHeader(request);

		if (StringUtils.hasText(token)) {

			if (!jwtTokenProvider.validateToken(token)) {
				log.error("Token Error");
				return;
			}

			if(!jwtTokenProvider.isAccessToken(token)) {
				response.getWriter().print("Invalid access token");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			String loginId = jwtTokenProvider.getLoginIdFromToken(token);

			try {
				setAuthentication(loginId);
			} catch (Exception e) {
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

}