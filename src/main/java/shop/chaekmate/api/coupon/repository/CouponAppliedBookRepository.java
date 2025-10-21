package shop.chaekmate.api.coupon.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.api.coupon.entity.CouponAppliedBook;

public interface CouponAppliedBookRepository
        extends JpaRepository<CouponAppliedBook, Long>, CouponAppliedBookJdbcRepository {

    List<CouponAppliedBook> findAllByCouponPolicyId(long couponPolicyId);
}
