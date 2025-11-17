package shop.chaekmate.api.coupon.dto.response;

import shop.chaekmate.api.coupon.entity.IssuedCoupon;
import shop.chaekmate.api.coupon.entity.type.DiscountType;

import java.time.LocalDateTime;

public record UsedCouponResponse(
        String couponName,
        String discountDescription,
        LocalDateTime usedAt
) {
    public static UsedCouponResponse from(IssuedCoupon issuedCoupon) {
        String discountDescription = createDiscountDescription(
                issuedCoupon.getCouponPolicy().getDiscountType(),
                issuedCoupon.getCouponPolicy().getDiscountValue()
        );

        return new UsedCouponResponse(
                issuedCoupon.getCouponPolicy().getName(),
                discountDescription,
                issuedCoupon.getUsedAt()
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
