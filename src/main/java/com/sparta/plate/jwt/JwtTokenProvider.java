package com.sparta.plate.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sparta.plate.entity.UserRoleEnum;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
	Jwt 토큰 생성, 토큰 복호화, 정보추출, 토큰 유효성 검증
 */
@Slf4j(topic = "JwtTokenProvider")
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final RedisTemplate<String, String> redisTemplate;

	public static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_KEY = "auth";
	private static final String BEARER_PREFIX = "Bearer ";

	private static final String TOKEN_TYPE = "type";
	private static final String ACCESS_TOKEN = "access";
	private static final String REFRESH_TOKEN = "refresh";

	private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;           // 30분
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000L;    // 24시간

	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	public String createAccessToken(String loginId, UserRoleEnum role){
		return Jwts.builder()
			.setSubject(loginId)
			.claim(TOKEN_TYPE, ACCESS_TOKEN)
			.claim(AUTHORIZATION_KEY, role.getAuthority())
			.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.signWith(key, signatureAlgorithm) // JWT 서명 키
			.compact();
	}

	public String createRefreshToken(String loginId, UserRoleEnum role){

		String refreshToken = Jwts.builder()
			.setSubject(loginId)
			.claim(TOKEN_TYPE, REFRESH_TOKEN)
			.claim(AUTHORIZATION_KEY, role.getAuthority())
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.signWith(key, signatureAlgorithm) // JWT 서명 키
			.compact();

		try {
			redisTemplate.opsForValue().set( // key value timeout timeUnit
				loginId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS
			);
		} catch (RedisConnectionFailureException e) {
			log.error("[Redis] Redis Connection failed ");
		}


		return refreshToken;
	}

	// 토큰에서 로그인 아이디 가져오기
	public String getLoginIdFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("sub", String.class);
	}

	// 토큰에서 유저권한 가져오기
	public String getRoleFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("auth", String.class);
	}

	// 토큰에서 사용자 정보 가져오기
	public Claims getUserInfoFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	// 만료된 토큰 여부 조회하기
	public Boolean isExpired(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().before((new Date()));
	}

	public boolean isAccessToken(String token){
		Claims claims = getUserInfoFromToken(token);
		return ACCESS_TOKEN.equals(claims.get(TOKEN_TYPE));
	}

	public boolean isRefreshToken(String token) {
		Claims claims = getUserInfoFromToken(token);
		return REFRESH_TOKEN.equals(claims.get(TOKEN_TYPE));
	}

	// HTTP 요청헤더에서 bearer 토큰을 가져옴.
	public String getJwtFromHeader(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	// Token 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token, 만료된 JWT token 입니다.");
			throw e;
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return false;
	}


}