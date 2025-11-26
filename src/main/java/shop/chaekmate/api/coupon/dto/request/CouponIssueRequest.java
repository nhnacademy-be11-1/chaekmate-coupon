package shop.chaekmate.api.coupon.dto.request;

import jakarta.validation.constraints.NotNull;

public record CouponIssueRequest(
        @NotNull(message = "쿠폰 정책 ID는 필수입니다.")
        Long couponPolicyId
) {
}
