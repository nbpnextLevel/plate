package com.sparta.plate.controller.store;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.plate.dto.request.StoreByAdminRequestDto;
import com.sparta.plate.dto.request.StoreRequestDto;
import com.sparta.plate.dto.request.StoreSearchRequestDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.StoreResponseDto;
import com.sparta.plate.entity.Store;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.store.CreateStoreService;
import com.sparta.plate.service.store.DeleteStoreService;
import com.sparta.plate.service.store.GetStoreListService;
import com.sparta.plate.service.store.GetStoreService;
import com.sparta.plate.service.store.UpdateStoreService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Store Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

	private final CreateStoreService createStoreService;
	private final GetStoreListService getStoreListService;
	private final GetStoreService getStoreService;
	private final UpdateStoreService updateStoreService;
	private final DeleteStoreService deleteStoreService;

	@PostMapping
	@Operation(summary = "가게 생성", description = "OWNER, MANAGER, MASTER 권한의 유저만 가게 생성 가능")
	public ApiResponseDto<Map<String, Object>> createStore(@Valid @RequestBody StoreRequestDto request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		Store store = createStoreService.createStore(request, userDetails.getUser());
		return ApiResponseDto.success(Map.of("uuid", store.getId()));
	}

	@PostMapping("/admin")
	@Operation(summary = "관리자에 의한 가게 생성", description = "MASTER 권한의 유저가 특정 CUSTOMER에 대해 가게 생성 가능")
	public ApiResponseDto<Map<String, Object>> createStoreByAdmin(@Valid @RequestBody StoreByAdminRequestDto request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		Store store = createStoreService.createStoreByAdmin(request, userDetails.getUser());
		return ApiResponseDto.success(Map.of("uuid", store.getId()));
	}

	@GetMapping
	@Operation(summary = "가게 리스트 조회", description = "등록된 가게 리스트를 조회할 수 있음. 검색 필터는 카테고리, 가게명, 가게주소에 적용")
	public ApiResponseDto<List<StoreResponseDto>> getAllStores(
		@RequestParam("page") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam("sortBy") String sortBy,
		@RequestParam("isAsc") boolean isAsc,
		@RequestParam(value = "categoryId", required = false) UUID categoryId,
		@RequestParam(value = "storeName", required = false) String storeName,
		@RequestParam(value = "address", required = false) String address
	) {
		StoreSearchRequestDto request = StoreSearchRequestDto.builder()
			.page(page-1)
			.size(size)
			.sortBy(sortBy)
			.isAsc(isAsc)
			.categoryId(categoryId)
			.storeName(storeName)
			.address(address)
			.build();

		Page<StoreResponseDto> storeList = getStoreListService.getStoreList(request);

		return ApiResponseDto.successPage(storeList);
	}

	@GetMapping("/{storeId}")
	@Operation(summary = "가게 단건 조회", description = "가게 단건 조회" )
	public ApiResponseDto<StoreResponseDto> getStore(@PathVariable("storeId") UUID storeId){
		Store store = getStoreService.getStore(storeId);
		StoreResponseDto responseDto = StoreResponseDto.of(store);

		return ApiResponseDto.success(responseDto);
	}

	@PatchMapping("/{storeId}")
	@Operation(summary = "가게 정보수정", description = "OWNER, MANAGER, MASTER만 수정 가능")
	public ApiResponseDto<UUID> updateStore(@PathVariable("storeId") UUID storeId, @Valid @RequestBody StoreRequestDto request,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		Store store = updateStoreService.updateStore(storeId, request, userDetails.getUser());

		return ApiResponseDto.success(store.getId());
	}

	@DeleteMapping("/{storeId}")
	@Operation(summary = "가게 정보삭제", description = "OWNER, MANAGER, MASTER만 삭제 가능")
	public ApiResponseDto<?> deleteStore(@PathVariable(name = "storeId") UUID storeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		deleteStoreService.deleteStore(storeId, userDetails.getUser());
		return ApiResponseDto.successDelete();
	}

}
