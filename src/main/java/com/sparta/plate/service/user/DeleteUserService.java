package com.sparta.plate.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.entity.User;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.exception.UnAuthorizedException;
import com.sparta.plate.exception.UserNotFoundException;
import com.sparta.plate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteUserService {

	private final UserRepository userRepository;

	@Transactional
	public void deleteUser(Long id, User user) {
		User savedUser = findUser(id);
		validateAuthorization(savedUser, user);
		savedUser.markAsDeleted(user.getId());
	}

	private User findUser(Long id) {
		return userRepository.findByIdAndIsDeletedFalse(id)
			.orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	private void validateAuthorization(User savedUser, User user) {
		if (! (savedUser.getId().equals(user.getId())
			|| UserRoleEnum.MANAGER.equals(user.getRole()) || UserRoleEnum.MASTER.equals(user.getRole())) ) {
			throw new UnAuthorizedException("Unauthorized user: " + user.getLoginId());
		}

		if(savedUser.getRole().equals(UserRoleEnum.MASTER) && user.getRole().equals(UserRoleEnum.MANAGER)){
			throw new UnAuthorizedException("Unauthorized user: " + user.getLoginId());
		}
	}
}
