package com.sparta.plate.service.store;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.entity.Store;
import com.sparta.plate.entity.User;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.exception.StoreNotFoundException;
import com.sparta.plate.exception.UnAuthorizedException;
import com.sparta.plate.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteStoreService {

	private final StoreRepository storeRepository;

	@Transactional
	public void deleteStore(UUID storeId, User user) {
		Store store = getStore(storeId);
		validateUpdateAuthorization(store, user);
		store.markAsDeleted(user.getId());
	}

	private Store getStore(UUID storeId) {
		return storeRepository.findByIdAndIsDeletedFalse(storeId)
			.orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + storeId));
	}

	private void validateUpdateAuthorization(Store store, User user) {
		if (! (store.getUser().getId().equals(user.getId())
			|| UserRoleEnum.MANAGER.equals(user.getRole()) || UserRoleEnum.MASTER.equals(user.getRole())) ) {
			throw new UnAuthorizedException("Unauthorized user: " + user.getLoginId());
		}
	}
}
