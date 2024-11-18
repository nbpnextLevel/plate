package com.sparta.plate.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreRequestDto {

	@NotNull
	private UUID categoryId;

	@Size(min = 2, max = 50, message = "가게이름은 2자 이상 50자 이하로 입력해주세요")
	private String name;

	@Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
	private String storeNumber;

	@NotNull
	private String address;

}
