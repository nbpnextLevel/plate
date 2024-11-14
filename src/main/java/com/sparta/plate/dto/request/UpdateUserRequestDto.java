package com.sparta.plate.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateUserRequestDto {

	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$",
		message = "비밀번호는 8~15자의 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다")
	private String password;

	@NotNull
	@Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하로 입력해주세요")
	private String nickname;

	@NotNull
	@Email(message = "이메일 형식에 맞지않습니다.")
	private String email;

	@Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
	private String phone;

	@NotNull
	private String address;
}
