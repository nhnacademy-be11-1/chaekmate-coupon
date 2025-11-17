package shop.chaekmate.api.coupon.service.strategy;

import shop.chaekmate.api.coupon.dto.request.CouponPolicyCreateRequest;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyUpdateRequest;
import shop.chaekmate.api.coupon.dto.response.CouponPolicyGetResponse;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponType;

public interface CouponTypeStrategy {
    boolean supports(CouponType couponType);

    void create(CouponPolicy couponPolicy, CouponPolicyCreateRequest request);

    void update(CouponPolicy couponPolicy, CouponPolicyUpdateRequest request);

    void delete(CouponPolicy couponPolicy);

    CouponPolicyGetResponse get(CouponPolicy couponPolicy);
}
