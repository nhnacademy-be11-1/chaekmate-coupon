package shop.chaekmate.api.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.api.coupon.entity.IssuedCoupon;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long>, IssuedCouponRepositoryCustom{
}
