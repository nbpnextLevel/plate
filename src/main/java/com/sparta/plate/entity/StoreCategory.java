package com.sparta.plate.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity @Table(name = "p_store_category")
public class StoreCategory {

	@Id @GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String category;


	public StoreCategory(UUID id, String category) {
		this.id = id;
		this.category = category;
	}
}
