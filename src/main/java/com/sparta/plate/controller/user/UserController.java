package com.sparta.plate.controller.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.plate.dto.request.SignupRequestDto;
import com.sparta.plate.dto.response.SignupUserResponseDto;
import com.sparta.plate.entity.User;
import com.sparta.plate.service.user.CheckDuplicatedLoginIdService;
import com.sparta.plate.service.user.UserSignupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO 공통 응답 객체 적용 필요 - 재희
@Slf4j(topic = "User Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserSignupService userSignupService;
	private final CheckDuplicatedLoginIdService checkDuplicatedLoginIdService;

	@PostMapping("/signup")
	public SignupUserResponseDto signup(@Valid @RequestBody SignupRequestDto request) {
		User savedUser = userSignupService.signup(request);
		return new SignupUserResponseDto(savedUser.getLoginId());
	}

	@GetMapping("/exists/{loginId}")
	public boolean checkUserExists(@PathVariable("loginId") String loginId) {
		return checkDuplicatedLoginIdService.service(loginId);
	}

}
