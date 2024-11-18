package com.sparta.plate.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.plate.dto.request.StoreSearchRequestDto;
import com.sparta.plate.entity.QStore;
import com.sparta.plate.entity.Store;
import com.sparta.plate.repository.StoreRepositoryCustom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Store> searchStores(StoreSearchRequestDto request) {
        QStore store = QStore.store;

        // 기본 조건: isDeleted = false
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(store.isDeleted.eq(false));

        // 카테고리 입력되면 해당 카테고리의 모든 지점 노출
        if(request.getCategoryId() != null) {
            builder.and(store.storeCategory.id.eq(request.getCategoryId()));
        }

        // 지점 입력되면 검색한 지점 이름 포함하는거 노출
        if (StringUtils.hasText(request.getStoreName())) {
            builder.and(store.storeName.contains(request.getStoreName()));
        }

        // 주소 입력되면 검색한 해당 주소에 등록된 지점 노출
        if (StringUtils.hasText(request.getAddress())) {
            builder.and(store.address.contains(request.getAddress()));
        }

        long total = getTotalCount(store, builder);
        OrderSpecifier<?> orderSpecifier = getOrderBy(request, store);

        List<Store> datas = jpaQueryFactory
            .selectFrom(store)
            .where(builder)
            .orderBy(orderSpecifier)
            .offset(request.getPageable().getOffset())
            .limit(request.getPageable().getPageSize())
            .fetch();

        return new PageImpl<>(datas, request.getPageable(), total);
    }

    private long getTotalCount(QStore store, BooleanBuilder builder) {
        return jpaQueryFactory
            .selectFrom(store)
            .where(builder)
            .fetchCount();
    }

    private OrderSpecifier<?> getOrderBy(StoreSearchRequestDto request, QStore store) {
        if ("createdAt".equals(request.getSortBy())) {
            return request.isAsc() ? store.createdAt.asc() : store.createdAt.desc();
        } else {
            return request.isAsc() ? store.updateAt.asc() : store.updateAt.desc();
        }
    }
}