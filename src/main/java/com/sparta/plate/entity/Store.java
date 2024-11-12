package com.sparta.plate.entity;

import java.util.UUID;

import com.sparta.plate.dto.request.StoreRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity @Table(name = "p_store")
public class Store extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_category_id", nullable = false)
	private StoreCategory storeCategory;

	@NotNull
	@Column(nullable = false, length = 100)
	private String storeName;

	@Column(length = 100)
	private String storeNumber;

	@NotNull
	@Column(nullable = false)
	private String address;

	private boolean isDeleted;

	@Builder
	public Store(UUID id, User user, StoreCategory storeCategory, String storeName, String storeNumber, String address, boolean isDeleted) {
		this.id = id;
		this.user = user;
		this.storeCategory = storeCategory;
		this.storeName = storeName;
		this.storeNumber = storeNumber;
		this.address = address;
		this.isDeleted = false;
	}

	@Override
	public void markAsDeleted(Long deletedBy) {
		super.markAsDeleted(deletedBy);
		this.isDeleted = true;
	}

	public void update(StoreRequestDto request, User user, StoreCategory storeCategory) {
		this.user = user;
		this.storeCategory = storeCategory;
		this.storeName = request.getName();
		this.storeNumber = request.getStoreNumber();
		this.address =  request.getAddress();
	}
}
