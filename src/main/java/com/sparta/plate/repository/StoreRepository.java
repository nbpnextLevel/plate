package com.sparta.plate.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.plate.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

	Store saveAndFlush(Store store);

	Optional<Store> findByIdAndIsDeletedFalse(UUID storeId);

	Page<Store> findByIsDeletedFalse(Pageable pageable);

	Page<Store> findByStoreNameContainingAndIsDeletedFalse(Pageable pageable, String search);

	Optional<Store> findByUserIdAndIsDeletedFalse(Long id);
}
