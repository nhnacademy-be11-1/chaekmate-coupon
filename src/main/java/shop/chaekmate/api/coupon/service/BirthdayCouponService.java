package shop.chaekmate.api.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.api.client.CoreApiClient;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.IssuedCoupon;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepository;
import shop.chaekmate.api.coupon.repository.IssuedCouponRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BirthdayCouponService {

    private final CoreApiClient coreApiClient;
    private final CouponPolicyRepository couponPolicyRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    @Transactional
    public void issueMonthlyBirthdayCoupons(int month) {
        log.info("{}월 생일자 조회", month);

        List<Long> memberIds = coreApiClient.getMemberIdsByBirthMonth(month).data();

        if (memberIds.isEmpty()) {
            return;
        }

        log.info("{}월 생일자 {}명", month, memberIds.size());

        CouponPolicy birthdayPolicy = couponPolicyRepository
                .findByType(CouponType.BIRTHDAY)
                .orElseThrow(() -> new IllegalStateException(
                        "생일 쿠폰 정책이 존재하지 않음"
                ));

        for (Long memberId : memberIds) {
            try {
                boolean alreadyIssued = issuedCouponRepository
                        .existsByMemberIdAndCouponTypeAndYear(
                                memberId,
                                CouponType.BIRTHDAY,
                                LocalDateTime.now().getYear()
                        );

                if (alreadyIssued) {
                    log.debug("올해 발급됨: memberId={}", memberId);

                    continue;
                }

                IssuedCoupon issuedCoupon = new IssuedCoupon(
                        memberId,
                        birthdayPolicy,
                        LocalDateTime.now()
                );

                issuedCouponRepository.save(issuedCoupon);

                log.debug("생일 쿠폰 발급 완료: memberId={}", memberId);
            } catch (Exception e) {
                log.error("생일 쿠폰 발급 실패: memberId={}", memberId, e);
            }
        }
    }
}
