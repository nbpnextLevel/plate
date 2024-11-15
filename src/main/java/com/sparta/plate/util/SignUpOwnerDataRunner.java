// package com.sparta.plate.util;
//
// import java.util.Arrays;
// import java.util.List;
//
// import org.springframework.boot.ApplicationArguments;
// import org.springframework.boot.ApplicationRunner;
// import org.springframework.core.annotation.Order;
// import org.springframework.stereotype.Component;
//
// import com.sparta.plate.dto.request.SignupRequestDto;
// import com.sparta.plate.entity.UserRoleEnum;
// import com.sparta.plate.repository.StoreRepository;
// import com.sparta.plate.service.user.UserSignupService;
//
// import lombok.RequiredArgsConstructor;
//
// @Order(1)
// @Component
// @RequiredArgsConstructor
// public class SignUpOwnerDataRunner implements ApplicationRunner {
//
// 	private final StoreRepository storeRepository;
// 	private final UserSignupService userSignupService;
//
// 	@Override
// 	public void run(ApplicationArguments args) throws Exception {
// 		List<SignupRequestDto> members = Arrays.asList(
// 			new SignupRequestDto("owner1100", "Owner3333@", "배민", "owner1100@naver.com",
// 				"010-1411-1111", "경기도 성남시 분당구 네이버로23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1101", "Owner3333@", "쿠팡이츠", "owner1101@naver.com",
// 				"010-1411-1112", "경기도 성남시 분당구 판교로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1102", "Owner3333@", "요기요", "owner1102@naver.com",
// 				"010-1411-1113", "서울시 강남구 테헤란로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1103", "Owner3333@", "배달의민족", "owner1103@naver.com",
// 				"010-1411-1114", "서울시 송파구 올림픽로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1104", "Owner3333@", "푸드플라이", "owner1104@naver.com",
// 				"010-1411-1115", "서울시 마포구 와우산로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1105", "Owner3333@", "딜리버리히어로", "owner1105@naver.com",
// 				"010-1411-1116", "서울시 강서구 공항대로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1106", "Owner3333@", "배달의명수", "owner1106@naver.com",
// 				"010-1411-1117", "인천시 연수구 센트럴로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1107", "Owner3333@", "배달의달인", "owner1107@naver.com",
// 				"010-1411-1118", "부산시 해운대구 센텀2로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1108", "Owner3333@", "맛집왕", "owner1108@naver.com",
// 				"010-1411-1119", "대구시 수성구 달구벌대로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1109", "Owner3333@", "먹방스타", "owner1109@naver.com",
// 				"010-1411-1120", "광주시 서구 상무중앙로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1110", "Owner3333@", "맛집사장", "owner1110@naver.com",
// 				"010-1411-1121", "대전시 유성구 대학로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1111", "Owner3333@", "음식달인", "owner1111@naver.com",
// 				"010-1411-1122", "울산시 남구 삼산로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1112", "Owner3333@", "요리왕", "owner1112@naver.com",
// 				"010-1411-1123", "세종시 한누리대로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1113", "Owner3333@", "셰프킹", "owner1113@naver.com",
// 				"010-1411-1124", "제주시 연동 연동로 23", UserRoleEnum.OWNER, "plate_owner"),
// 			new SignupRequestDto("owner1114", "Owner3333@", "맛의달인", "owner1114@naver.com",
// 				"010-1411-1125", "강원도 춘천시 춘천로 23", UserRoleEnum.OWNER, "plate_owner")
// 		);
//
// 		// 회원가입 실행
// 		for (SignupRequestDto member : members) {
// 			try {
// 				userSignupService.signup(member);
// 			} catch (Exception e) {
// 				System.out.println("Error creating member: " + member.getLoginId() + " - " + e.getMessage());
// 			}
// 		}
// 	}
// }
