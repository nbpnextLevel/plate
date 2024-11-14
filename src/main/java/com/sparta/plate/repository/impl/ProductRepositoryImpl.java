package com.sparta.plate.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.plate.dto.request.ProductQueryDto;
import com.sparta.plate.dto.response.ProductImageResponseDto;
import com.sparta.plate.dto.response.ProductResponseDto;
import com.sparta.plate.entity.Product;
import com.sparta.plate.entity.ProductDisplayStatusEnum;
import com.sparta.plate.entity.ProductImage;
import com.sparta.plate.entity.QProduct;
import com.sparta.plate.repository.ProductImageRepository;
import com.sparta.plate.repository.ProductRepositoryCustom;
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

import static com.sparta.plate.entity.QProduct.product;
import static com.sparta.plate.entity.QStore.store;
import static com.sparta.plate.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;
    private final ProductImageRepository productImageRepository;

    PathBuilder<Object> productEntityPath = new PathBuilder<>(QProduct.class, "product");

    @Override
    public Page<ProductResponseDto> searchAll(Pageable pageable, ProductQueryDto queryDto, String role, Long userId) {
        Long totalCnt = jpaQueryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.store, store)
                .leftJoin(store.user, user)
                .where(
                        searchByBooleanType(queryDto.getIsHidden(), "isHidden", productEntityPath),
                        searchByBooleanType(queryDto.getIsDeleted(), "isDeleted", productEntityPath),
                        searchByText(queryDto.getProductName(), "name", productEntityPath),
                        searchById(queryDto.getProductId(), "id", productEntityPath),
                        searchByStoreId(queryDto.getStoreId()),
                        searchByDateRange(queryDto.getStartDate(), queryDto.getEndDate()),
                        searchDisplayStatusByRole(role, queryDto.getDisplayStatus(), userId)
                )
                .fetchOne();

        if (totalCnt == null) {
            totalCnt = 0L;
        }

        List<Product> products = jpaQueryFactory
                .selectFrom(product)
                .leftJoin(product.store, store)
                .leftJoin(store.user, user)
                .where(
                        searchByBooleanType(queryDto.getIsHidden(), "isHidden", productEntityPath),
                        searchByBooleanType(queryDto.getIsDeleted(), "isDeleted", productEntityPath),
                        searchByText(queryDto.getProductName(), "name", productEntityPath),
                        searchById(queryDto.getProductId(), "id", productEntityPath),
                        searchByStoreId(queryDto.getStoreId()),
                        searchByDateRange(queryDto.getStartDate(), queryDto.getEndDate()),
                        searchDisplayStatusByRole(role, queryDto.getDisplayStatus(), userId)
                )
                .orderBy(sort(queryDto.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ProductResponseDto> responseDtos = products.stream()
                .map(product -> {
                    List<ProductImage> productImages = productImageRepository.findActiveImages(product.getId());
                    List<ProductImageResponseDto> imageDtos = productImages.stream()
                            .map(ProductImageResponseDto::toDto)
                            .collect(Collectors.toList());
                    return ProductResponseDto.toDto(product, imageDtos);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responseDtos, pageable, totalCnt);
    }

    private BooleanBuilder searchById(UUID id, String fieldName, PathBuilder<Object> entityPath) {
        return QueryUtil.searchById(id, fieldName, entityPath);
    }

    private BooleanBuilder searchByStoreId(UUID storeId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (storeId != null) {
            booleanBuilder.and(product.store.id.eq(storeId));
        }
        return booleanBuilder;
    }

    private BooleanBuilder searchByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return QueryUtil.searchByDateRange(startDate, endDate, product.createdAt);
    }

    private BooleanBuilder searchByText(String requestText, String fieldName, PathBuilder<Object> entityPath) {
        return QueryUtil.searchByText(requestText, fieldName, entityPath);
    }

    private BooleanBuilder searchByBooleanType(String text, String fieldName, PathBuilder<Object> entityPath) {
        return QueryUtil.searchByBooleanType(text, fieldName, entityPath);
    }

    private OrderSpecifier<?> sort(String sort) {
        return QueryUtil.sort(sort, product.createdAt, product.updateAt);
    }

    private BooleanBuilder searchDisplayStatusByRole(String role, String searchDisplayStatus, Long userId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (role == null || "ROLE_CUSTOMER".equals(role)) {
            booleanBuilder.and(product.displayStatus.eq(ProductDisplayStatusEnum.valueOf("IN_STOCK")));
            return booleanBuilder;
        }

        if ("ROLE_OWNER".equals(role)) {
            applySearchDisplayStatusForOwner(booleanBuilder, searchDisplayStatus, userId);
            return booleanBuilder;
        }

        if ("ROLE_MASTER".equals(role) || "ROLE_MANAGER".equals(role)) {
            applySearchDisplayStatus(booleanBuilder, searchDisplayStatus);
            return booleanBuilder;
        }

        return booleanBuilder;
    }

    private void applySearchDisplayStatus(BooleanBuilder booleanBuilder, String searchDisplayStatus) {
        if ("all".equals(searchDisplayStatus) || searchDisplayStatus == null) {
            booleanBuilder.and(
                    product.displayStatus.eq(ProductDisplayStatusEnum.IN_STOCK)
                            .or(product.displayStatus.eq(ProductDisplayStatusEnum.PENDING_SALE))
                            .or(product.displayStatus.eq(ProductDisplayStatusEnum.DISCONTINUED))
            );
        } else {
            booleanBuilder.and(
                    product.displayStatus.eq(ProductDisplayStatusEnum.valueOf(searchDisplayStatus))
            );

            if ("IN_STOCK".equals(searchDisplayStatus)) {
                booleanBuilder.and(product.displayStatus.eq(ProductDisplayStatusEnum.IN_STOCK));
            } else if ("PENDING_SALE".equals(searchDisplayStatus)) {
                booleanBuilder.and(product.displayStatus.eq(ProductDisplayStatusEnum.PENDING_SALE));
            } else if ("DISCONTINUED".equals(searchDisplayStatus)) {
                booleanBuilder.and(product.displayStatus.eq(ProductDisplayStatusEnum.DISCONTINUED));
            }
        }
    }

    private void applySearchDisplayStatusForOwner(BooleanBuilder booleanBuilder, String searchDisplayStatus, Long userId) {
        if ("all".equals(searchDisplayStatus) || searchDisplayStatus == null) {
            booleanBuilder.and(product.displayStatus.eq(ProductDisplayStatusEnum.IN_STOCK));
            booleanBuilder.or(
                    product.store.user.id.eq(userId)
                            .and(product.displayStatus.in(ProductDisplayStatusEnum.PENDING_SALE, ProductDisplayStatusEnum.DISCONTINUED))
            );
        } else {
            if ("IN_STOCK".equals(searchDisplayStatus)) {
                booleanBuilder.and(
                        product.store.user.id.eq(userId)
                                .and(product.displayStatus.eq(ProductDisplayStatusEnum.IN_STOCK))
                );
            } else if ("PENDING_SALE".equals(searchDisplayStatus)) {
                booleanBuilder.and(
                        product.store.user.id.eq(userId)
                                .and(product.displayStatus.eq(ProductDisplayStatusEnum.PENDING_SALE))
                );
            } else if ("DISCONTINUED".equals(searchDisplayStatus)) {
                booleanBuilder.and(
                        product.store.user.id.eq(userId)
                                .and(product.displayStatus.eq(ProductDisplayStatusEnum.DISCONTINUED))
                );
            }
        }
    }
}
