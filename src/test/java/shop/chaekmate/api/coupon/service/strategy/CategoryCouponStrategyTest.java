package shop.chaekmate.api.coupon.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shop.chaekmate.api.client.CoreApiClient;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyCreateRequest;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyUpdateRequest;
import shop.chaekmate.api.coupon.dto.response.CategoriesGetResponse;
import shop.chaekmate.api.coupon.dto.response.CouponPolicyGetResponse;
import shop.chaekmate.api.coupon.entity.CouponAppliedCategory;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;
import shop.chaekmate.api.coupon.repository.CouponAppliedCategoryRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CategoryCouponStrategyTest {
    @InjectMocks
    private CategoryCouponStrategy strategy;

    @Mock
    private CouponAppliedCategoryRepository categoryRepository;

    @Mock
    private CoreApiClient coreApiClient;

    @Test
    void supports는_CATEGORY_타입일_때_true를_반환한다() {
        // when
        boolean result = strategy.supports(CouponType.CATEGORY);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest(name = "supports는 {0}타입일 때 false를 반환한다")
    @EnumSource(
            value = CouponType.class,
            names = {"WELCOME", "BIRTHDAY", "BOOK"}
    )
    void supports는_CATEGORY_타입이_아닐_때_false를_반환한다(CouponType couponType) {
        // when
        boolean result = strategy.supports(couponType);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void create는_요청된_카테고리_ID로_엔티티를_저장한다() {
        // given
        CouponPolicy couponPolicy = createFakeCouponPolicy();
        CouponPolicyCreateRequest request = new CouponPolicyCreateRequest(
                "카테고리 쿠폰",
                CouponType.CATEGORY,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                List.of(1L, 2L, 3L),
                null,
                null,
                DiscountType.AMOUNT,
                10_000,
                1_000,
                50_000L,
                100
        );

        // when
        strategy.create(couponPolicy, request);

        // then
        verify(categoryRepository, times(1)).saveAllInBatch(any());
    }

    @Test
    void update는_기존_카테고리를_삭제하고_새로_저장한다() {
        // given
        CouponPolicy couponPolicy = createFakeCouponPolicy();
        CouponPolicyUpdateRequest request = new CouponPolicyUpdateRequest(
                "변경된 이름",
                CouponType.CATEGORY,
                CouponAppliedPeriodType.THIRTY_DAYS,
                List.of(10L, 20L),
                null,
                null,
                DiscountType.RATE,
                20,
                1000,
                10000L,
                50
        );

        // when
        strategy.update(couponPolicy, request);

        // then
        verify(categoryRepository, times(1)).softDeleteAllByCouponPolicy(couponPolicy);
        verify(categoryRepository, times(1)).saveAllInBatch(any());
    }

    @Test
    void delete는_카테고리_전체를_삭제한다() {
        // given
        CouponPolicy couponPolicy = createFakeCouponPolicy();

        // when
        strategy.delete(couponPolicy);

        // then
        verify(categoryRepository, times(1)).softDeleteAllByCouponPolicy(couponPolicy);
    }

    @Test
    void get은_DB에서_ID목록을_조회하고_API를_호출한다() {
        // given
        CouponPolicy couponPolicy = createFakeCouponPolicy();
        given(categoryRepository.findAllByCouponPolicyId(couponPolicy.getId()))
                .willReturn(List.of(
                        new CouponAppliedCategory(couponPolicy, 3L),
                        new CouponAppliedCategory(couponPolicy, 7L)
                ));

        List<List<CategoriesGetResponse>> categoriesWithParents = List.of(
                List.of(
                        new CategoriesGetResponse(1L, "국내도서", 0),
                        new CategoriesGetResponse(3L, "경영", 1)
                ),
                List.of(
                        new CategoriesGetResponse(1L, "국내도서", 0),
                        new CategoriesGetResponse(7L, "소설", 1)
                )
        );

        given(coreApiClient.getCategoriesWithParents(eq(List.of(3L, 7L))))
                .willReturn(categoriesWithParents);


        // when
        CouponPolicyGetResponse result = strategy.get(couponPolicy);

        // then
        assertThat(result).isNotNull();
        assertThat(result.couponAppliedTargetNames()).contains("국내도서 > 경영");
        assertThat(result.couponAppliedTargetNames()).contains("국내도서 > 소설");

        verify(categoryRepository).findAllByCouponPolicyId(couponPolicy.getId());
        verify(coreApiClient).getCategoriesWithParents(anyList());
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
                100
        );
        ReflectionTestUtils.setField(couponPolicy, "id", 1L);
        return couponPolicy;
    }
}
