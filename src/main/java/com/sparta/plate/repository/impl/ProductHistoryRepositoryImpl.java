package com.sparta.plate.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.plate.dto.request.ProductHistoryQueryDto;
import com.sparta.plate.dto.response.ProductHistoryResponseDto;
import com.sparta.plate.entity.ProductHistory;
import com.sparta.plate.repository.ProductHistoryRepositoryCustom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.sparta.plate.entity.QProductHistory.productHistory;

@Repository
@RequiredArgsConstructor
public class ProductHistoryRepositoryImpl implements ProductHistoryRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    @Override
    public Page<ProductHistoryResponseDto> searchAll(Pageable pageable, ProductHistoryQueryDto queryDto) {
        Long totalCnt = jpaQueryFactory
                .select(productHistory.count())
                .from(productHistory)
                .where(
                        searchByIsDeleted(queryDto.getIsDeleted()),
                        searchById(queryDto.getId()),
                        searchByProductId(queryDto.getProductId()),
                        searchByDateRange(queryDto.getStartDate(), queryDto.getEndDate())
                )
                .fetchOne();

        if (totalCnt == null) {
            totalCnt = 0L;
        }

        List<ProductHistory> histories = jpaQueryFactory
                .selectFrom(productHistory)
                .where(
                        searchByIsDeleted(queryDto.getIsDeleted()),
                        searchById(queryDto.getId()),
                        searchByProductId(queryDto.getProductId()),
                        searchByDateRange(queryDto.getStartDate(), queryDto.getEndDate())
                )
                .orderBy(sort(queryDto.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ProductHistoryResponseDto> responseDtos = histories.stream()
                .map(ProductHistoryResponseDto::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(responseDtos, pageable, totalCnt);
    }

    private BooleanBuilder searchById(UUID id) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (id != null) {
            booleanBuilder.and(productHistory.id.eq(id));
        }
        return booleanBuilder;
    }

    private BooleanBuilder searchByProductId(UUID productId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (productId != null) {
            booleanBuilder.and(productHistory.productId.eq(productId));
        }
        return booleanBuilder;
    }

    private BooleanBuilder searchByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (startDate != null) {
            booleanBuilder.and(productHistory.createdAt.goe(startDate));
        }

        if (endDate != null) {
            booleanBuilder.and(productHistory.createdAt.loe(endDate));
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate");
        }

        return booleanBuilder;
    }

    private BooleanBuilder searchByIsDeleted(String isDeleted) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (isDeleted != null && !isDeleted.isEmpty()) {
            switch (isDeleted) {
                case "true":
                    booleanBuilder.and(productHistory.isDeleted.isTrue());
                    break;
                case "false":
                    booleanBuilder.and(productHistory.isDeleted.isFalse());
                    break;
                case "all":
                default:
                    break;
            }
        }
        return booleanBuilder;
    }

    private OrderSpecifier<?> sort(String sort) {
        if (sort == null || sort.isEmpty()) {
            return new OrderSpecifier<>(Order.DESC, productHistory.createdAt);
        }

        switch (sort) {
            case "byRegistrationDate":
                return new OrderSpecifier<>(Order.ASC, productHistory.createdAt);
            case "byRecentRegistrationDate":
            default:
                return new OrderSpecifier<>(Order.DESC, productHistory.createdAt);
        }
    }
}
