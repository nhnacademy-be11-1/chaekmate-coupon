package shop.chaekmate.api.coupon.dto.response;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import shop.chaekmate.api.coupon.entity.CouponPolicy;

public record CouponPolicyGetResponse(
        long couponPolicyId,
        String name,
        String type,
        String couponAppliedTargetNames,
        String couponAppliedPeriodType,
        LocalDateTime appliedStartedAt,
        LocalDateTime appliedExpiredAt,
        String discountType,
        int discountValue,
        int minAvailableAmount,
        long maxAppliedAmount,
        long remainingQuantity
) {
    public static CouponPolicyGetResponse ofBook(CouponPolicy couponPolicy, List<BooksGetResponse> responses) {
        String bookNames = responses.stream()
                .map(BooksGetResponse::name)
                .collect(Collectors.joining(", "));

        return new CouponPolicyGetResponse(
                couponPolicy.getId(),
                couponPolicy.getName(),
                couponPolicy.getType().getName(),
                bookNames,
                couponPolicy.getAppliedPeriodType().getName(),
                couponPolicy.getAppliedStartedAt(),
                couponPolicy.getAppliedExpiredAt(),
                couponPolicy.getDiscountType().getName(),
                couponPolicy.getDiscountValue(),
                couponPolicy.getMinAvailableAmount(),
                couponPolicy.getMaxAppliedAmount(),
                couponPolicy.getRemainingQuantity()
        );
    }

    public static CouponPolicyGetResponse ofCategory(CouponPolicy couponPolicy, List<List<CategoriesGetResponse>> responses) {
        String categoryNames = responses.stream()
                .map(path -> path.stream()
                        .sorted(Comparator.comparingInt(CategoriesGetResponse::depth))
                        .map(CategoriesGetResponse::name)
                        .collect(Collectors.joining(" > "))
                )
                .collect(Collectors.joining(", "));

        return new CouponPolicyGetResponse(
                couponPolicy.getId(),
                couponPolicy.getName(),
                couponPolicy.getType().getName(),
                categoryNames,
                couponPolicy.getAppliedPeriodType().getName(),
                couponPolicy.getAppliedStartedAt(),
                couponPolicy.getAppliedExpiredAt(),
                couponPolicy.getDiscountType().getName(),
                couponPolicy.getDiscountValue(),
                couponPolicy.getMinAvailableAmount(),
                couponPolicy.getMaxAppliedAmount(),
                couponPolicy.getRemainingQuantity()
        );
    }
}
