package com.sparta.plate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity @Getter
@Table(name = "p_users")
public class User {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Pattern(regexp = "^[a-z0-9]{4,10}$",
		message = "아이디는 4~10자의 영문 소문자, 숫자만 사용 가능합니다")
	@Column(nullable = false, unique = true, length = 100)
	private String loginId;

	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$",
		message = "비밀번호는 8~15자의 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다")
	@Column(nullable = false)
	private String password;

	@Size(max = 30)
	@NotNull @Column(nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@NotNull @Column(nullable = false, length = 100)
	private UserRoleEnum role;

	@Email
	@NotNull @Column(nullable = false)
	private String email;

	@Column(length = 100)
	private String phone;

	@Column(nullable = false)
	private boolean isDeleted = Boolean.FALSE;

	@NotNull @Column(nullable = false, length = 10)
	private String zipcode;

	@NotNull @Column(nullable = false)
	private String address;

	@NotNull @Column(nullable = false)
	private String detailAddress;

	@Column(nullable = false)
	private Long createdBy;

	private Long updatedBy;

	private Long deletedBy;

	@Builder
	public User(String loginId, String password, String nickname, UserRoleEnum role, String email, String phone,
		boolean isDeleted, String zipcode, String address, String detailAddress, Long createdBy) {
		this.loginId = loginId;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
		this.email = email;
		this.phone = phone;
		this.isDeleted = isDeleted;
		this.zipcode = zipcode;
		this.address = address;
		this.detailAddress = detailAddress;
		this.createdBy = createdBy;
	}
}
