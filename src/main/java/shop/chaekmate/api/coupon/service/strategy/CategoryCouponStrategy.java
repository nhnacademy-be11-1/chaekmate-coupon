package shop.chaekmate.api.coupon.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.api.client.CoreApiClient;
import shop.chaekmate.api.common.dto.CommonResponse;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyCreateRequest;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyUpdateRequest;
import shop.chaekmate.api.coupon.dto.response.CategoriesGetResponse;
import shop.chaekmate.api.coupon.dto.response.CouponPolicyGetResponse;
import shop.chaekmate.api.coupon.entity.CouponAppliedCategory;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.repository.CouponAppliedCategoryRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryCouponStrategy implements CouponTypeStrategy {
    private final CouponAppliedCategoryRepository categoryRepository;

    private final CoreApiClient coreApiClient;

    @Override
    public boolean supports(CouponType couponType) {
        return CouponType.CATEGORY == couponType;
    }

    @Override
    public void create(CouponPolicy couponPolicy, CouponPolicyCreateRequest request) {
        List<CouponAppliedCategory> categories = request.ids().stream()
                .map(id -> new CouponAppliedCategory(couponPolicy, id))
                .toList();

        categoryRepository.saveAllInBatch(categories);
    }

    @Override
    public void update(CouponPolicy couponPolicy, CouponPolicyUpdateRequest request) {
        categoryRepository.softDeleteAllByCouponPolicy(couponPolicy);

        List<CouponAppliedCategory> categories = request.ids().stream()
                .map(id -> new CouponAppliedCategory(couponPolicy, id))
                .toList();

        categoryRepository.saveAllInBatch(categories);
    }

    @Override
    public void delete(CouponPolicy couponPolicy) {
        categoryRepository.softDeleteAllByCouponPolicy(couponPolicy);
    }

    @Override
    public CouponPolicyGetResponse get(CouponPolicy couponPolicy) {
        List<Long> ids = categoryRepository.findAllByCouponPolicyId(couponPolicy.getId())
                .stream()
                .map(CouponAppliedCategory::getCategoryId)
                .toList();

        CommonResponse<List<List<CategoriesGetResponse>>> responses = coreApiClient.getCategoriesWithParents(ids);
        return CouponPolicyGetResponse.ofCategory(couponPolicy, responses.data());
    }
}
