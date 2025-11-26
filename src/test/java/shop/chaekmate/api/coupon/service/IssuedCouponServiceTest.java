package shop.chaekmate.api.coupon.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.api.coupon.dto.response.IssuedCouponResponse;
import shop.chaekmate.api.coupon.dto.response.UsedCouponResponse;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.IssuedCoupon;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;
import shop.chaekmate.api.coupon.repository.IssuedCouponRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class IssuedCouponServiceTest {

    @InjectMocks
    private IssuedCouponService issuedCouponService;

    @Mock
    private IssuedCouponRepository issuedCouponRepository;

    @Test
    void 사용_가능한_쿠폰_목록을_조회한다() {
        // given
        Long memberId = 1L;

        List<IssuedCoupon> coupons = new ArrayList<>();
        coupons.add(createAvailableCoupon(memberId, "신규 가입 쿠폰", DiscountType.AMOUNT, 5000));
        coupons.add(createAvailableCoupon(memberId, "생일 쿠폰", DiscountType.RATE, 10));

        given(issuedCouponRepository.findAvailableCouponsByMemberId(eq(memberId), any(LocalDateTime.class)))
                .willReturn(coupons);

        // when
        List<IssuedCouponResponse> result = issuedCouponService.getAvailableCoupons(memberId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).couponName()).isEqualTo("신규 가입 쿠폰");
        assertThat(result.get(0).discountDescription()).isEqualTo("5,000원 할인");
        assertThat(result.get(1).couponName()).isEqualTo("생일 쿠폰");
        assertThat(result.get(1).discountDescription()).isEqualTo("10% 할인");
    }

    @Test
    void 사용_가능한_쿠폰이_없으면_빈_리스트를_반환한다() {
        // given
        Long memberId = 1L;
        given(issuedCouponRepository.findAvailableCouponsByMemberId(eq(memberId), any()))
                .willReturn(new ArrayList<>());

        // when
        List<IssuedCouponResponse> result = issuedCouponService.getAvailableCoupons(memberId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 사용한_쿠폰_목록을_조회한다() {
        // given
        Long memberId = 1L;

        List<IssuedCoupon> coupons = new ArrayList<>();
        coupons.add(createUsedCoupon(memberId, "크리스마스 쿠폰", DiscountType.AMOUNT, 10000));

        given(issuedCouponRepository.findUsedCouponsByMemberId(memberId))
                .willReturn(coupons);

        // when
        List<UsedCouponResponse> result = issuedCouponService.getUsedCoupons(memberId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().couponName()).isEqualTo("크리스마스 쿠폰");
        assertThat(result.getFirst().discountDescription()).isEqualTo("10,000원 할인");
    }

    @Test
    void 사용한_쿠폰이_없으면_빈_리스트를_반환한다() {
        // given
        Long memberId = 1L;
        given(issuedCouponRepository.findUsedCouponsByMemberId(memberId))
                .willReturn(new ArrayList<>());

        // when
        List<UsedCouponResponse> result = issuedCouponService.getUsedCoupons(memberId);

        // then
        assertThat(result).isEmpty();
    }

    private IssuedCoupon createAvailableCoupon(Long memberId, String policyName,
                                               DiscountType discountType, int discountValue) {
        CouponPolicy policy = createCouponPolicy(policyName, discountType, discountValue);
        LocalDateTime now = LocalDateTime.now();

        return new IssuedCoupon(memberId, policy, now);
    }

    private IssuedCoupon createUsedCoupon(Long memberId, String policyName,
                                          DiscountType discountType, int discountValue) {
        CouponPolicy policy = createCouponPolicy(policyName, discountType, discountValue);
        LocalDateTime now = LocalDateTime.now();

        IssuedCoupon coupon = new IssuedCoupon(memberId, policy, now.minusDays(10));
        coupon.use();

        return coupon;
    }

    private CouponPolicy createCouponPolicy(String name, DiscountType discountType, int discountValue) {
        return new CouponPolicy(
                name,
                CouponType.WELCOME,
                CouponAppliedPeriodType.THIRTY_DAYS,
                null, null,
                discountType, discountValue,
                10000, 100000L, 100L
        );
    }
}
