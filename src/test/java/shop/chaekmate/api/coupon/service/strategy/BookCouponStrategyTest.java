package shop.chaekmate.api.coupon.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
import shop.chaekmate.api.coupon.dto.response.BooksGetResponse;
import shop.chaekmate.api.coupon.dto.response.CouponPolicyGetResponse;
import shop.chaekmate.api.coupon.entity.CouponAppliedBook;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;
import shop.chaekmate.api.coupon.repository.CouponAppliedBookRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookCouponStrategyTest {
    @InjectMocks
    private BookCouponStrategy strategy;

    @Mock
    private CouponAppliedBookRepository couponAppliedBookRepository;

    @Mock
    private CoreApiClient coreApiClient;

    @Test
    void supports는_BOOK_타입일_때_true를_반환한다() {
        // when
        boolean result = strategy.supports(CouponType.BOOK);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest(name = "supports는 {0}타입일 때 false를 반환한다")
    @EnumSource(
            value = CouponType.class,
            names = {"WELCOME", "BIRTHDAY", "CATEGORY"}
    )
    void supports는_BOOK_타입이_아닐_때_false를_반환한다(CouponType type) {
        // when
        boolean result = strategy.supports(type);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 쿠폰적용도서를_batch_insert_한다() {
        // given
        CouponPolicy couponPolicy = makeFakePolicy();
        CouponPolicyCreateRequest request = new CouponPolicyCreateRequest(
                "도서 쿠폰",
                CouponType.BOOK,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                List.of(1L, 2L, 3L),
                null,
                null,
                DiscountType.AMOUNT,
                1_000,
                500,
                10_000L,
                100
        );

        // when
        strategy.create(couponPolicy, request);

        // then
        verify(couponAppliedBookRepository, times(1)).saveAllInBatch(anyList());
    }

    @Test
    void 쿠폰_정책_수정시_기존_데이터를_삭제하고_새로_batch_insert_한다() {
        // given
        CouponPolicy couponPolicy = makeFakePolicy();
        CouponPolicyUpdateRequest request = new CouponPolicyUpdateRequest(
                "도서 쿠폰",
                CouponType.BOOK,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                List.of(4L, 5L, 6L),
                null,
                null,
                DiscountType.AMOUNT,
                2_000,
                500,
                10_000L,
                100
        );

        // when
        strategy.update(couponPolicy, request);

        // then
        verify(couponAppliedBookRepository, times(1)).deleteAllByCouponPolicy(couponPolicy);
        verify(couponAppliedBookRepository, times(1)).saveAllInBatch(anyList());
    }

    @Test
    void 해당_쿠폰정책의_모든_도서를_삭제한다() {
        // given
        CouponPolicy couponPolicy = makeFakePolicy();

        // when
        strategy.delete(couponPolicy);

        // then
        verify(couponAppliedBookRepository).deleteAllByCouponPolicy(couponPolicy);
        verifyNoMoreInteractions(couponAppliedBookRepository);
    }

    @Test
    void 도서정보를_core_서버에서_조회한다() {
        // given
        CouponPolicy couponPolicy = makeFakePolicy();
        ReflectionTestUtils.setField(couponPolicy, "id", 1L);

        List<CouponAppliedBook> appliedBooks = List.of(
                new CouponAppliedBook(couponPolicy, 101L),
                new CouponAppliedBook(couponPolicy, 102L)
        );

        given(couponAppliedBookRepository.findAllByCouponPolicyId(1L))
                .willReturn(appliedBooks);

        List<BooksGetResponse> responses = List.of(
                new BooksGetResponse(101L, "Effective Java"),
                new BooksGetResponse(102L, "Clean Code")
        );

        given(coreApiClient.getFullBooksById(anyList())).willReturn(responses);

        // when
        CouponPolicyGetResponse result = strategy.get(couponPolicy);

        // then
        verify(couponAppliedBookRepository).findAllByCouponPolicyId(1L);
        verify(coreApiClient).getFullBooksById(anyList());

        assertThat(result).isNotNull();
    }

    private CouponPolicy makeFakePolicy() {
        CouponPolicy policy = new CouponPolicy(
                "name",
                CouponType.BOOK,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                null, null,
                DiscountType.AMOUNT,
                1_000,
                500,
                10_000L,
                100
        );

        ReflectionTestUtils.setField(policy, "id", 1L);

        return policy;
    }
}