package com.sparta.plate.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.sparta.plate.dto.request.SignupRequestDto;
import com.sparta.plate.entity.UserRoleEnum;
import com.sparta.plate.repository.StoreRepository;
import com.sparta.plate.service.user.UserSignupService;

import lombok.RequiredArgsConstructor;

@Order(1)
@Component
@RequiredArgsConstructor
public class UserDataRunner implements ApplicationRunner {

	private final StoreRepository storeRepository;
	private final UserSignupService userSignupService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<SignupRequestDto> members = Arrays.asList(

			new SignupRequestDto("customer1111", "Customer1111@", "유저1", "customer1111@naver.com",
				"010-1111-1111", "경기도 성남시 분당구 네이버로23", UserRoleEnum.CUSTOMER, null),

			new SignupRequestDto("owner1111", "Owner1111@", "맥도날드", "owner1111@naver.com",
				"010-1411-1112", "경기도 성남시 분당구 판교로 23", UserRoleEnum.OWNER, "plate_owner"),

			new SignupRequestDto("manager1111", "Manager1111@", "플레이트 관리자 ", "manager1111@naver.com",
				"010-1411-1113", "서울시 강남구 테헤란로 23", UserRoleEnum.MANAGER, "plate_manager"),

			new SignupRequestDto("master1111", "Master1111@", "플레이트 마스터", "master1111@naver.com",
				"010-1411-1114", "서울시 송파구 올림픽로 23", UserRoleEnum.MASTER, "plate_master")
		);

		// 회원가입 실행
		for (SignupRequestDto member : members) {
			try {
				userSignupService.signup(member);
			} catch (Exception e) {
				System.out.println("Error creating member: " + member.getLoginId() + " - " + e.getMessage());
			}
		}
	}
}
