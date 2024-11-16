package com.sparta.plate.service.user;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.sparta.plate.dto.request.UserSearchRequestDto;
import com.sparta.plate.dto.response.UserResponseDto;
import com.sparta.plate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetUserListService {
	private final UserRepository userRepository;

	public Page<UserResponseDto> getUserList(UserSearchRequestDto request) {

		request.validate();

		return userRepository.searchUsers(request).map(UserResponseDto::of);
	}
}
