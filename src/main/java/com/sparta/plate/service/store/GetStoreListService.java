package com.sparta.plate.service.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.dto.response.StoreResponseDto;
import com.sparta.plate.entity.Store;
import com.sparta.plate.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetStoreListService {

	private final StoreRepository storeRepository;

	@Transactional(readOnly = true)
	public Page<StoreResponseDto> getStoreList(int page, int size, String sortBy, boolean isAsc, String search) {

		// TODO 페이징 유틸로 만들기
		// sortBy : createdAt, updatedAt만 가능
		// 페이지사이즈 10, 30, 50 이외는 제한
		Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
		Sort sort = Sort.by(direction, sortBy);
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<Store> storeList;

		if(search == null || search.trim().isEmpty()) {
			storeList = storeRepository.findByIsDeletedFalse(pageable);
		} else {
			storeList = storeRepository.findByStoreNameContainingAndIsDeletedFalse(pageable, search);
		}

		return storeList.map(StoreResponseDto::of);
	}

}