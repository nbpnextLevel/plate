package com.sparta.plate.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity @Table(name = "p_store")
public class Store {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_category_id", nullable = false)
	private StoreCategory categoryId;

	//private User userId;

	@NotNull
	@Column(nullable = false, length = 100)
	private String storeName;

	@Column(length = 100)
	private String storeNumber;

	@NotNull
	@Column(nullable = false, length = 10)
	private String zipcode;

	@NotNull
	@Column(nullable = false)
	private String address;

	@NotNull
	@Column(nullable = false)
	private String detailAddress;

	@Column(nullable = false)
	private Long createdBy;

	private Long updatedBy;

	private Long deletedBy;

	@Builder
	public Store(StoreCategory categoryId, String storeName, String storeNumber, String zipcode, String address,
		String detailAddress, Long createdBy) {
		this.categoryId = categoryId;
		this.storeName = storeName;
		this.storeNumber = storeNumber;
		this.zipcode = zipcode;
		this.address = address;
		this.detailAddress = detailAddress;
		this.createdBy = createdBy;
	}
}
