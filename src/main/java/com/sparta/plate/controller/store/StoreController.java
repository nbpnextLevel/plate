package com.sparta.plate.controller.store;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.plate.dto.request.CreateStoreRequestDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.CreteStoreResponseDto;
import com.sparta.plate.dto.response.StoreResponseDto;
import com.sparta.plate.entity.Store;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.store.CreateStoreService;
import com.sparta.plate.service.store.DeleteStoreService;
import com.sparta.plate.service.store.GetStoreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Store Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

	private final CreateStoreService createStoreService;
	private final GetStoreService storeService;
	private final DeleteStoreService deleteStoreService;

	@PostMapping
	public CreteStoreResponseDto createStore(@Valid @RequestBody CreateStoreRequestDto request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		Store store = createStoreService.createStore(request, userDetails.getUser());
		return new CreteStoreResponseDto(store.getId());
	}

	@GetMapping("/{storeId}")
	public ApiResponseDto<StoreResponseDto> getStore(@PathVariable("storeId") UUID storeId){
		Store store = storeService.getStore(storeId);
		StoreResponseDto responseDto = StoreResponseDto.of(store);

		return ApiResponseDto.success(responseDto);
	}

	@DeleteMapping
	public void deleteStore(@RequestParam(name = "id") UUID storeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		deleteStoreService.deleteStore(storeId, userDetails.getUser().getId());
	}

}
