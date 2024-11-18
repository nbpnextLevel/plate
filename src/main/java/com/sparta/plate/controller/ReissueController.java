package com.sparta.plate.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.service.user.ReIssueService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
서버측 JWTFilter에서 Access 토큰의 만료로 인한 특정한 상태 코드가 응답되면
프론트측 Axios Interceptor와 같은 예외 핸들러에서 Access 토큰 재발급을 위한 Refresh을 서버측으로 전송한다.
이때 서버에서는 Refresh 토큰을 받아 새로운 Access 토큰을 응답하는 코드
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ReissueController {
	private final ReIssueService reissueService;

	@PostMapping("/reissue")
	@Operation(summary = "액세스 토큰 재발급", description = "서버로부터 403 응답(만료된 토큰 에러)을 받은 경우, 리프레쉬 토큰을 가지고 액세스 토큰 재발급을 요청")
	public ApiResponseDto<Object> reissue(HttpServletRequest request, HttpServletResponse response) {

		String refreshToken = getRefreshToken(request);

		if (refreshToken == null) {
			return ApiResponseDto.error("리프레시 토큰이 존재하지 않습니다.");
		}

		Map<String, String> tokenMap = reissueService.reissueToken(refreshToken);

		response.setHeader("access", tokenMap.get("accessToken"));
		response.addCookie(createCookie("refresh", tokenMap.get("refreshToken")));

		return ApiResponseDto.success("토큰이 재발급되었습니다.");
	}

	private String getRefreshToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refresh")) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24*60*60);
		cookie.setHttpOnly(true);

		return cookie;
	}
}
