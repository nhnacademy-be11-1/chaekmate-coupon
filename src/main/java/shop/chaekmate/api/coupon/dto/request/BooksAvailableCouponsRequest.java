package shop.chaekmate.api.coupon.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

// 여러 권의 책들에 대해 사용 가능한 쿠폰을 조회하기 위한 dto
public record BooksAvailableCouponsRequest(
        @NotEmpty(message = "책 목록은 비어있을 수 없습니다.")
        @Valid List<BookCouponCheckRequest> books
) {
}
