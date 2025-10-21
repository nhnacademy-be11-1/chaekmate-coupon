package shop.chaekmate.api.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.api.coupon.entity.CouponPolicy;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {


}
