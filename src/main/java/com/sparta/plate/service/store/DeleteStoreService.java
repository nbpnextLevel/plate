package com.sparta.plate.service.store;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.entity.Store;
import com.sparta.plate.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteStoreService {

	private final StoreRepository storeRepository;

	@Transactional
	public void deleteStore(UUID storeId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Not found store with id " + storeId));

		// soft delete
		// TODO 유저 반영
		store.deleteStore(1L);
	}
}
