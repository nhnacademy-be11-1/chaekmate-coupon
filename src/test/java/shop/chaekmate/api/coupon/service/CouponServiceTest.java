package shop.chaekmate.api.coupon.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyCreateRequest;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyUpdateRequest;
import shop.chaekmate.api.coupon.dto.response.CouponPoliciesGetResponse;
import shop.chaekmate.api.coupon.dto.response.CouponPolicyGetResponse;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepository;
import shop.chaekmate.api.coupon.service.strategy.admin.CouponStrategyFactory;
import shop.chaekmate.api.coupon.service.strategy.admin.CouponTypeStrategy;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouponServiceTest {
    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @Mock
    private CouponStrategyFactory couponStrategyFactory;

    @Mock
    private CouponTypeStrategy strategy;

    @Test
    void 쿠폰정책_생성시_정책저장과_전략이_호출된다() {
        // given
        CouponPolicyCreateRequest request = mock(CouponPolicyCreateRequest.class);
        CouponPolicy couponPolicy = createFakeCouponPolicy();

        given(request.toCouponPolicy()).willReturn(couponPolicy);
        given(request.type()).willReturn(CouponType.BOOK);
        given(couponStrategyFactory.getStrategy(CouponType.BOOK)).willReturn(strategy);

        // when
        long result = couponService.createCouponPolicy(request);

        // then
        verify(couponPolicyRepository).save(couponPolicy);
        verify(strategy).create(couponPolicy, request);
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void 쿠폰정책_수정시_전략이_호출되고_정책이_갱신된다() {
        // given
        long id = 1L;
        CouponPolicyUpdateRequest request = mock(CouponPolicyUpdateRequest.class);
        CouponPolicy couponPolicy = createFakeCouponPolicy();

        given(couponPolicyRepository.getById(id)).willReturn(couponPolicy);
        given(request.type()).willReturn(CouponType.CATEGORY);
        given(couponStrategyFactory.getStrategy(CouponType.CATEGORY)).willReturn(strategy);

        // when
        couponService.updateCouponPolicy(id, request);

        // then
        verify(couponPolicyRepository).getById(id);
        verify(strategy).update(couponPolicy, request);
    }

    @Test
    void 쿠폰정책_삭제시_전략과_리포지토리가_호출된다() {
        // given
        long id = 1L;
        CouponPolicy couponPolicy = spy(createFakeCouponPolicy());
        doReturn(CouponType.BOOK).when(couponPolicy).getType();

        given(couponPolicyRepository.getById(id)).willReturn(couponPolicy);
        given(couponPolicy.getType()).willReturn(CouponType.BOOK);
        given(couponStrategyFactory.getStrategy(CouponType.BOOK)).willReturn(strategy);

        // when
        couponService.deleteCouponPolicy(id);

        // then
        verify(strategy).delete(couponPolicy);
        verify(couponPolicyRepository).deleteById(id);
    }

    @Test
    void 쿠폰정책목록을_페이지로_조회하면_DTO리스트로_반환한다() {
        // given
        CouponPolicy couponPolicy = createFakeCouponPolicy();
        given(couponPolicyRepository.findAll(any(PageRequest.class)))
                .willReturn(new PageImpl<>(List.of(couponPolicy)));

        // when
        List<CouponPoliciesGetResponse> result =
                couponService.getCouponPolices(PageRequest.of(0, 10));

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void 쿠폰정책상세를_조회하면_전략에서_DTO를_반환한다() {
        // given
        long id = 1L;
        CouponPolicy couponPolicy = spy(createFakeCouponPolicy());
        CouponPolicyGetResponse response = mock(CouponPolicyGetResponse.class);

        given(couponPolicyRepository.getById(id)).willReturn(couponPolicy);
        given(couponPolicy.getType()).willReturn(CouponType.CATEGORY);
        given(couponStrategyFactory.getStrategy(CouponType.CATEGORY)).willReturn(strategy);
        given(strategy.get(couponPolicy)).willReturn(response);

        // when
        CouponPolicyGetResponse result = couponService.getCouponPolicy(id);

        // then
        verify(strategy).get(couponPolicy);
        assertThat(result).isEqualTo(response);
    }

    private CouponPolicy createFakeCouponPolicy() {
        CouponPolicy couponPolicy = new CouponPolicy(
                "테스트 쿠폰",
                CouponType.CATEGORY,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                null,
                null,
                DiscountType.AMOUNT,
                5_000,
                1_000,
                50_000L,
                100L
        );
        ReflectionTestUtils.setField(couponPolicy, "id", 1L);
        return couponPolicy;
    }
}
