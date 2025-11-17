package shop.chaekmate.api.coupon.dto.response;

import shop.chaekmate.api.coupon.entity.IssuedCoupon;
import shop.chaekmate.api.coupon.entity.type.DiscountType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record IssuedCouponResponse(
        Long couponId,
        String couponName,
        String discountDescription,
        long minAvailableAmount,
        long maxAppliedAmount,
        LocalDateTime expiredAt,
        long daysUntilExpired
) {
    public static IssuedCouponResponse from(IssuedCoupon issuedCoupon) {
        String discountDescription = createDiscountDescription(
                issuedCoupon.getCouponPolicy().getDiscountType(),
                issuedCoupon.getCouponPolicy().getDiscountValue()
        );

        // 만료일까지 남은 일 수 계산 로직
        long daysUntilExpired = ChronoUnit.DAYS.between(
                LocalDateTime.now(),
                issuedCoupon.getExpiredAt()
        );

        return new IssuedCouponResponse(
                issuedCoupon.getId(),
                issuedCoupon.getCouponPolicy().getName(),
                discountDescription,
                issuedCoupon.getCouponPolicy().getMinAvailableAmount(),
                issuedCoupon.getCouponPolicy().getMaxAppliedAmount(),
                issuedCoupon.getExpiredAt(),
                daysUntilExpired
        );
    }

    private static String createDiscountDescription(DiscountType type, int value) {
        if (type == DiscountType.AMOUNT) {
            return String.format("%,d원 할인", value);
        } else {
            return String.format("%d%% 할인", value);
        }
    }
}