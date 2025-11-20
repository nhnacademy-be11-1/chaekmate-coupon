package shop.chaekmate.api.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.chaekmate.api.coupon.dto.response.AvailableCouponPolicyResponse;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepository;
import shop.chaekmate.api.coupon.repository.IssuedCouponRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;
    private final IssuedCouponRepository issuedCouponRepository;


    public List<AvailableCouponPolicyResponse> getAvailableCouponPolicies(Long memberId) {
        List<CouponPolicy> couponPolicies = couponPolicyRepository.findAvailableCouponPolicies(LocalDateTime.now());

        List<AvailableCouponPolicyResponse> responses = new ArrayList<>();
        for (CouponPolicy couponPolicy : couponPolicies) {
            boolean isIssued = issuedCouponRepository.existsByMemberIdAndCouponPolicyId(memberId, couponPolicy.getId());

            responses.add(AvailableCouponPolicyResponse.from(couponPolicy, isIssued));
        }

        return responses;
    }
}
