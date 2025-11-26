package shop.chaekmate.api.coupon.service.strategy.order;

import shop.chaekmate.api.coupon.dto.request.BookCouponCheckRequest;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponType;

public interface CouponApplicableStrategy {

    boolean supports(CouponType couponType);

    boolean isApplicable(CouponPolicy policy, BookCouponCheckRequest book);
}
