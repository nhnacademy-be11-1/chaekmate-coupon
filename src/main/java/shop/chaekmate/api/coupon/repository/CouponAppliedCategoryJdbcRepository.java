package shop.chaekmate.api.coupon.repository;

import java.util.Collection;
import shop.chaekmate.api.coupon.entity.CouponAppliedCategory;

public interface CouponAppliedCategoryJdbcRepository {

    void saveAllInBatch(Collection<CouponAppliedCategory> couponAppliedCategories);
}
