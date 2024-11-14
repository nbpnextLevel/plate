package com.sparta.plate.service.store;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.dto.request.StoreSearchRequestDto;
import com.sparta.plate.dto.response.StoreResponseDto;
import com.sparta.plate.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetStoreListService {

	private final StoreRepository storeRepository;

	@Transactional(readOnly = true)
	public Page<StoreResponseDto> getStoreList(StoreSearchRequestDto request) {

		request.validate();

		return storeRepository.searchStores(request).map(StoreResponseDto::of);
	}
}