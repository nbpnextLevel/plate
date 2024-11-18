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

			new SignupRequestDto("test1111", "Test1111@", "test1111", "test1111@naver.com",
				"49-4492-0103", "서울시 마포구 와우산로 123", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test2222", "Test2222@", "test2222", "test2222@naver.com",
				"49-4492-0104", "서울시 마포구 와우산로 124", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test3333", "Test3333@", "test3333", "test3333@naver.com",
				"49-4492-0105", "서울시 마포구 와우산로 125", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test4444", "Test4444@", "test4444", "test4444@naver.com",
				"49-4492-0106", "서울시 마포구 와우산로 126", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test5555", "Test5555@", "test5555", "test5555@naver.com",
				"49-4492-0107", "서울시 마포구 와우산로 127", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test6666", "Test6666@", "test6666", "test6666@naver.com",
				"49-4492-0108", "서울시 마포구 와우산로 128", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test7777", "Test7777@", "test7777", "test7777@naver.com",
				"49-4492-0109", "서울시 마포구 와우산로 129", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test8888", "Test8888@", "test8888", "test8888@naver.com",
				"49-4492-0110", "서울시 마포구 와우산로 130", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test9999", "Test9999@", "test9999", "test9999@naver.com",
				"49-4492-0111", "서울시 마포구 와우산로 131", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test1010", "Test1010@", "test1010", "test1010@naver.com",
				"49-4492-0112", "서울시 마포구 와우산로 132", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test1112", "Test1112@", "test1112", "test1112@naver.com",
				"49-4492-0113", "서울시 마포구 와우산로 133", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test1212", "Test1212@", "test1212", "test1212@naver.com",
				"49-4492-0114", "서울시 마포구 와우산로 134", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test1313", "Test1313@", "test1313", "test1313@naver.com",
				"49-4492-0115", "서울시 마포구 와우산로 135", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test1414", "Test1414@", "test1414", "test1414@naver.com",
				"49-4492-0116", "서울시 마포구 와우산로 136", UserRoleEnum.CUSTOMER, null),
			new SignupRequestDto("test1515", "Test1515@", "test1515", "test1515@naver.com",
				"49-4492-0117", "서울시 마포구 와우산로 137", UserRoleEnum.CUSTOMER, null),

			new SignupRequestDto("owner1111", "Owner1111@", "owner1111", "owner1111@naver.com",
				"49-4492-0103", "서울시 마포구 성산대로 2", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner2222", "Owner2222@", "owner2222", "owner2222@naver.com",
				"49-4492-0104", "서울시 마포구 성산대로 3", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner3333", "Owner3333@", "owner3333", "owner3333@naver.com",
				"49-4492-0105", "서울시 마포구 성산대로 4", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner4444", "Owner4444@", "owner4444", "owner4444@naver.com",
				"49-4492-0106", "서울시 마포구 성산대로 5", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner5555", "Owner5555@", "owner5555", "owner5555@naver.com",
				"49-4492-0107", "서울시 마포구 성산대로 6", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner6666", "Owner6666@", "owner6666", "owner6666@naver.com",
				"49-4492-0108", "서울시 마포구 성산대로 7", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner7777", "Owner7777@", "owner7777", "owner7777@naver.com",
				"49-4492-0109", "서울시 마포구 성산대로 8", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner8888", "Owner8888@", "owner8888", "owner8888@naver.com",
				"49-4492-0110", "서울시 마포구 성산대로 9", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner9999", "Owner9999@", "owner9999", "owner9999@naver.com",
				"49-4492-0111", "서울시 마포구 성산대로 10", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner1010", "Owner1010@", "owner1010", "owner1010@naver.com",
				"49-4492-0112", "서울시 마포구 성산대로 11", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner1112", "Owner1112@", "owner1112", "owner1112@naver.com",
				"49-4492-0113", "서울시 마포구 성산대로 12", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner1212", "Owner1212@", "owner1212", "owner1212@naver.com",
				"49-4492-0114", "서울시 마포구 성산대로 13", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner1313", "Owner1313@", "owner1313", "owner1313@naver.com",
				"49-4492-0115", "서울시 마포구 성산대로 14", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner1414", "Owner1414@", "owner1414", "owner1414@naver.com",
				"49-4492-0116", "서울시 마포구 성산대로 15", UserRoleEnum.OWNER, "plate_owner"),
			new SignupRequestDto("owner1515", "Owner1515@", "owner1515", "owner1515@naver.com",
				"49-4492-0117", "서울시 마포구 성산대로 16", UserRoleEnum.OWNER, "plate_owner"),

			new SignupRequestDto("manager0000", "Manager0000@", "플레이트 관리자0", "manager0000@naver.com",
				"010-1411-1113", "서울시 강남구 테헤란로 23", UserRoleEnum.MANAGER, "plate_manager"),

			new SignupRequestDto("master0000", "Master0000@", "플레이트 마스터0", "master0000@naver.com",
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
