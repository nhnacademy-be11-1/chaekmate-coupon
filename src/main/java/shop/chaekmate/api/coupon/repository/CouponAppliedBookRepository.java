package shop.chaekmate.api.coupon.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.chaekmate.api.coupon.entity.CouponAppliedBook;
import shop.chaekmate.api.coupon.entity.CouponPolicy;

public interface CouponAppliedBookRepository
        extends JpaRepository<CouponAppliedBook, Long>, CouponAppliedBookJdbcRepository {
    List<CouponAppliedBook> findAllByCouponPolicyId(long couponPolicyId);

    @Modifying
    @Query("DELETE FROM CouponAppliedBook cab WHERE cab.couponPolicy = :couponPolicy")
    void deleteAllByCouponPolicy(@Param("couponPolicy") CouponPolicy couponPolicy);
}
