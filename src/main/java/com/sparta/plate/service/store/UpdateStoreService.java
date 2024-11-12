package com.sparta.plate.service.store;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.dto.request.StoreRequestDto;
import com.sparta.plate.entity.Store;
import com.sparta.plate.entity.StoreCategory;
import com.sparta.plate.entity.User;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.exception.StoreCategoryNotFoundException;
import com.sparta.plate.exception.StoreNotFoundException;
import com.sparta.plate.exception.UnAuthorizedException;
import com.sparta.plate.repository.StoreCategoryRepository;
import com.sparta.plate.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateStoreService {
	private final StoreRepository storeRepository;
	private final StoreCategoryRepository storeCategoryRepository;

	@Transactional
	public Store updateStore(UUID storeId, StoreRequestDto request, User user) {

		Store store = getStore(storeId);
		StoreCategory storeCategory = getCategory(request);

		validateUpdateAuthorization(store, user);
		store.update(request, user, storeCategory);

		return store;
	}

	private Store getStore(UUID storeId) {
		return storeRepository.findByIdAndIsDeletedFalse(storeId)
			.orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + storeId));
	}

	private StoreCategory getCategory(StoreRequestDto request) {
		return storeCategoryRepository.findById(request.getCategoryId())
			.orElseThrow(() -> new StoreCategoryNotFoundException("Store Category not found with ID: " + request.getCategoryId()));
	}

	private void validateUpdateAuthorization(Store store, User user) {
		if (! (store.getUser().getId().equals(user.getId())
			|| UserRoleEnum.MANAGER.equals(user.getRole()) || UserRoleEnum.MASTER.equals(user.getRole())) ) {
			throw new UnAuthorizedException("Unauthorized user: " + user.getLoginId());
		}
	}
}
