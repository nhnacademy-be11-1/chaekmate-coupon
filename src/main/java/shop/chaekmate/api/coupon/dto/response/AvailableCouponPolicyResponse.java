package shop.chaekmate.api.coupon.dto.response;

import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.DiscountType;

import java.time.LocalDateTime;

public record AvailableCouponPolicyResponse(
        Long couponPolicyId,
        String couponName,
        String discountDescription,
        Integer minAvailableAmount,
        Long maxAppliedAmount,
        LocalDateTime appliedExpiredAt,
        Long remainingQuantity,
        boolean alreadyIssued
) {
    public static AvailableCouponPolicyResponse from(CouponPolicy couponPolicy, boolean alreadyIssued) {
        String discountDescription = createDiscountDescription(
                couponPolicy.getDiscountType(),
                couponPolicy.getDiscountValue()
        );

        return new AvailableCouponPolicyResponse(
                couponPolicy.getId(),
                couponPolicy.getName(),
                discountDescription,
                couponPolicy.getMinAvailableAmount(),
                couponPolicy.getMaxAppliedAmount(),
                couponPolicy.getAppliedExpiredAt(),
                couponPolicy.getRemainingQuantity(),
                alreadyIssued
        );
    }

    private static String createDiscountDescription(DiscountType discountType, int discountValue) {
        if (discountType == DiscountType.AMOUNT) {
            return String.format("%,d원 할인", discountValue);
        } else {
            return String.format("%d%% 할인", discountValue);
        }
    }
}
