package shop.chaekmate.api.coupon.dto.request;

import jakarta.validation.constraints.Positive;

public record CalculateDiscountRequest(
        @Positive(message = "주문 금액은 0보다 커야 합니다.")
        Integer orderAmount
) {}
