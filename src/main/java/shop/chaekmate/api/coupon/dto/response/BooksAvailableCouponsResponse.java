package shop.chaekmate.api.coupon.dto.response;

import java.util.List;
import java.util.Map;

// 사용가능한 쿠폰 목록과 책 ID와 적용 가능한 쿠폰 ID를 전달하는 dto
public record BooksAvailableCouponsResponse(
        List<IssuedCouponSimpleResponse> availableCoupons,
        Map<Long, List<Long>> bookCouponMap
) {
}
