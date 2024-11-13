package com.sparta.plate.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sparta.plate.dto.response.UserResponseDto;
import com.sparta.plate.entity.Store;
import com.sparta.plate.entity.User;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.exception.UnAuthorizedException;
import com.sparta.plate.exception.UserNotFoundException;
import com.sparta.plate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetUserListService {
	private final UserRepository userRepository;

	public Page<UserResponseDto> getUserList(int page, int size, String sortBy, boolean isAsc, String search) {

		Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
		Sort sort = Sort.by(direction, sortBy);
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<User> userList;

		if (search == null || search.trim().isEmpty()) {
			userList = userRepository.findByIsDeletedFalse(pageable);
		} else {
			userList = userRepository.findByLoginIdContainingAndIsDeletedFalse(pageable, search);
		}

		return userList.map(UserResponseDto::of);
	}
}
