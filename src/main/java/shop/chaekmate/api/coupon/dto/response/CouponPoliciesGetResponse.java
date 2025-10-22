package shop.chaekmate.api.coupon.dto.response;

import java.time.LocalDateTime;
import shop.chaekmate.api.coupon.entity.CouponPolicy;

public record CouponPoliciesGetResponse(
        long couponPolicyId,
        String name,
        String discountType,
        int discountValue,
        LocalDateTime appliedStartedAt,
        LocalDateTime appliedExpiredAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CouponPoliciesGetResponse fromEntity(CouponPolicy couponPolicy) {
        return new CouponPoliciesGetResponse(
                couponPolicy.getId(),
                couponPolicy.getName(),
                couponPolicy.getDiscountType().getName(),
                couponPolicy.getDiscountValue(),
                couponPolicy.getAppliedStartedAt(),
                couponPolicy.getAppliedExpiredAt(),
                couponPolicy.getCreatedAt(),
                couponPolicy.getUpdatedAt()
        );
    }
}
