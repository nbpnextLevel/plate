package com.sparta.plate.service.store;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sparta.plate.entity.Store;
import com.sparta.plate.exception.StoreNotFoundException;
import com.sparta.plate.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetStoreService {

	private final StoreRepository storeRepository;

	public Store getStore(UUID storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + storeId));
	}
}
