// package com.sparta.plate.util;
//
// import java.util.HashMap;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.UUID;
//
// import org.springframework.boot.ApplicationArguments;
// import org.springframework.boot.ApplicationRunner;
// import org.springframework.core.annotation.Order;
// import org.springframework.stereotype.Component;
//
// import com.sparta.plate.dto.request.StoreRequestDto;
// import com.sparta.plate.entity.StoreCategory;
// import com.sparta.plate.entity.User;
// import com.sparta.plate.repository.StoreCategoryRepository;
// import com.sparta.plate.repository.StoreRepository;
// import com.sparta.plate.repository.UserRepository;
// import com.sparta.plate.service.store.CreateStoreService;
//
// import lombok.RequiredArgsConstructor;
//
// @Order(3)
// @Component
// @RequiredArgsConstructor
// public class CreateStoreDataRunner implements ApplicationRunner {
//
// 	private final StoreRepository storeRepository;
// 	private final UserRepository userRepository;
// 	private final StoreCategoryRepository storeCategoryRepository;
// 	private final CreateStoreService createStoreService;
//
// 	@Override
// 	public void run(ApplicationArguments args) throws Exception {
// 		// 카테고리 맵 생성
// 		Map<String, UUID> categoryMap = new HashMap<>();
// 		List<StoreCategory> categories = storeCategoryRepository.findAll();
// 		for (StoreCategory category : categories) {
// 			categoryMap.put(category.getCategory(), category.getId());
// 		}
//
// 		// 사용자와 가게 정보 매핑
// 		Map<String, StoreRequestDto> storeData = new LinkedHashMap<>();
// 		storeData.put("owner1100", new StoreRequestDto(categoryMap.get("한식"), "청진동해장국 강남점", "02-555-1234", "서울시 강남구 테헤란로 123"));
// 		storeData.put("owner1101", new StoreRequestDto(categoryMap.get("중식"), "대박반점 홍대점", "02-332-9876", "서울시 마포구 와우산로 789"));
// 		storeData.put("owner1102", new StoreRequestDto(categoryMap.get("일식"), "스시혼 강남점", "02-987-6543", "서울시 강남구 선릉로 234"));
// 		storeData.put("owner1103", new StoreRequestDto(categoryMap.get("양식"), "빕스 강남점", "02-777-8888", "서울시 강남구 역삼로 456"));
// 		storeData.put("owner1104", new StoreRequestDto(categoryMap.get("분식"), "엽기떡볶이 신촌점", "02-444-5555", "서울시 서대문구 연세로 789"));
// 		storeData.put("owner1105", new StoreRequestDto(categoryMap.get("디저트"), "파리바게뜨 역삼점", "02-234-5678", "서울시 강남구 역삼동 890"));
// 		storeData.put("owner1106", new StoreRequestDto(categoryMap.get("패스트푸드"), "맥도날드 강남점", "02-666-7777", "서울시 강남구 강남대로 123"));
// 		storeData.put("owner1107", new StoreRequestDto(categoryMap.get("치킨"), "교촌치킨 압구정점", "02-876-5432", "서울시 강남구 압구정로 456"));
// 		storeData.put("owner1108", new StoreRequestDto(categoryMap.get("피자"), "도미노피자 서울점", "02-789-4564", "서울시 강남구 역삼로 890"));
// 		storeData.put("owner1109", new StoreRequestDto(categoryMap.get("아시안"), "포베트남 신사점", "02-222-3333", "서울시 강남구 가로수길 123"));
// 		storeData.put("owner1110", new StoreRequestDto(categoryMap.get("고기"), "삼겹살달인 신논현점", "02-987-6543", "서울시 강남구 신논현로 456"));
// 		storeData.put("owner1111", new StoreRequestDto(categoryMap.get("해산물"), "대게나라 강남점", "02-765-4321", "서울시 강남구 역삼로 012"));
// 		storeData.put("owner1112", new StoreRequestDto(categoryMap.get("한식"), "할매순대국 종로점", "02-738-5678", "서울시 종로구 종로 456"));
// 		storeData.put("owner1113", new StoreRequestDto(categoryMap.get("중식"), "황금성 신촌점", "02-123-4567", "서울시 서대문구 연세로 101"));
// 		storeData.put("owner1114", new StoreRequestDto(categoryMap.get("일식"), "라멘조이 삼성점", "02-345-6789", "서울시 강남구 삼성로 567"));
//
// 		// 가게 생성
// 		for (Map.Entry<String, StoreRequestDto> entry : storeData.entrySet()) {
// 			try {
// 				String loginId = entry.getKey();
// 				StoreRequestDto storeRequest = entry.getValue();
//
// 				// 사용자 조회
// 				Optional<User> userOptional = userRepository.findByLoginId(loginId);
// 				if (userOptional.isPresent()) {
// 					User user = userOptional.get();
// 					createStoreService.createStore(storeRequest, user);
// 					System.out.println("Store created successfully for user: " + loginId);
// 				} else {
// 					System.out.println("User not found: " + loginId);
// 				}
// 			} catch (Exception e) {
// 				System.out.println("Error creating store for user " + entry.getKey() + ": " + e.getMessage());
// 			}
// 		}
//
// 	}
// }
