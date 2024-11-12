package com.sparta.plate.service.store;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.entity.Store;
import com.sparta.plate.exception.StoreNotFoundException;
import com.sparta.plate.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteStoreService {

	private final StoreRepository storeRepository;

	@Transactional
	public void deleteStore(UUID storeId, Long userId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + storeId));

		store.markAsDeleted(userId);
	}
}
