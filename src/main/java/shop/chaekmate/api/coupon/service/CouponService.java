package shop.chaekmate.api.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyCreateRequest;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyUpdateRequest;
import shop.chaekmate.api.coupon.dto.response.CouponPoliciesGetResponse;
import shop.chaekmate.api.coupon.dto.response.CouponPolicyGetResponse;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepository;
import shop.chaekmate.api.coupon.service.strategy.admin.CouponStrategyFactory;
import shop.chaekmate.api.coupon.service.strategy.admin.CouponTypeStrategy;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponPolicyRepository couponPolicyRepository;

    private final CouponStrategyFactory couponStrategyFactory;

    @Transactional
    public long createCouponPolicy(CouponPolicyCreateRequest request) {
        CouponPolicy couponPolicy = request.toCouponPolicy();
        couponPolicyRepository.save(couponPolicy);

        CouponTypeStrategy strategy = couponStrategyFactory.getStrategy(request.type());
        strategy.create(couponPolicy, request);

        return couponPolicy.getId();
    }

    @Transactional
    public void updateCouponPolicy(long couponPolicyId, CouponPolicyUpdateRequest request) {
        CouponPolicy couponPolicy = couponPolicyRepository.getById(couponPolicyId);

        CouponTypeStrategy strategy = couponStrategyFactory.getStrategy(request.type());
        strategy.update(couponPolicy, request);

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
        CouponPolicy couponPolicy = couponPolicyRepository.getById(couponPolicyId);

        CouponTypeStrategy strategy = couponStrategyFactory.getStrategy(couponPolicy.getType());
        strategy.delete(couponPolicy);

        couponPolicyRepository.deleteById(couponPolicyId);
    }

    public List<CouponPoliciesGetResponse> getCouponPolices(Pageable pageable) {
        return couponPolicyRepository.findAll(pageable).stream()
                .map(CouponPoliciesGetResponse::fromEntity)
                .toList();
    }

    public CouponPolicyGetResponse getCouponPolicy(long couponPolicyId) {
        CouponPolicy couponPolicy = couponPolicyRepository.getById(couponPolicyId);

        CouponTypeStrategy strategy = couponStrategyFactory.getStrategy(couponPolicy.getType());
        return strategy.get(couponPolicy);
    }
}
