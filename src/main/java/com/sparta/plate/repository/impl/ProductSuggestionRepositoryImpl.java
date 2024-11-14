package com.sparta.plate.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.plate.dto.request.ProductSuggestionQueryDto;
import com.sparta.plate.dto.response.ProductSuggestionResponseDto;
import com.sparta.plate.entity.ProductSuggestionRequest;
import com.sparta.plate.entity.QProductSuggestionRequest;
import com.sparta.plate.repository.ProductSuggestionRepositoryCustom;
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

import static com.sparta.plate.entity.QProductSuggestionRequest.productSuggestionRequest;

@Repository
@RequiredArgsConstructor
public class ProductSuggestionRepositoryImpl implements ProductSuggestionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    PathBuilder<Object> suggestionEntityPath = new PathBuilder<>(QProductSuggestionRequest.class, "productSuggestionRequest");

    @Override
    public Page<ProductSuggestionResponseDto> searchAll(Pageable pageable, ProductSuggestionQueryDto queryDto) {
        Long totalCnt = jpaQueryFactory
                .select(productSuggestionRequest.count())
                .from(productSuggestionRequest)
                .where(
                        searchByBooleanType(queryDto.getIsDeleted(), "isDeleted", suggestionEntityPath),
                        searchByBooleanType(queryDto.getIsSuccess(), "isSuccess", suggestionEntityPath),
                        searchById(queryDto.getId(), "id", suggestionEntityPath),
                        searchByText(queryDto.getRequestText(), "requestText", suggestionEntityPath),
                        searchByDateRange(queryDto.getStartDate(), queryDto.getEndDate())
                )
                .fetchOne();

        if (totalCnt == null) {
            totalCnt = 0L;
        }

        List<ProductSuggestionRequest> suggestions = jpaQueryFactory
                .selectFrom(productSuggestionRequest)
                .where(
                        searchByBooleanType(queryDto.getIsDeleted(), "isDeleted", suggestionEntityPath),
                        searchByBooleanType(queryDto.getIsSuccess(), "isSuccess", suggestionEntityPath),
                        searchById(queryDto.getId(), "id", suggestionEntityPath),
                        searchByText(queryDto.getRequestText(), "requestText", suggestionEntityPath),
                        searchByDateRange(queryDto.getStartDate(), queryDto.getEndDate())
                )
                .orderBy(sort(queryDto.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ProductSuggestionResponseDto> responseDtos = suggestions.stream()
                .map(ProductSuggestionResponseDto::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(responseDtos, pageable, totalCnt);
    }

    private BooleanBuilder searchById(UUID id, String fieldName, PathBuilder<Object> entityPath) {
        return QueryUtil.searchById(id, fieldName, entityPath);
    }

    private BooleanBuilder searchByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return QueryUtil.searchByDateRange(startDate, endDate, productSuggestionRequest.createdAt);
    }

    private BooleanBuilder searchByText(String requestText, String fieldName, PathBuilder<Object> entityPath) {
        return QueryUtil.searchByText(requestText, fieldName, entityPath);
    }

    private BooleanBuilder searchByBooleanType(String text, String fieldName, PathBuilder<Object> entityPath) {
        return QueryUtil.searchByBooleanType(text, fieldName, entityPath);
    }

    private OrderSpecifier<?> sort(String sort) {
        return QueryUtil.sort(sort, productSuggestionRequest.createdAt);
    }
}
