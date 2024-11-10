package com.sparta.plate.dto.response;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sparta.plate.entity.StoreCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreCategoryResponseDto {
	private UUID storeCategoryId;
	private String category;

	public static List<StoreCategoryResponseDto> listOf(List<StoreCategory> categories){
		return categories.stream()
			.map(category -> new StoreCategoryResponseDto(category.getId(), category.getCategory()))
			.collect(Collectors.toList());
	}
}
