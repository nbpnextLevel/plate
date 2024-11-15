package com.sparta.plate.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.plate.dto.request.ProductHistoryQueryDto;
import com.sparta.plate.dto.response.ProductHistoryResponseDto;
import com.sparta.plate.entity.ProductHistory;
import com.sparta.plate.entity.QProductHistory;
import com.sparta.plate.repository.ProductHistoryRepositoryCustom;
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

import static com.sparta.plate.entity.QProductHistory.productHistory;

@Repository
@RequiredArgsConstructor
public class ProductHistoryRepositoryImpl implements ProductHistoryRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    PathBuilder<Object> historyEntityPath = new PathBuilder<>(QProductHistory.class, "productHistory");

    @Override
    public Page<ProductHistoryResponseDto> searchAll(Pageable pageable, ProductHistoryQueryDto queryDto) {
        Long totalCnt = jpaQueryFactory
                .select(productHistory.count())
                .from(productHistory)
                .where(
                        searchByBooleanType(queryDto.getIsDeleted(), "isDeleted", historyEntityPath),
                        searchById(queryDto.getId(), "id", historyEntityPath),
                        searchById(queryDto.getProductId(), "productId", historyEntityPath),
                        searchByDateRange(queryDto.getStartDate(), queryDto.getEndDate())
                )
                .fetchOne();

        if (totalCnt == null) {
            totalCnt = 0L;
        }

        List<ProductHistory> histories = jpaQueryFactory
                .selectFrom(productHistory)
                .where(
                        searchByBooleanType(queryDto.getIsDeleted(), "isDeleted", historyEntityPath),
                        searchById(queryDto.getId(), "id", historyEntityPath),
                        searchById(queryDto.getProductId(), "productId", historyEntityPath),
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


    private BooleanBuilder searchById(UUID id, String fieldName, PathBuilder<Object> entityPath) {
        return QueryUtil.searchById(id, fieldName, entityPath);
    }

    private BooleanBuilder searchByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return QueryUtil.searchByDateRange(startDate, endDate, productHistory.createdAt);
    }

    private BooleanBuilder searchByBooleanType(String text, String fieldName, PathBuilder<Object> entityPath) {
        return QueryUtil.searchByBooleanType(text, fieldName, entityPath);
    }

    private OrderSpecifier<?> sort(String sort) {
        return QueryUtil.sort(sort, productHistory.createdAt);
    }
}
