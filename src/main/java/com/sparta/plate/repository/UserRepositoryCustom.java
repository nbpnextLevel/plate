package com.sparta.plate.repository;

import org.springframework.data.domain.Page;

import com.sparta.plate.dto.request.UserSearchRequestDto;
import com.sparta.plate.entity.User;

public interface UserRepositoryCustom {
	Page<User> searchUsers(UserSearchRequestDto request);
}
