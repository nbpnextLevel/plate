package com.sparta.plate.service.store;

import org.springframework.stereotype.Service;

import com.sparta.plate.dto.request.CreateStoreRequestDto;
import com.sparta.plate.entity.Store;
import com.sparta.plate.entity.StoreCategory;
import com.sparta.plate.entity.User;
import com.sparta.plate.exception.StoreCategoryNotFoundException;
import com.sparta.plate.repository.StoreCategoryRepository;
import com.sparta.plate.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateStoreService {

	private final StoreRepository storeRepository;
	private final StoreCategoryRepository storeCategoryRepository;

	public Store createStore(CreateStoreRequestDto request, User user) {

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

	private StoreCategory findStoreCategoryById(CreateStoreRequestDto request) {
		return storeCategoryRepository.findById(request.getCategoryId())
			.orElseThrow(() -> new StoreCategoryNotFoundException("Store Category not found with ID: " + request.getCategoryId()));
	}

}


