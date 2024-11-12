package com.sparta.plate.repository;

import com.sparta.plate.entity.Order;
import com.sparta.plate.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

//    Optional<Payment> findById(UUID paymentId);


    Optional<Payment> findByPaymentId(UUID paymentId);

    Page<Payment> findByOrderUserLoginId(String loginId, Pageable pageable);


// ERROR : loginId & search:storeName
//    {
//        "errorMessage": "org.hibernate.query.sqm.UnknownPathException: Could not resolve attribute 'storeName' of 'com.sparta.plate.entity.Payment' [SELECT p FROM Payment p JOIN p.order o JOIN o.store s JOIN o.user u WHERE u.id = :loginId AND s.storeName LIKE :storeName ORDER BY s.storeName ASC, p.storeName asc]",
//            "statusCode": 400
//    }
    @Query("SELECT p FROM Payment p " +
            "JOIN p.order o " +            // Payment -> Order
            "JOIN o.store s " +            // Order -> Store
            "JOIN o.user u " +             // Order -> User (연관관계 추가)
            "WHERE u.loginId = :loginId " +// User의 loginId로 필터링
            "AND s.storeName LIKE %:storeName% " +  // Store의 storeName을 LIKE로 필터링
            "ORDER BY s.storeName ASC")    // storeName 기준으로 정렬
    Page<Payment> searchPaymentsByLoginIdAndStoreName(@Param("loginId") String loginId,
                                                      @Param("storeName") String storeName,
                                                      Pageable pageable);


//    Page<Payment> findByOrderStoreId(UUID storeId, Pageable pageable);


//    @Query("SELECT p FROM Payment p " +
//            "JOIN p.order o " +               // Payment -> Order
//            "JOIN o.store s " +               // Order -> Store
//            "WHERE s.id = :storeId " +        // Store의 id를 기준으로 결제 조회
//            "ORDER BY p.paymentId ASC")              // 결제 내역을 주문 순으로 정렬
    Page<Payment> findALLByOrderStoreId(@Param("storeId") UUID storeId, Pageable pageable);
    // 로그인 ID로 결제 조회 (storeName 조건 없이)
//    Page<Payment> findByUserLoginId(String loginId, Pageable pageable);
}
