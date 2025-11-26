package shop.chaekmate.api.coupon.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;

import static shop.chaekmate.api.coupon.entity.QCouponAppliedBook.couponAppliedBook;
import static shop.chaekmate.api.coupon.entity.QCouponAppliedCategory.couponAppliedCategory;
import static shop.chaekmate.api.coupon.entity.QCouponPolicy.couponPolicy;

@Repository
@RequiredArgsConstructor
public class CouponPolicyRepositoryImpl implements CouponPolicyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CouponPolicy> findAvailableCouponPolicies(LocalDateTime now) {
        return queryFactory
                .selectFrom(couponPolicy)
                .where(
                        isManualIssueType(),
                        isWithinIssuePeriod(now),
                        hasRemainingQuantity()
                )
                .orderBy(couponPolicy.createdAt.desc())
                .fetch();
    }

    @Override
    public List<CouponPolicy> findAvailableCouponPoliciesByBookId(Long bookId, LocalDateTime now) {
        return queryFactory
                .selectFrom(couponPolicy)
                .join(couponAppliedBook).on(couponAppliedBook.couponPolicy.eq(couponPolicy))
                .where(
                        couponPolicy.type.eq(CouponType.BOOK),
                        couponAppliedBook.bookId.eq(bookId),
                        isWithinIssuePeriod(now)
                )
                .distinct()
                .orderBy(couponPolicy.createdAt.desc())
                .fetch();
    }

    @Override
    public List<CouponPolicy> findAvailableCouponPoliciesByCategoryIds(List<Long> categoryIds, LocalDateTime now) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(couponPolicy)
                .join(couponAppliedCategory).on(couponAppliedCategory.couponPolicy.eq(couponPolicy))
                .where(
                        couponPolicy.type.eq(CouponType.CATEGORY),
                        couponAppliedCategory.categoryId.in(categoryIds),
                        isWithinIssuePeriod(now)
                )
                .distinct()
                .orderBy(couponPolicy.createdAt.desc())
                .fetch();
    }

    private BooleanExpression isManualIssueType() {
        return couponPolicy.type.in(CouponType.BOOK, CouponType.CATEGORY);
    }

    private BooleanExpression isWithinIssuePeriod(LocalDateTime now) {
        return hasStarted(now).and(hasNotExpired(now));
    }

    private BooleanExpression hasStarted(LocalDateTime now) {
        return couponPolicy.appliedStartedAt.isNull()
                .or(couponPolicy.appliedStartedAt.loe(now));
    }

    private BooleanExpression hasNotExpired(LocalDateTime now) {
        return couponPolicy.appliedExpiredAt.isNull()
                .or(couponPolicy.appliedExpiredAt.goe(now));
    }

    private BooleanExpression hasRemainingQuantity() {
        return couponPolicy.remainingQuantity.isNull()
                .or(couponPolicy.remainingQuantity.gt(0L));
    }
}
