package com.sparta.plate.service.user;


import org.springframework.stereotype.Service;

import com.sparta.plate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CheckDuplicatedLoginIdService {

	private final UserRepository userRepository;

	public boolean service(String loginId) {
		return userRepository.existsByLoginId(loginId);
	}
}
