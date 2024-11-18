package com.sparta.plate.dto.request;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreSearchRequestDto {
	private int page;
	private int size;
	private String sortBy;
	private boolean isAsc;
	private UUID categoryId;
	private String storeName;
	private String address;

	public void validate() {
		validatePageSize();
		validateSortBy();
	}

	private void validatePageSize() {
		if (!Arrays.asList(10, 30, 50).contains(size)) {
			throw new InvalidParameterException("페이지 사이즈는 10, 30, 50만 가능합니다.");
		}
	}

	private void validateSortBy() {
		if (!Arrays.asList("createdAt", "updatedAt").contains(sortBy)) {
			throw new InvalidParameterException("정렬 기준은 createdAt, updatedAt만 가능합니다.");
		}
	}

	public Pageable getPageable() {
		Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
		return PageRequest.of(page, size, Sort.by(direction, sortBy));
	}
}
