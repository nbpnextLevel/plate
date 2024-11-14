package com.sparta.plate.entity;

import java.util.ArrayList;
import java.util.List;

import com.sparta.plate.dto.request.StoreRequestDto;
import com.sparta.plate.dto.request.UpdateUserRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity @Getter
@Table(name = "p_users")
public class User extends Timestamped {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String loginId;

	@Column(nullable = false)
	private String password;

	@NotNull @Column(nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@NotNull @Column(nullable = false, length = 100)
	private UserRoleEnum role;

	// TODO 논의 필요
	// @OneToMany(mappedBy = "user")
	// private List<Store> storeList = new ArrayList<>();

	@OneToOne
	private Store store;

	@NotNull @Column(nullable = false)
	private String email;

	@Column(length = 100)
	private String phone;

	@Column(nullable = false)
	private boolean isDeleted;

	@NotNull @Column(nullable = false)
	private String address;

	@Builder
	public User(String loginId, String password, String nickname, UserRoleEnum role, String email,
		String phone, String address) {
		this.loginId = loginId;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
		this.email = email;
		this.phone = phone;
		this.isDeleted = false;
		this.address = address;
	}

	@Override
	public void markAsDeleted(Long deletedBy) {
		super.markAsDeleted(deletedBy);
		this.isDeleted = true;
	}

	public void updateWithoutPassword(UpdateUserRequestDto request) {
		updateCommonInfo(request);
	}

	public void updateAllInfo(UpdateUserRequestDto request, String encodedPassword) {
		this.password = encodedPassword;
		updateCommonInfo(request);
	}

	public void updateCommonInfo(UpdateUserRequestDto request) {
		this.nickname = request.getNickname();
		this.email = request.getEmail();
		this.phone = request.getPhone();
		this.address = request.getAddress();
	}
}
