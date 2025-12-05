package shop.chaekmate.api.coupon.dto.response;

import java.util.List;
import java.util.Map;
import shop.chaekmate.api.coupon.dto.request.IssuedCouponDetailInfo;
import shop.chaekmate.api.coupon.dto.request.IssuedCouponDiscountDetailItem;

public record BooksAvailableCouponsDetailResponse(
        List<IssuedCouponDetailInfo> coupons,
        Map<Long, List<IssuedCouponDiscountDetailItem>> bookCouponMap
) {}
