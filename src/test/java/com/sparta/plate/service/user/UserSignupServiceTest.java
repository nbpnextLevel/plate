package com.sparta.plate.service.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.entity.User;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.repository.UserRepository;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class UserSignupServiceTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("회원가입 성공 테스트")
	void signUp() {

		// given
		String loginId = "test1234";
		String password = "Test1234@";
		String nickName = "test";
		UserRoleEnum role = UserRoleEnum.CUSTOMER;
		String email = "test@test.com";
		String phone = "010-1234-5678";
		String address = "서울시 서초구 123";

		String encodedPwd = passwordEncoder.encode(password);

		User user = User.builder()
			.loginId(loginId)
			.password(encodedPwd)
			.nickname(nickName)
			.role(role)
			.email(email)
			.phone(phone)
			.address(address)
			.build();

		// when
		userRepository.save(user);

		// then
		User savedUser = userRepository.findByLoginId(user.getLoginId()).orElseThrow();

		assertEquals(loginId, savedUser.getLoginId());
		assertTrue(passwordEncoder.matches(password, encodedPwd));
		assertEquals(nickName, savedUser.getNickname());
		assertEquals(role, savedUser.getRole());
		assertEquals(email, savedUser.getEmail());
		assertEquals(phone, savedUser.getPhone());
		assertEquals(address, savedUser.getAddress());
	}
}