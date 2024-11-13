package com.sparta.plate.controller.store;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.plate.dto.response.StoreCategoryResponseDto;
import com.sparta.plate.entity.StoreCategory;
import com.sparta.plate.service.store.StoreCategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO 공통 응답객체 적용 필요
@Slf4j(topic = "StoreCategory Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class StoreCategoryController {

	private final StoreCategoryService storeCategoryService;

	@GetMapping
	public List<StoreCategoryResponseDto> getAllCategories() {
		List<StoreCategory> storeCategories = storeCategoryService.getAllStoreCategories();

		return StoreCategoryResponseDto.listOf(storeCategories);
	}
}
