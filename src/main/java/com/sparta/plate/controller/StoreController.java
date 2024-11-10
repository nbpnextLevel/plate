package com.sparta.plate.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.plate.dto.request.CreateStoreRequestDto;
import com.sparta.plate.dto.response.CreteStoreResponseDto;
import com.sparta.plate.entity.Store;
import com.sparta.plate.service.store.CreateStoreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO 공통 응답객체 적용 필요
@Slf4j(topic = "Store Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

	private final CreateStoreService createStoreService;

	@PostMapping
	public CreteStoreResponseDto createStore(@Valid @RequestBody CreateStoreRequestDto request) {
		Store store = createStoreService.createStore(request);
		return new CreteStoreResponseDto(store.getId());
	}

}
