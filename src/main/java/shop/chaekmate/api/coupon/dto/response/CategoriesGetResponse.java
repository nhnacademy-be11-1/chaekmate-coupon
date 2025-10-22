package shop.chaekmate.api.coupon.dto.response;

public record CategoriesGetResponse(
        Long id,
        String name,
        int depth
) {
}
