package shop.chaekmate.api.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.chaekmate.api.coupon.dto.response.IssuedCouponResponse;
import shop.chaekmate.api.coupon.dto.response.UsedCouponResponse;
import shop.chaekmate.api.coupon.entity.IssuedCoupon;
import shop.chaekmate.api.coupon.repository.IssuedCouponRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssuedCouponService {

    private final IssuedCouponRepository issuedCouponRepository;

    public List<IssuedCouponResponse> getAvailableCoupons(Long memberId) {
        List<IssuedCoupon> coupons = issuedCouponRepository
                .findAvailableCouponsByMemberId(memberId, LocalDateTime.now());

        List<IssuedCouponResponse> responses = new ArrayList<>();
        for (IssuedCoupon coupon : coupons) {
            responses.add(IssuedCouponResponse.from(coupon));
        }

        return responses;
    }

    public List<UsedCouponResponse> getUsedCoupons(Long memberId) {
        List<IssuedCoupon> coupons = issuedCouponRepository
                .findUsedCouponsByMemberId(memberId);

        List<UsedCouponResponse> responses = new ArrayList<>();
        for (IssuedCoupon coupon : coupons) {
            responses.add(UsedCouponResponse.from(coupon));
        }

        return responses;
    }
}
