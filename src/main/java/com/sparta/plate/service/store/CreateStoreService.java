package com.sparta.plate.service.store;

import org.springframework.stereotype.Service;

import com.sparta.plate.dto.request.CreateStoreRequestDto;
import com.sparta.plate.entity.Store;
import com.sparta.plate.entity.StoreCategory;
import com.sparta.plate.repository.StoreCategoryRepository;
import com.sparta.plate.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateStoreService {

	private final StoreRepository storeRepository;
	private final StoreCategoryRepository storeCategoryRepository;

	public Store createStore(CreateStoreRequestDto request) {

		StoreCategory storeCategory = findStoreCategoryById(request);

		Store store = Store.builder()
			.storeName(request.getName())
			.address(request.getAddress())
			.storeNumber(request.getStoreNumber())
			.storeCategory(storeCategory)
			.build();

		Store savedStore = storeRepository.saveAndFlush(store);

		// TODO Store set createdBy 필요

		return savedStore;
	}

	private StoreCategory findStoreCategoryById(CreateStoreRequestDto request) {
		return storeCategoryRepository.findById(request.getCategoryId())
			.orElseThrow(() -> new IllegalArgumentException("Invalid category id"));
	}

}


