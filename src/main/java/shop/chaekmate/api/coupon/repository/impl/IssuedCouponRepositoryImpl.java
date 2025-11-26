package shop.chaekmate.api.coupon.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.chaekmate.api.coupon.dto.request.BookCouponCheckRequest;
import shop.chaekmate.api.coupon.entity.IssuedCoupon;
import shop.chaekmate.api.coupon.repository.IssuedCouponRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;

import static shop.chaekmate.api.coupon.entity.QCouponPolicy.couponPolicy;
import static shop.chaekmate.api.coupon.entity.QIssuedCoupon.issuedCoupon;
import static shop.chaekmate.api.coupon.entity.QCouponAppliedBook.couponAppliedBook;
import static shop.chaekmate.api.coupon.entity.QCouponAppliedCategory.couponAppliedCategory;

@Repository
@RequiredArgsConstructor
public class IssuedCouponRepositoryImpl implements IssuedCouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<IssuedCoupon> findAvailableCouponsByMemberId(Long memberId, LocalDateTime now) {
        return queryFactory
                .selectFrom(issuedCoupon)
                .join(issuedCoupon.couponPolicy, couponPolicy).fetchJoin()
                .where(
                        issuedCoupon.memberId.eq(memberId),
                        issuedCoupon.usedAt.isNull(),
                        issuedCoupon.expiredAt.gt(now)
                )
                .orderBy(issuedCoupon.expiredAt.asc())
                .fetch();
    }

    @Override
    public List<IssuedCoupon> findUsedCouponsByMemberId(Long memberId) {
        return queryFactory
                .selectFrom(issuedCoupon)
                .join(issuedCoupon.couponPolicy, couponPolicy).fetchJoin()
                .where(
                        issuedCoupon.memberId.eq(memberId),
                        issuedCoupon.usedAt.isNotNull()
                )
                .orderBy(issuedCoupon.usedAt.desc())
                .fetch();
    }

    @Override
    public boolean existsByMemberIdAndCouponPolicyId(Long memberId, Long couponPolicyId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(issuedCoupon)
                .where(
                        issuedCoupon.memberId.eq(memberId),
                        issuedCoupon.couponPolicy.id.eq(couponPolicyId)
                )
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public List<IssuedCoupon> finaAvailableCouponForBooks(
            Long memberId,
            List<BookCouponCheckRequest> books) {
        return queryFactory
                .selectFrom(issuedCoupon)
                .distinct()
                .join(issuedCoupon.couponPolicy, couponPolicy).fetchJoin()
                .leftJoin(couponAppliedBook)
                    .on(couponAppliedBook.couponPolicy.id.eq(couponPolicy.id))
                    .fetchJoin()
                .leftJoin(couponAppliedCategory)
                    .on(couponAppliedCategory.couponPolicy.id.eq(couponPolicy.id))
                    .fetchJoin()
                .where(
                        issuedCoupon.memberId.eq(memberId),
                        issuedCoupon.usedAt.isNull(),
                        issuedCoupon.expiredAt.gt(LocalDateTime.now())
                )
                .orderBy(issuedCoupon.expiredAt.asc())
                .fetch();
    }
}
