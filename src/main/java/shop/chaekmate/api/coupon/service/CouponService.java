package shop.chaekmate.api.coupon.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.api.client.CoreClient;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyCreateRequest;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyUpdateRequest;
import shop.chaekmate.api.coupon.dto.response.BooksGetResponse;
import shop.chaekmate.api.coupon.dto.response.CategoriesGetResponse;
import shop.chaekmate.api.coupon.dto.response.CouponPoliciesGetResponse;
import shop.chaekmate.api.coupon.dto.response.CouponPolicyGetResponse;
import shop.chaekmate.api.coupon.entity.CouponAppliedBook;
import shop.chaekmate.api.coupon.entity.CouponAppliedCategory;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.exception.NotFoundCouponPolicy;
import shop.chaekmate.api.coupon.repository.CouponAppliedBookRepository;
import shop.chaekmate.api.coupon.repository.CouponAppliedCategoryRepository;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepository;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponPolicyRepository couponPolicyRepository;
    private final CouponAppliedBookRepository couponAppliedBookRepository;
    private final CouponAppliedCategoryRepository couponAppliedCategoryRepository;

    private final CoreClient coreClient;

    @Transactional
    public long createCouponPolicy(CouponPolicyCreateRequest request) {
        CouponType couponType = request.type();

        CouponPolicy couponPolicy = request.toCouponPolicy();
        couponPolicyRepository.save(couponPolicy);

        if (CouponType.BOOK == couponType) {
            // TODO: id validation
            List<CouponAppliedBook> couponAppliedBooks = request.ids().stream()
                    .map(bookId -> new CouponAppliedBook(couponPolicy, bookId))
                    .toList();

            couponAppliedBookRepository.saveAllInBatch(couponAppliedBooks);
        }

        if (CouponType.CATEGORY == couponType) {
            List<CouponAppliedCategory> couponAppliedCategories = request.ids().stream()
                    .map(categoryId -> new CouponAppliedCategory(couponPolicy, categoryId))
                    .toList();

            couponAppliedCategoryRepository.saveAllInBatch(couponAppliedCategories);
        }

        return couponPolicy.getId();
    }

    @Transactional
    public void updateCouponPolicy(long couponPolicyId, CouponPolicyUpdateRequest request) {
        CouponPolicy couponPolicy = couponPolicyRepository.findById(couponPolicyId)
                .orElseThrow(() -> new NotFoundCouponPolicy(couponPolicyId));

        couponPolicy.update(
                request.name(),
                request.type(),
                request.appliedPeriodType(),
                request.appliedStartedAt(),
                request.appliedExpiredAt(),
                request.discountType(),
                request.discountValue(),
                request.minAvailableAmount(),
                request.maxAppliedAmount(),
                request.remainingQuantity()
        );
    }

    @Transactional
    public void deleteCouponPolicy(long couponPolicyId) {
        if (!couponPolicyRepository.existsById(couponPolicyId)) {
            throw new NotFoundCouponPolicy(couponPolicyId);
        }

        couponPolicyRepository.deleteById(couponPolicyId);
    }

    public List<CouponPoliciesGetResponse> getCouponPolices(Pageable pageable) {
        return couponPolicyRepository.findAll(pageable).stream()
                .map(CouponPoliciesGetResponse::fromEntity)
                .toList();
    }

    public CouponPolicyGetResponse getCouponPolicy(long couponPolicyId) {
        CouponPolicy couponPolicy = couponPolicyRepository.findById(couponPolicyId)
                .orElseThrow(() -> new NotFoundCouponPolicy(couponPolicyId));

        if (CouponType.BOOK == couponPolicy.getType()) {
            List<Long> bookIds = couponAppliedBookRepository.findAllByCouponPolicyId(couponPolicyId).stream()
                    .map(CouponAppliedBook::getBookId)
                    .toList();
            List<BooksGetResponse> responses = coreClient.getFullBooksById(bookIds);
            return CouponPolicyGetResponse.ofBook(couponPolicy, responses);
        }

        if (CouponType.CATEGORY == couponPolicy.getType()) {
            List<Long> categoryIds = couponAppliedCategoryRepository.findAllByCouponPolicyId(couponPolicyId).stream()
                    .map(CouponAppliedCategory::getCategoryId)
                    .toList();
            List<CategoriesGetResponse> categoryResponses = coreClient.getFullCategoriesById(categoryIds);
            return CouponPolicyGetResponse.ofCategory(couponPolicy, categoryResponses);
        }

        return CouponPolicyGetResponse.fromEntity(couponPolicy);
    }
}
