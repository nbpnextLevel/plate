package com.sparta.plate.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.plate.dto.request.ProductSuggestionQueryDto;
import com.sparta.plate.dto.response.ProductSuggestionResponseDto;
import com.sparta.plate.entity.ProductSuggestionRequest;
import com.sparta.plate.repository.ProductSuggestionRepositoryCustom;
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

    @Override
    public Page<ProductSuggestionResponseDto> searchAll(Pageable pageable, ProductSuggestionQueryDto queryDto) {
        Long totalCnt = jpaQueryFactory
                .select(productSuggestionRequest.count())
                .from(productSuggestionRequest)
                .where(
                        searchByIsDeleted(queryDto.getIsDeleted()),
                        searchByIsSuccess(queryDto.getIsSuccess()),
                        searchById(queryDto.getId()),
                        searchByRequestText(queryDto.getRequestText()),
                        searchByDateRange(queryDto.getStartDate(), queryDto.getEndDate())
                )
                .fetchOne();

        if (totalCnt == null) {
            totalCnt = 0L;
        }

        List<ProductSuggestionRequest> suggestions = jpaQueryFactory
                .selectFrom(productSuggestionRequest)
                .where(
                        searchByIsDeleted(queryDto.getIsDeleted()),
                        searchByIsSuccess(queryDto.getIsSuccess()),
                        searchById(queryDto.getId()),
                        searchByRequestText(queryDto.getRequestText()),
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

    private BooleanBuilder searchById(UUID id) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (id != null) {
            booleanBuilder.and(productSuggestionRequest.id.eq(id));
        }
        return booleanBuilder;
    }

    private BooleanBuilder searchByRequestText(String requestText) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (requestText != null && !requestText.trim().isEmpty()) {
            System.out.println("Searching with requestText: " + requestText);
            booleanBuilder.and(productSuggestionRequest.requestText.lower().like("%" + requestText.toLowerCase() + "%"));
        }
        return booleanBuilder;
    }

    private BooleanBuilder searchByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (startDate != null) {
            booleanBuilder.and(productSuggestionRequest.createdAt.goe(startDate));
        }

        if (endDate != null) {
            booleanBuilder.and(productSuggestionRequest.createdAt.loe(endDate));
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
                    booleanBuilder.and(productSuggestionRequest.isDeleted.isTrue());
                    break;
                case "false":
                    booleanBuilder.and(productSuggestionRequest.isDeleted.isFalse());
                    break;
                case "all":
                default:
                    break;
            }
        }
        return booleanBuilder;
    }

    private BooleanBuilder searchByIsSuccess(String isSuccess) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (isSuccess != null && !isSuccess.isEmpty()) {
            switch (isSuccess) {
                case "true":
                    booleanBuilder.and(productSuggestionRequest.isSuccess.isTrue());
                    break;
                case "false":
                    booleanBuilder.and(productSuggestionRequest.isSuccess.isFalse());
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
            return new OrderSpecifier<>(Order.DESC, productSuggestionRequest.createdAt);
        }

        switch (sort) {
            case "byRegistrationDate":
                return new OrderSpecifier<>(Order.ASC, productSuggestionRequest.createdAt);
            case "byRecentRegistrationDate":
            default:
                return new OrderSpecifier<>(Order.DESC, productSuggestionRequest.createdAt);
        }
    }
}
