package com.sparta.plate.dto.response;

import com.sparta.plate.entity.Store;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class StoreResponseDto {
	private String category;
	private String storeName;
	private String storeNumber;
	private String address;

	public static StoreResponseDto of(Store store) {
		return StoreResponseDto.builder()
			.category(store.getStoreCategory().getCategory())
			.storeName(store.getStoreName())
			.storeNumber(store.getStoreNumber())
			.address(store.getAddress())
			.build();
	}
}
