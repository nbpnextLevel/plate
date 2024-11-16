package com.sparta.plate.service.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.dto.request.UpdateUserRequestDto;
import com.sparta.plate.entity.User;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.exception.UnAuthorizedException;
import com.sparta.plate.exception.UserNotFoundException;
import com.sparta.plate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateUserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public User updateUser(Long id, UpdateUserRequestDto request, User currentUser) {

		User savedUser = findUser(id);

		boolean isSelfUpdate = isSelfUpdate(currentUser, savedUser);
		boolean isAdmin = isAdmin(currentUser);

		validateAuthorization(isSelfUpdate, isAdmin, savedUser, currentUser);

		if(isSelfUpdate) { // 본인거 수정하는 경우
			selfUpdate(request, savedUser);
		} else { // 매니저, 마스터가 고객, 오너 정보수정하는 경우
			if(request.getPassword() != null) {
				throw new IllegalArgumentException("비밀번호는 당사자에 의해서만 변경 가능합니다.");
			}
			savedUser.updateWithoutPassword(request);
		}

		return savedUser;
	}

	private boolean isSelfUpdate(User currentUser, User savedUser) {
		return savedUser.getId().equals(currentUser.getId());
	}

	private boolean isAdmin(User currentUser) {
		return UserRoleEnum.MANAGER.equals(currentUser.getRole())
			|| UserRoleEnum.MASTER.equals(currentUser.getRole());
	}

	private void selfUpdate(UpdateUserRequestDto request, User savedUser) {
		String encodePassword = passwordEncoder.encode(request.getPassword());
		savedUser.updateAllInfo(request, encodePassword);
	}

	private User findUser(Long id) {
		return userRepository.findByIdAndIsDeletedFalse(id)
			.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
	}

	private void validateAuthorization(boolean isSelfUpdate, boolean isAdmin, User savedUser, User currentUser) {

		if (!isSelfUpdate && !isAdmin) {
			throw new UnAuthorizedException("Unauthorized user: " + currentUser.getLoginId());
		}

		if (!isSelfUpdate
			&& savedUser.getRole().equals(UserRoleEnum.MASTER)
			&& currentUser.getRole().equals(UserRoleEnum.MANAGER)) {
			throw new UnAuthorizedException("Manager cannot modify Master user " + currentUser.getLoginId());
		}
	}
}
