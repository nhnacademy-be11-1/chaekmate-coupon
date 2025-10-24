package shop.chaekmate.api.coupon.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.chaekmate.api.coupon.entity.CouponAppliedCategory;
import shop.chaekmate.api.coupon.entity.CouponPolicy;

public interface CouponAppliedCategoryRepository
        extends JpaRepository<CouponAppliedCategory, Long>, CouponAppliedCategoryJdbcRepository {
    List<CouponAppliedCategory> findAllByCouponPolicyId(long couponPolicyId);

    @Modifying
    @Query("UPDATE CouponAppliedCategory cac SET cac.deletedAt = CURRENT_TIMESTAMP WHERE cac.couponPolicy = :couponPolicy AND cac.deletedAt IS NULL")
    void softDeleteAllByCouponPolicy(@Param("couponPolicy") CouponPolicy couponPolicy);
}
