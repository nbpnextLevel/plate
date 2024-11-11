package com.sparta.plate.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sparta.plate.entity.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
	JwtAuthenticationFilter에서 인증되어 SecurityContextHolder에 저장된
	Authentication 객체 정보 가져오는 클래스
 */
@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

	private final User user;

	@Override
	public String getUsername() {
		return user.getLoginId();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String authority = user.getRole().getAuthority();

		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(simpleGrantedAuthority);

		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
