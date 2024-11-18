package com.sparta.plate.util;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.plate.entity.StoreCategory;
import com.sparta.plate.repository.StoreCategoryRepository;

import lombok.RequiredArgsConstructor;

@Order(2)
@Component
@RequiredArgsConstructor
public class StoreCategoryDataRunner implements ApplicationRunner {

	private final StoreCategoryRepository storeCategoryRepository;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		// 이미 데이터가 있는지 확인
		if (storeCategoryRepository.count() > 0) {
			return;
		}

		// 기본 카테고리 목록 생성
		List<StoreCategory> categories = Arrays.asList(
			new StoreCategory(UUID.randomUUID(), "한식"),
			new StoreCategory(UUID.randomUUID(), "중식"),
			new StoreCategory(UUID.randomUUID(), "일식"),
			new StoreCategory(UUID.randomUUID(), "양식"),
			new StoreCategory(UUID.randomUUID(), "분식"),
			new StoreCategory(UUID.randomUUID(), "디저트"),
			new StoreCategory(UUID.randomUUID(), "패스트푸드"),
			new StoreCategory(UUID.randomUUID(), "치킨"),
			new StoreCategory(UUID.randomUUID(), "피자"),
			new StoreCategory(UUID.randomUUID(), "아시안"),
			new StoreCategory(UUID.randomUUID(), "고기"),
			new StoreCategory(UUID.randomUUID(), "해산물")
		);

		// 카테고리 저장
		storeCategoryRepository.saveAll(categories);
	}
}
