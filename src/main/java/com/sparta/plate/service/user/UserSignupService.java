package com.sparta.plate.service.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.dto.request.SignupRequestDto;
import com.sparta.plate.entity.User;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSignupService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public User signup(SignupRequestDto request) {
		String loginId = request.getLoginId();
		String password = passwordEncoder.encode(request.getPassword());

		validateRequest(request);

		UserRoleEnum role = getUserRole(request);

		User user = User.builder()
			.loginId(loginId)
			.password(password)
			.nickname(request.getNickname())
			.role(role)
			.email(request.getEmail())
			.phone(request.getPhone())
			.isDeleted(false)
			.address(request.getAddress())
			.build();

		User savedUser = userRepository.save(user);

		return savedUser;
	}

	private void validateRequest(SignupRequestDto request) {
		checkDuplicatedLoginId(request.getLoginId());
		checkDuplicatedEmail(request.getEmail());
	}

	private void checkDuplicatedLoginId(String loginId) {
		if(userRepository.existsByLoginId(loginId)) {
			throw new IllegalArgumentException("이미 가입된 아이디입니다.");
		}
	}

	private void checkDuplicatedEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("중복된 Email 입니다.");
		}
	}

	private UserRoleEnum getUserRole(SignupRequestDto request) {
		return request.getRole().validateVerificationCode(request.getVerificationCode());
	}

}
