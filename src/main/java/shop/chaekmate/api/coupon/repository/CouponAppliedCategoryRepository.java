package shop.chaekmate.api.coupon.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.api.coupon.entity.CouponAppliedCategory;

public interface CouponAppliedCategoryRepository
        extends JpaRepository<CouponAppliedCategory, Long>, CouponAppliedCategoryJdbcRepository {

    List<CouponAppliedCategory> findAllByCouponPolicyId(long couponPolicyId);
}
