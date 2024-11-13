package com.sparta.plate.service.store;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sparta.plate.entity.StoreCategory;
import com.sparta.plate.repository.StoreCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreCategoryService {

	private final StoreCategoryRepository storeCategoryRepository;

	public List<StoreCategory> getAllStoreCategories() {
		return storeCategoryRepository.findAll();
	}
}
