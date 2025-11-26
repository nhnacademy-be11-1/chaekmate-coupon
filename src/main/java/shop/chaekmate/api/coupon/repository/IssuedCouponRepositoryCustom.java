package shop.chaekmate.api.coupon.repository;

import shop.chaekmate.api.coupon.entity.IssuedCoupon;

import java.time.LocalDateTime;
import java.util.List;

public interface IssuedCouponRepositoryCustom {

    /*
        사용 가능한 쿠폰 조회
        사용 x
        만료 x
     */
    List<IssuedCoupon> findAvailableCouponsByMemberId(Long memberId, LocalDateTime now);

    /*
        사용한 쿠폰
     */
    List<IssuedCoupon> findUsedCouponsByMemberId(Long memberId);

    // 특정 회원이 특정 쿠폰을 발급 받은 적이 있는지 확인
    boolean existsByMemberIdAndCouponPolicyId(Long memberId, Long couponPolicyId);

}
