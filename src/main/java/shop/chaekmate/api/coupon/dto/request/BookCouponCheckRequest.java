package shop.chaekmate.api.coupon.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

// 각 책에 대한 쿠폰이 적용 가능 여부를 확인하기 위한 dto
public record BookCouponCheckRequest(
        @NotNull(message = "책 ID는 필수입니다.")
        Long bookId,

        @NotNull(message = "카테고리 ID 목록은 필수입니다.")
        List<Long> categoryIds,

        @Positive(message = "금액은 0보다 커야 합니다.")
        Integer amount
) {
}
