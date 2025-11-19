package shop.chaekmate.api.coupon.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shop.chaekmate.api.coupon.dto.response.AvailableCouponPolicyResponse;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepository;
import shop.chaekmate.api.coupon.repository.IssuedCouponRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouponPolicyServiceTest {

    @InjectMocks
    private CouponPolicyService couponPolicyService;

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @Mock
    private IssuedCouponRepository issuedCouponRepository;

    @Test
    void 발급_가능한_쿠폰_목록을_조회한다() {
        // given
        Long memberId = 1L;
        CouponPolicy policy1 = createPolicy(1L, "베스트셀러 쿠폰");
        CouponPolicy policy2 = createPolicy(2L, "IT 도서 쿠폰");

        given(couponPolicyRepository.findAvailableCouponPolicies(any()))
                .willReturn(List.of(policy1, policy2));
        given(issuedCouponRepository.existsByMemberIdAndCouponPolicyId(anyLong(), anyLong()))
                .willReturn(false);

        // when
        List<AvailableCouponPolicyResponse> result =
                couponPolicyService.getAvailableCouponPolicies(memberId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).couponName()).isEqualTo("베스트셀러 쿠폰");
        assertThat(result.get(0).alreadyIssued()).isFalse();  // ✅ alreadyIssued
    }

    @Test
    void 이미_발급받은_쿠폰은_alreadyIssued가_true다() {
        // given
        Long memberId = 1L;
        CouponPolicy policy = createPolicy(1L, "테스트 쿠폰");

        given(couponPolicyRepository.findAvailableCouponPolicies(any()))
                .willReturn(List.of(policy));
        given(issuedCouponRepository.existsByMemberIdAndCouponPolicyId(memberId, 1L))
                .willReturn(true);

        // when
        List<AvailableCouponPolicyResponse> result =
                couponPolicyService.getAvailableCouponPolicies(memberId);

        // then
        assertThat(result.get(0).alreadyIssued()).isTrue();  // ✅ alreadyIssued
    }

    @Test
    void 발급_가능한_쿠폰이_없으면_빈_리스트를_반환한다() {
        // given
        given(couponPolicyRepository.findAvailableCouponPolicies(any()))
                .willReturn(List.of());

        // when
        List<AvailableCouponPolicyResponse> result =
                couponPolicyService.getAvailableCouponPolicies(1L);

        // then
        assertThat(result).isEmpty();
    }

    private CouponPolicy createPolicy(Long id, String name) {
        CouponPolicy policy = new CouponPolicy(
                name,
                CouponType.BOOK,
                CouponAppliedPeriodType.THIRTY_DAYS,
                null, null,
                DiscountType.RATE, 10,
                10000, 50000L, 100L
        );
        ReflectionTestUtils.setField(policy, "id", id);
        return policy;
    }
}