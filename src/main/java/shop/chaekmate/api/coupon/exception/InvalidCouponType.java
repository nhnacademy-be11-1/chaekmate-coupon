package shop.chaekmate.api.coupon.exception;

import shop.chaekmate.api.coupon.entity.type.CouponType;

public class InvalidCouponType extends RuntimeException {
    public InvalidCouponType(CouponType couponType) {
        super("Unsupported coupon type: %s".formatted(couponType.name()));
    }
}
