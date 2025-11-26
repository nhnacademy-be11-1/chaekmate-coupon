package shop.chaekmate.api.coupon.validation;

import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;

import java.time.LocalDateTime;

/**
 * 쿠폰 정책 날짜 검증을 위한 공통 인터페이스
 */
public interface CouponPolicyDateValidatable {
    CouponAppliedPeriodType appliedPeriodType();
    LocalDateTime appliedStartedAt();
    LocalDateTime appliedExpiredAt();
}
