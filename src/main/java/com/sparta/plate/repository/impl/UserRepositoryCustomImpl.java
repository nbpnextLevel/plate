package com.sparta.plate.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.plate.dto.request.UserSearchRequestDto;
import com.sparta.plate.entity.QUser;
import com.sparta.plate.entity.User;
import com.sparta.plate.repository.UserRepositoryCustom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<User> searchUsers(UserSearchRequestDto request) {
		QUser user = QUser.user;

		// 기본 조건: isDeleted = false
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(user.isDeleted.eq(false));

		// 검색어 입력되면 로그인아이디에서 부분일치~전체일치하는거 반환
		if (StringUtils.hasText(request.getSearch())) {
			builder.and(user.loginId.contains(request.getSearch()));
		}

		// 전체 카운트 조회
		long total = getTotalCount(builder);
		OrderSpecifier<?> orderSpecifier = getOrderBy(request, user);

		// 데이터 조회
		List<User> users = jpaQueryFactory
			.selectFrom(QUser.user)
			.where(builder)
			.offset(request.getPageable().getOffset())
			.limit(request.getPageable().getPageSize())
			.orderBy(orderSpecifier)
			.fetch();

		return new PageImpl<>(users, request.getPageable(), total);

	}

	private OrderSpecifier<?> getOrderBy(UserSearchRequestDto request, QUser user) {
		if ("createdAt".equals(request.getSortBy())) {
			return request.isAsc() ? user.createdAt.asc() : user.createdAt.desc();
		} else {
			return request.isAsc() ? user.updateAt.asc() : user.updateAt.desc();
		}
	}

	private long getTotalCount(BooleanBuilder builder) {
		return jpaQueryFactory
			.selectFrom(QUser.user)
			.where(builder)
			.fetchCount();
	}
}
