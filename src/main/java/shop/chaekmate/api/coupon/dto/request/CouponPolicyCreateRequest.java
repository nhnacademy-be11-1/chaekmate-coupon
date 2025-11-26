package shop.chaekmate.api.coupon.dto.request;

import jakarta.validation.constraints.*;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;
import shop.chaekmate.api.coupon.validation.CouponPolicyDateValidatable;
import shop.chaekmate.api.coupon.validation.annotation.ValidCouponPolicyDates;

import java.time.LocalDateTime;
import java.util.List;

@ValidCouponPolicyDates
public record CouponPolicyCreateRequest(
        @NotBlank(message = "쿠폰 이름은 필수입니다.")
        @Size(max = 100, message = "쿠폰 이름은 최대 100자까지 가능합니다.")
        String name,

        @NotNull(message = "쿠폰 유형은 필수입니다.")
        CouponType type,

        @NotNull(message = "적용 기간 타입은 필수입니다.")
        CouponAppliedPeriodType appliedPeriodType,

        List<Long> ids,

        LocalDateTime appliedStartedAt,
        LocalDateTime appliedExpiredAt,

        @NotNull(message = "할인 유형은 필수입니다.")
        DiscountType discountType,

        @Positive(message = "할인 금액 또는 비율은 0보다 커야 합니다.")
        int discountValue,

        @PositiveOrZero(message = "최소 사용 금액은 0 이상이어야 합니다.")
        Integer minAvailableAmount,

        @PositiveOrZero(message = "최대 적용 금액은 0 이상이어야 합니다.")
        Long maxAppliedAmount,

        @PositiveOrZero(message = "남은 수량은 0 이상이어야 합니다.")
        Long remainingQuantity
) implements CouponPolicyDateValidatable {

    public CouponPolicy toCouponPolicy() {
        return new CouponPolicy(
                name,
                type,
                appliedPeriodType,
                appliedStartedAt,
                appliedExpiredAt,
                discountType,
                discountValue,
                minAvailableAmount,
                maxAppliedAmount,
                remainingQuantity
        );
    }
}
