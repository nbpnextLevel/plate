package com.sparta.plate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity @Getter
@Table(name = "p_users")
public class User {

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

	@NotNull @Column(nullable = false)
	private String email;

	@Column(length = 100)
	private String phone;

	@Column(nullable = false)
	private boolean isDeleted;

	@NotNull @Column(nullable = false)
	private String address;

	private Long createdBy;

	private Long updatedBy;

	private Long deletedBy;

	@Builder
	public User(String loginId, String password, String nickname, UserRoleEnum role, String email, String phone,
		boolean isDeleted, String address, Long createdBy, Long updatedBy, Long deletedBy) {
		this.loginId = loginId;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
		this.email = email;
		this.phone = phone;
		this.isDeleted = isDeleted;
		this.address = address;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.deletedBy = deletedBy;
	}

	public void setCreatedBy(Long id) {
		this.createdBy = id;
	}
}
