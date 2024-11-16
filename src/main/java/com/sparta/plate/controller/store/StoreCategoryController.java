package com.sparta.plate.controller.store;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.StoreCategoryResponseDto;
import com.sparta.plate.entity.StoreCategory;
import com.sparta.plate.service.store.StoreCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "StoreCategory Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class StoreCategoryController {

	private final StoreCategoryService storeCategoryService;

	@GetMapping
	@Operation(summary = "음식점 카테고리 조회", description = "이미 저장된 음식점 카테고리 조회")
	public ApiResponseDto<List<StoreCategoryResponseDto>> getAllCategories() {
		List<StoreCategory> storeCategories = storeCategoryService.getAllStoreCategories();

		return ApiResponseDto.success(StoreCategoryResponseDto.listOf(storeCategories));
	}
}
