package com.sparta.plate.service.store;

import org.springframework.stereotype.Service;

import com.sparta.plate.dto.request.StoreRequestDto;
import com.sparta.plate.entity.Store;
import com.sparta.plate.entity.StoreCategory;
import com.sparta.plate.entity.User;
import com.sparta.plate.exception.AlreadyStoreExsitException;
import com.sparta.plate.exception.StoreCategoryNotFoundException;
import com.sparta.plate.repository.StoreCategoryRepository;
import com.sparta.plate.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateStoreService {

	private final StoreRepository storeRepository;
	private final StoreCategoryRepository storeCategoryRepository;

	public Store createStore(StoreRequestDto request, User user) {

		validateUserHasNoStore(user);

		StoreCategory storeCategory = findStoreCategoryById(request);

		Store store = Store.builder()
			.user(user)
			.storeCategory(storeCategory)
			.storeName(request.getName())
			.storeNumber(request.getStoreNumber())
			.address(request.getAddress())
			.build();

		Store savedStore = storeRepository.save(store);

		return savedStore;
	}

	private void validateUserHasNoStore(User user) {
		storeRepository.findByUserIdAndIsDeletedFalse(user.getId())
			.ifPresent(store -> {
				throw new AlreadyStoreExsitException("해당 유저는 이미 가게를 생성했습니다. loginID = " + user.getLoginId());
			});
	}

	private StoreCategory findStoreCategoryById(StoreRequestDto request) {
		return storeCategoryRepository.findById(request.getCategoryId())
			.orElseThrow(() -> new StoreCategoryNotFoundException("Store Category not found with ID: " + request.getCategoryId()));
	}

}


