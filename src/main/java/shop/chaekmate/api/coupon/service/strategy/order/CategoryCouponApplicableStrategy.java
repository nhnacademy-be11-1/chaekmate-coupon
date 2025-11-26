package shop.chaekmate.api.coupon.service.strategy.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.api.coupon.dto.request.BookCouponCheckRequest;
import shop.chaekmate.api.coupon.entity.CouponAppliedCategory;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.repository.CouponAppliedCategoryRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryCouponApplicableStrategy implements CouponApplicableStrategy {

    private final CouponAppliedCategoryRepository couponAppliedCategoryRepository;

    @Override
    public boolean supports(CouponType couponType) {
        return CouponType.CATEGORY == couponType;
    }

    @Override
    public boolean isApplicable(CouponPolicy policy, BookCouponCheckRequest book) {
        List<CouponAppliedCategory> appliedCategories =
                couponAppliedCategoryRepository.findAllByCouponPolicyId(policy.getId());

        List<Long> bookCategoryIds = book.categoryIds();

        for (CouponAppliedCategory appliedCategory : appliedCategories) {
            for (Long bookCategoryId : bookCategoryIds) {
                if (appliedCategory.getCategoryId() == bookCategoryId) {
                    return true;
                }
            }
        }

        return false;
    }
}
