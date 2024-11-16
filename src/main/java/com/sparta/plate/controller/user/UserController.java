package com.sparta.plate.controller.user;

import java.util.List;
import java.util.Map;

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

import com.sparta.plate.dto.request.SignupRequestDto;
import com.sparta.plate.dto.request.UpdateUserRequestDto;
import com.sparta.plate.dto.response.ApiResponseDto;
import com.sparta.plate.dto.response.UserResponseDto;
import com.sparta.plate.entity.User;
import com.sparta.plate.security.UserDetailsImpl;
import com.sparta.plate.service.user.CheckDuplicatedLoginIdService;
import com.sparta.plate.service.user.DeleteUserService;
import com.sparta.plate.service.user.GetUserListService;
import com.sparta.plate.service.user.GetUserService;
import com.sparta.plate.service.user.UpdateUserService;
import com.sparta.plate.service.user.UserSignupService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "User Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserSignupService userSignupService;
	private final CheckDuplicatedLoginIdService checkDuplicatedLoginIdService;
	private final GetUserListService getUserListService;
	private final GetUserService getUserService;
	private final UpdateUserService updateUserService;
	private final DeleteUserService deleteUserService;

	@PostMapping("/signup")
	@Operation(summary = "유저 회원가입", description = "최초 회원가입을 위해 사용")
	public ApiResponseDto<Map<String, Object>> signup(@Valid @RequestBody SignupRequestDto request) {
		User savedUser = userSignupService.signup(request);
		return ApiResponseDto.success(Map.of("loginId", savedUser.getLoginId()));
	}

	@GetMapping("/exists/{loginId}")
	@Operation(summary = "아이디 중복 체크", description = "회원가입이 아이디 중복여부 확인을 위해 사용")
	public ApiResponseDto<Map<String, Object>> checkUserExists(@PathVariable("loginId") String loginId) {
		boolean result = checkDuplicatedLoginIdService.service(loginId);
		return ApiResponseDto.success(Map.of("result", result));
	}

	@GetMapping
	@Operation(summary = "유저 리스트 조회", description = "가입한 유저 리스트를 조회할 수 있음. 검색 필터는 loginId에 적용")
	public ApiResponseDto<List<UserResponseDto>> getUserList(
		@RequestParam("page") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam("sortBy") String sortBy,
		@RequestParam("isAsc") boolean isAsc,
		@RequestParam(value = "search", required = false) String search
	) {

		Page<UserResponseDto> userList = getUserListService.getUserList(page - 1, size, sortBy, isAsc, search);

		return ApiResponseDto.successPage(userList);
	}

	@GetMapping("/{id}")
	@Operation(summary = "유저 단건조회", description = "유저 테이블에 저장된 유저 id(pk)를 기반으로 단건 조회")
	public ApiResponseDto<UserResponseDto> getUser(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		User user = getUserService.getUser(id, userDetails.getUser());
		return ApiResponseDto.success(UserResponseDto.of(user));
	}

	@PatchMapping("/{id}")
	@Operation(summary = "유저 정보 수정",
		description = "CUSTOMER/OWNER는 본인 계정 정보(비밀번호 포함) 수정 가능,"
			+ "MANAGER는 MASTER 외의 모든 유저에 대한 정보 수정(비밀번호 제외)이 가능,"
			+ "MASTER는 모든 유저에 대한 정보(비밀번호 제외) 수정 가능")
	public ApiResponseDto<String> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequestDto request,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		User user = updateUserService.updateUser(id, request, userDetails.getUser());

		return ApiResponseDto.success(user.getLoginId());
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "유저 탈퇴",
		description = "CUSTOMER/OWNER는 본인 계정 탈퇴 가능,"
			+ "MANAGER는 MASTER 외의 모든 유저 삭제 가능, MASTER는 모든 유저 삭제 가능")
	public ApiResponseDto<?> deleteUser(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		deleteUserService.deleteUser(id, userDetails.getUser());
		return ApiResponseDto.successDelete();
	}
}
