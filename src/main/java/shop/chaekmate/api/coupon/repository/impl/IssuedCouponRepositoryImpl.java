package shop.chaekmate.api.coupon.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.chaekmate.api.coupon.entity.IssuedCoupon;
import shop.chaekmate.api.coupon.repository.IssuedCouponRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;

import static shop.chaekmate.api.coupon.entity.QIssuedCoupon.issuedCoupon;
import static shop.chaekmate.api.coupon.entity.QCouponPolicy.couponPolicy;

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
}
