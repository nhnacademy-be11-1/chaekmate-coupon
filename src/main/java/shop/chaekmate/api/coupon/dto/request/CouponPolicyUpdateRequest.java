package shop.chaekmate.api.coupon.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;

public record CouponPolicyUpdateRequest(
        @NotBlank(message = "쿠폰 이름은 필수입니다.")
        @Size(max = 100, message = "쿠폰 이름은 최대 100자까지 가능합니다.")
        String name,

        @NotNull(message = "쿠폰 유형은 필수입니다.")
        CouponType type,

        @NotNull(message = "적용 기간 타입은 필수입니다.")
        CouponAppliedPeriodType appliedPeriodType,

        List<Long> ids,

        @NotNull(message = "쿠폰 적용 시작일은 필수입니다.")
        @FutureOrPresent(message = "쿠폰 적용 시작일은 현재보다 이후여야 합니다.")
        LocalDateTime appliedStartedAt,

        @NotNull(message = "쿠폰 적용 종료일은 필수입니다.")
        @Future(message = "쿠폰 적용 종료일은 현재보다 이후여야 합니다.")
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
        long remainingQuantity
) {
}
