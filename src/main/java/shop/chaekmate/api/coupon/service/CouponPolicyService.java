package shop.chaekmate.api.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.chaekmate.api.coupon.dto.response.AvailableCouponPolicyResponse;
import shop.chaekmate.api.coupon.dto.response.BookCouponPolicyResponse;
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

    public List<BookCouponPolicyResponse> getAvailableCouponPoliciesForBook(
            Long bookId,
            List<Long> categoryIds,
            Long memberId) {
        LocalDateTime now = LocalDateTime.now();

        List<CouponPolicy> bookCoupons = couponPolicyRepository.findAvailableCouponPoliciesByBookId(bookId, now);

        List<CouponPolicy> categoryCoupons = couponPolicyRepository.findAvailableCouponPoliciesByCategoryIds(categoryIds, now);

        List<CouponPolicy> allCoupons = new ArrayList<>();
        allCoupons.addAll(bookCoupons);
        allCoupons.addAll(categoryCoupons);

        List<BookCouponPolicyResponse> responses = new ArrayList<>();
        for (CouponPolicy couponPolicy : allCoupons) {
            boolean isIssued = issuedCouponRepository
                    .existsByMemberIdAndCouponPolicyId(memberId, couponPolicy.getId());

            responses.add(BookCouponPolicyResponse.from(couponPolicy, isIssued));
        }

        return responses;
    }
}
