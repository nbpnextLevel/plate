package com.sparta.plate.repository;

import org.springframework.data.domain.Page;

import com.sparta.plate.dto.request.StoreSearchRequestDto;
import com.sparta.plate.entity.Store;

public interface StoreRepositoryCustom {

	Page<Store> searchStores(StoreSearchRequestDto request);
}
