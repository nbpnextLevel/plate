package com.sparta.plate.repository;

import com.sparta.plate.entity.Payment;
import com.sparta.plate.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByPayment(Payment payment);

    Optional<Review> findById(UUID reviewId);

//    @Query("SELECT p FROM Payment p JOIN p.order o JOIN o.user u WHERE u.id = :userId")
@Query("SELECT r FROM Review r " +
        "JOIN r.payment p " +
        "JOIN p.order o " +
        "JOIN o.user u " +
        "WHERE u.id = :userId " +
        "AND r.reviewStatus = true")
    Page<Review> findReviewByUserId(Long userId, Pageable pageable);


    @Query("SELECT r FROM Review r " +
            "JOIN r.payment p " +
            "JOIN p.order o " +
            "JOIN o.user u " +
            "JOIN o.store s " +
            "WHERE u.id = :userId " +
            "AND s.storeName LIKE %:storeName% " +
            "AND r.reviewStatus = true")
    Page<Review> findByPaymentOrderUserIdAndStoreName(Long userId, String storeName, Pageable pageable);


    @Query("SELECT r FROM Review r " +
            "JOIN r.payment p " +
            "JOIN p.order o " +
            "JOIN o.store s " +
            "WHERE s.id = :storeId " +
            "AND r.reviewStatus = true")
    Page<Review> findByStoreId(UUID storeId, Pageable pageable);
}