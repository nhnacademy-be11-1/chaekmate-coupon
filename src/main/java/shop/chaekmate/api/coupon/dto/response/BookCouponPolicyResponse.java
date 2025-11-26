package shop.chaekmate.api.coupon.dto.response;

import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;

import java.time.LocalDateTime;

public record BookCouponPolicyResponse(
        Long id,
        String name,
        CouponType type,
        CouponAppliedPeriodType appliedPeriodType,
        LocalDateTime appliedStartedAt,
        LocalDateTime appliedExpiredAt,
        DiscountType discountType,
        Integer discountValue,
        Integer minAvailableAmount,
        Long maxAppliedAmount,
        boolean isIssued
) {
    public static BookCouponPolicyResponse from(CouponPolicy couponPolicy, boolean isIssued) {
        return new BookCouponPolicyResponse(
                couponPolicy.getId(),
                couponPolicy.getName(),
                couponPolicy.getType(),
                couponPolicy.getAppliedPeriodType(),
                couponPolicy.getAppliedStartedAt(),
                couponPolicy.getAppliedExpiredAt(),
                couponPolicy.getDiscountType(),
                couponPolicy.getDiscountValue(),
                couponPolicy.getMinAvailableAmount(),
                couponPolicy.getMaxAppliedAmount(),
                isIssued
        );
    }
}
