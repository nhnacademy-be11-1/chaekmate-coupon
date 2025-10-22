package shop.chaekmate.api.coupon.exception;

public class NotFoundCouponPolicy extends RuntimeException {
    public NotFoundCouponPolicy(long couponPolicyId) {
        super("Not found coupon policy. id: %d".formatted(couponPolicyId));
    }
}
