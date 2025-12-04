package shop.chaekmate.api.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.exception.NotFoundCouponPolicy;

import java.util.Optional;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long>, CouponPolicyRepositoryCustom {
    default CouponPolicy getById(long couponPolicyId) {
        return findById(couponPolicyId)
                .orElseThrow(() -> new NotFoundCouponPolicy(couponPolicyId));
    }

    Optional<CouponPolicy> findByType(CouponType type);
}
