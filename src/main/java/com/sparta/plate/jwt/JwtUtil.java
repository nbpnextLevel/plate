package com.sparta.plate.jwt;

import java.security.Key;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/*
	Jwt 토큰 생성, 토큰 복호화, 정보추출, 토큰 유효성 검증 = JwtTokenProvider
 */
@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

	private static final String AUTHORIZATION_KEY = "auth";
	private static final String TOKEN_PREFIX = "Bearer ";

	private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;              // 30분
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일

	private String secretKey;
	private Key key;


}
