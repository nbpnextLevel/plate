package com.sparta.plate.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.plate.dto.request.ProductImageQueryDto;
import com.sparta.plate.dto.response.ProductImageResponseDto;
import com.sparta.plate.entity.ProductImage;
import com.sparta.plate.entity.QProductImage;
import com.sparta.plate.repository.ProductImageRepositoryCustom;
import com.sparta.plate.repository.util.QueryUtil;
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

    PathBuilder<Object> imageEntityPath = new PathBuilder<>(QProductImage.class, "productImage");


    @Override
    public Page<ProductImageResponseDto> searchAll(Pageable pageable, ProductImageQueryDto queryDto) {
        Long totalCnt = jpaQueryFactory
                .select(productImage.count())
                .from(productImage)
                .where(
                        searchByBooleanType(queryDto.getIsDeleted(), "isDeleted", imageEntityPath),
                        searchById(queryDto.getId(), "id", imageEntityPath),
                        searchByProductId(queryDto.getProductId()),
                        searchByDateRange(queryDto.getStartDate(), queryDto.getEndDate())
                )
                .fetchOne();

        if (totalCnt == null) {
            totalCnt = 0L;
        }

        List<ProductImage> images = jpaQueryFactory
                .selectFrom(productImage)
                .where(
                        searchByBooleanType(queryDto.getIsDeleted(), "isDeleted", imageEntityPath),
                        searchById(queryDto.getId(), "id", imageEntityPath),
                        searchByProductId(queryDto.getProductId()),
                        searchByDateRange(queryDto.getStartDate(), queryDto.getEndDate())
                )
                .orderBy(sort(queryDto.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ProductImageResponseDto> responseDtos = images.stream()
                .map(ProductImageResponseDto::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(responseDtos, pageable, totalCnt);
    }

    private BooleanBuilder searchByProductId(UUID productId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (productId != null) {
            booleanBuilder.and(productImage.product.id.eq(productId));
        }
        return booleanBuilder;
    }

    private BooleanBuilder searchById(UUID id, String fieldName, PathBuilder<Object> entityPath) {
        return QueryUtil.searchById(id, fieldName, entityPath);
    }

    private BooleanBuilder searchByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return QueryUtil.searchByDateRange(startDate, endDate, productImage.createdAt);
    }

    private BooleanBuilder searchByBooleanType(String text, String fieldName, PathBuilder<Object> entityPath) {
        return QueryUtil.searchByBooleanType(text, fieldName, entityPath);
    }

    private OrderSpecifier<?> sort(String sort) {
        return QueryUtil.sort(sort, productImage.createdAt);
    }
}
