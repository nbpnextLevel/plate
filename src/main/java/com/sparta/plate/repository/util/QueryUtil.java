package com.sparta.plate.repository.util;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class QueryUtil {
    public static BooleanBuilder searchById(UUID id, String fieldName, PathBuilder<Object> entityPath) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (id != null) {
            ComparablePath<UUID> idPath = entityPath.getComparable(fieldName, UUID.class);
            booleanBuilder.and(idPath.eq(id));
        }

        return booleanBuilder;
    }

    public static BooleanBuilder searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, DateTimePath<LocalDateTime> createdAt) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (startDate == null && endDate != null) {
            booleanBuilder.and(createdAt.loe(endDate));
        }

        if (startDate == null && endDate == null) {
            endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            booleanBuilder.and(createdAt.loe(endDate));
        }

        if (startDate != null && endDate == null) {
            booleanBuilder.and(createdAt.goe(startDate));
        }

        if (startDate != null && endDate != null) {
            booleanBuilder.and(createdAt.goe(startDate));
            booleanBuilder.and(createdAt.loe(endDate));
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate");
        }

        return booleanBuilder;
    }

    public static BooleanBuilder searchByText(String text, String fieldName, PathBuilder<Object> entityPath) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (text != null && !text.trim().isEmpty()) {
            StringPath idPath = entityPath.getString(fieldName);
            booleanBuilder.and(idPath.lower().like("%" + text.toLowerCase() + "%"));
        }
        return booleanBuilder;
    }

    public static BooleanBuilder searchByBooleanType(String text, String fieldName, PathBuilder<Object> entityPath) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (text != null && !text.isEmpty()) {
            BooleanPath booleanPath = entityPath.getBoolean(fieldName);

            switch (text.toLowerCase()) {
                case "true":
                    booleanBuilder.and(booleanPath.isTrue());
                    break;
                case "false":
                    booleanBuilder.and(booleanPath.isFalse());
                    break;
                case "all":
                default:
                    break;
            }
        }

        return booleanBuilder;
    }

    public static OrderSpecifier<?> sort(String sort, DateTimePath<LocalDateTime> createdAt) {
        if (sort == null || sort.isEmpty()) {
            return new OrderSpecifier<>(Order.DESC, createdAt);
        }

        switch (sort) {
            case "byRegistrationDate":
                return new OrderSpecifier<>(Order.ASC, createdAt);
            case "byRecentRegistrationDate":
            default:
                return new OrderSpecifier<>(Order.DESC, createdAt);
        }
    }

    public static OrderSpecifier<?> sort(String sort, DateTimePath<LocalDateTime> createdAt, DateTimePath<LocalDateTime> updatedAt) {
        if (sort == null || sort.isEmpty()) {
            return new OrderSpecifier<>(Order.DESC, createdAt);
        }

        switch (sort) {
            case "byRegistrationDate":
                return new OrderSpecifier<>(Order.ASC, createdAt);
            case "byRecentRegistrationDate":
                return new OrderSpecifier<>(Order.DESC, createdAt);
            case "byUpdateDate":
                return new OrderSpecifier<>(Order.ASC, updatedAt);
            case "byRecentUpdateDate":
            default:
                return new OrderSpecifier<>(Order.DESC, updatedAt);
        }
    }
}
