package com.sparta.plate.controller;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

@Slf4j(topic = "User Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserSignupService userSignupService;
	private final CheckDuplicatedLoginIdService checkDuplicatedLoginIdService;

	@PostMapping("/signup")
	public SignupUserResponseDto signup(@Valid @RequestBody SignupRequestDto request, BindingResult bindingResult) {

		List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		if (fieldErrors.size() > 0) {
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
			}
			return null;
		}

		User savedUser = userSignupService.signup(request);

		return new SignupUserResponseDto(savedUser.getLoginId());


	}

	@GetMapping("/exists/{loginId}")
	public boolean checkUserExists(@PathVariable("loginId") String loginId) {
		return checkDuplicatedLoginIdService.service(loginId);
	}

}
