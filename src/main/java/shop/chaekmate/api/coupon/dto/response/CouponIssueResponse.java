package shop.chaekmate.api.coupon.dto.response;

import shop.chaekmate.api.coupon.entity.IssuedCoupon;

import java.time.LocalDateTime;

public record CouponIssueResponse(
        Long issuedCouponId,
        Long couponPolicyId,
        String couponName,
        LocalDateTime issuedAt,
        LocalDateTime expiredAt
) {
    public static CouponIssueResponse from(IssuedCoupon issuedCoupon) {
        return new CouponIssueResponse(
                issuedCoupon.getId(),
                issuedCoupon.getCouponPolicy().getId(),
                issuedCoupon.getCouponPolicy().getName(),
                issuedCoupon.getIssuedAt(),
                issuedCoupon.getExpiredAt()
        );
    }
}
