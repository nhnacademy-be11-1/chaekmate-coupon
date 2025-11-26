package shop.chaekmate.api.coupon.dto.response;

import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.IssuedCoupon;
import shop.chaekmate.api.coupon.entity.type.DiscountType;

// 발급된 쿠폰의 정보
// 주문 페이지 프론트에서 쿠폰 선택 드롭다운에 표시할 요소들임.
public record IssuedCouponSimpleResponse(
        Long issuedCouponId,
        String couponName,
        DiscountType discountType,
        Integer discountValue,
        Integer minAvailableAmount,
        Long maxAppliedAmount
) {
    public static IssuedCouponSimpleResponse from(IssuedCoupon issuedCoupon) {
        CouponPolicy policy = issuedCoupon.getCouponPolicy();

        return new IssuedCouponSimpleResponse(
                issuedCoupon.getId(),
                policy.getName(),
                policy.getDiscountType(),
                policy.getDiscountValue(),
                policy.getMinAvailableAmount(),
                policy.getMaxAppliedAmount()
        );
    }
}
