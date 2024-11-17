package com.sparta.plate.jwt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.plate.dto.request.LoginRequestDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.security.UserDetailsImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/*
	JWT 인증을 하기 위해 설치하는 Custom Filter로 UsernamePasswordAuthenticationFilter 이전에 실행
 */
@Slf4j(topic = "로그인 및 JWT 생성을 위한 Filter")
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final JwtTokenProvider jwtTokenProvider;

	public LoginFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
		setFilterProcessesUrl("/api/users/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		try {
			LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

			// AuthenticationManager가 인증 처리
			// 인자로 받은 Authentication이 유효한 인증인지 확인하고 Authentication 객체를 반환한다
			return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(
					requestDto.getLoginId(),
					requestDto.getPassword(),
					null
				)
			);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	/*
	 로그인 성공 시 access, refresh 토큰 발행: authentication에서 userName, role 추출
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		FilterChain chain, Authentication authentication) throws IOException {

		String loginId = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
		UserRoleEnum role = ((UserDetailsImpl) authentication.getPrincipal()).getUser().getRole();

		String accessToken = jwtTokenProvider.createAccessToken(loginId, role);
		String refreshToken = jwtTokenProvider.createRefreshToken(loginId, role);

		response.setHeader("access", accessToken);
		response.addCookie(createCookie("refresh", refreshToken));

		ApiResponseDto<Object> responseDto = ApiResponseDto.success("로그인에 성공했습니다.");
		sendJsonResponse(response, responseDto);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws
		IOException {

		ApiResponseDto<Object> responseDto = ApiResponseDto.unauthorized("로그인 실패 = 자격 증명에 실패하였습니다.");
		sendJsonResponse(response, responseDto);
	}

	private Cookie createCookie(String key, String value) throws UnsupportedEncodingException {

		Cookie cookie = new Cookie(key, URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
		cookie.setMaxAge(24*60*60);
		cookie.setHttpOnly(true);

		return cookie;
	}

	private void sendJsonResponse(HttpServletResponse response, ApiResponseDto<?> responseDto) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		new ObjectMapper().writeValue(response.getWriter(), responseDto);
	}
}
