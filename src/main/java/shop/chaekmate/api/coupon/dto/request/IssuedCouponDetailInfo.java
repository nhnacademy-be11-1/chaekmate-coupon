package shop.chaekmate.api.coupon.dto.request;


import shop.chaekmate.api.coupon.entity.type.DiscountType;

public record IssuedCouponDetailInfo(
        Long couponId,
        String name,
        DiscountType discountType,  // RATE or AMOUNT
        Integer rate,
        Integer amount
) {}
