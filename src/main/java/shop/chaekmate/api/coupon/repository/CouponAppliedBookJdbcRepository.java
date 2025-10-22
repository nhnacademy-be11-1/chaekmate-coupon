package shop.chaekmate.api.coupon.repository;

import java.util.Collection;
import shop.chaekmate.api.coupon.entity.CouponAppliedBook;

public interface CouponAppliedBookJdbcRepository {

    void saveAllInBatch(Collection<CouponAppliedBook> couponAppliedBooks);
}
