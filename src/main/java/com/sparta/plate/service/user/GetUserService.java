package com.sparta.plate.service.user;

import org.springframework.stereotype.Service;

import com.sparta.plate.entity.User;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.exception.UnAuthorizedException;
import com.sparta.plate.exception.UserNotFoundException;
import com.sparta.plate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetUserService {
	private final UserRepository userRepository;

	public User getUser(Long id, User user) {

		User findUser = findUser(id);
		validateAuthorization(user, findUser);
		return findUser;
	}

	private User findUser(Long id) {
		return userRepository.findByIdAndIsDeletedFalse(id)
			.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
	}

	private static void validateAuthorization(User user, User findUser) {
		if(! (findUser.getId().equals(user.getId())
			|| UserRoleEnum.MASTER.equals(user.getRole()) || UserRoleEnum.MANAGER.equals(user.getRole())) ) {
			throw new UnAuthorizedException("Unauthorized user: " + user.getLoginId());
		}
	}
}
