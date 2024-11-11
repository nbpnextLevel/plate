package com.sparta.plate.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sparta.plate.entity.User;
import com.sparta.plate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/*
	UserDetailsService interface 구현체로 DB에 접근하여 사용자 정보 가져옴
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		User user = userRepository.findByLoginId(loginId)
			.orElseThrow(() -> new UsernameNotFoundException("Not Found login ID = " + loginId));

		return new UserDetailsImpl(user);
	}
}
