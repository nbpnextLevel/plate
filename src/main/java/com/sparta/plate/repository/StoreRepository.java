package com.sparta.plate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.plate.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

	Store saveAndFlush(Store store);
}
