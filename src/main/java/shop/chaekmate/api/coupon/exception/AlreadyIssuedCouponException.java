package shop.chaekmate.api.coupon.exception;

import jakarta.ws.rs.BadRequestException;

public class AlreadyIssuedCouponException extends BadRequestException {
    public AlreadyIssuedCouponException(Long couponPolicyId) {
        super(String.format("이미 발급받은 쿠폰입니다. couponPolicyId: %d", couponPolicyId));
    }
}
