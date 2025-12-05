package shop.chaekmate.api.coupon.dto.request;


import shop.chaekmate.api.coupon.entity.type.DiscountType;

public record IssuedCouponDiscountDetailItem(
        Long couponId,
        String name,
        DiscountType discountType,  // RATE or AMOUNT
        Integer rate,               // RATE일 때만 값
        Integer amount,             // AMOUNT일 때만 값
        int discountAmount          // 실제 할인 금액(정률이면 계산된 값)
) {}

