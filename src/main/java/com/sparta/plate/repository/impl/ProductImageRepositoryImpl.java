package com.sparta.plate.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.plate.dto.request.ProductImageQueryDto;
import com.sparta.plate.dto.response.ProductImageResponseDto;
import com.sparta.plate.entity.ProductImage;
import com.sparta.plate.repository.ProductImageRepositoryCustom;
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

import static com.sparta.plate.entity.QProductImage.productImage;

@Repository
@RequiredArgsConstructor
public class ProductImageRepositoryImpl implements ProductImageRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    @Override
    public Page<ProductImageResponseDto> searchAll(Pageable pageable, ProductImageQueryDto queryDto) {
        Long totalCnt = jpaQueryFactory
                .select(productImage.count())
                .from(productImage)
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

        System.out.println("totalCnt: " + totalCnt);
        System.out.println(jpaQueryFactory.selectFrom(productImage)
                .where(
                        searchByIsDeleted(queryDto.getIsDeleted()),
                        searchById(queryDto.getId()),
                        searchByProductId(queryDto.getProductId()),
                        searchByDateRange(queryDto.getStartDate(), queryDto.getEndDate())
                )
                .orderBy(sort(queryDto.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toString());

        List<ProductImage> images = jpaQueryFactory
                .selectFrom(productImage)
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

        System.out.println("suggestions.size : " + images.size());

        List<ProductImageResponseDto> responseDtos = images.stream()
                .map(ProductImageResponseDto::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(responseDtos, pageable, totalCnt);
    }

    private BooleanBuilder searchById(UUID id) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (id != null) {
            booleanBuilder.and(productImage.id.eq(id));
        }
        return booleanBuilder;
    }

    private BooleanBuilder searchByProductId(UUID productId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (productId != null) {
            booleanBuilder.and(productImage.product.id.eq(productId));
        }
        return booleanBuilder;
    }

    private BooleanBuilder searchByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (startDate != null) {
            booleanBuilder.and(productImage.createdAt.goe(startDate));
        }

        if (endDate != null) {
            booleanBuilder.and(productImage.createdAt.loe(endDate));
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
                    booleanBuilder.and(productImage.isDeleted.isTrue());
                    break;
                case "false":
                    booleanBuilder.and(productImage.isDeleted.isFalse());
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
            return new OrderSpecifier<>(Order.DESC, productImage.createdAt);
        }

        switch (sort) {
            case "byRegistrationDate":
                return new OrderSpecifier<>(Order.ASC, productImage.createdAt);
            case "byRecentRegistrationDate":
            default:
                return new OrderSpecifier<>(Order.DESC, productImage.createdAt);
        }
    }
}
