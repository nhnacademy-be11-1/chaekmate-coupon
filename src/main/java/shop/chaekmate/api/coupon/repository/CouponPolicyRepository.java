package shop.chaekmate.api.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.exception.NotFoundCouponPolicy;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {
    default CouponPolicy getById(long couponPolicyId) {
        return findById(couponPolicyId)
                .orElseThrow(() -> new NotFoundCouponPolicy(couponPolicyId));
    }
}
