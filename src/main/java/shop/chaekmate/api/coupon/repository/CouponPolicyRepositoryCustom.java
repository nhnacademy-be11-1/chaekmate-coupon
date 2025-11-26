package shop.chaekmate.api.coupon.repository;

import shop.chaekmate.api.coupon.entity.CouponPolicy;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponPolicyRepositoryCustom {

    List<CouponPolicy> findAvailableCouponPolicies(LocalDateTime now);

    List<CouponPolicy> findAvailableCouponPoliciesByBookId(Long bookId, LocalDateTime now);

    List<CouponPolicy> findAvailableCouponPoliciesByCategoryIds(List<Long> categoryIds, LocalDateTime now);
}
