package shop.chaekmate.api.coupon.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.exception.InvalidCouponType;
import shop.chaekmate.api.coupon.service.strategy.admin.CouponStrategyFactory;
import shop.chaekmate.api.coupon.service.strategy.admin.CouponTypeStrategy;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouponStrategyFactoryTest {

    @Mock
    private CouponTypeStrategy bookStrategy;

    @Mock
    private CouponTypeStrategy categoryStrategy;

    @Test
    void 지원되는_CouponType을_찾으면_해당_Strategy를_반환한다() {
        // given
        given(bookStrategy.supports(CouponType.BOOK)).willReturn(true);

        CouponStrategyFactory factory = new CouponStrategyFactory(List.of(bookStrategy, categoryStrategy));

        // when
        CouponTypeStrategy result = factory.getStrategy(CouponType.BOOK);

        // then
        assertThat(result).isEqualTo(bookStrategy);
    }

    @Test
    void 지원되는_CouponType이_없으면_InvalidCouponType_예외를_던진다() {
        // given
        given(bookStrategy.supports(CouponType.WELCOME)).willReturn(false);
        given(categoryStrategy.supports(CouponType.WELCOME)).willReturn(false);

        CouponStrategyFactory factory = new CouponStrategyFactory(List.of(bookStrategy, categoryStrategy));

        // when & then
        assertThatThrownBy(() -> factory.getStrategy(CouponType.WELCOME))
                .isInstanceOf(InvalidCouponType.class)
                .hasMessageContaining("WELCOME");
    }

    @Test
    void 여러_Strategy가_있을_때_먼저_매칭된_Strategy를_반환한다() {
        // given
        given(bookStrategy.supports(CouponType.CATEGORY)).willReturn(false);
        given(categoryStrategy.supports(CouponType.CATEGORY)).willReturn(true);

        CouponStrategyFactory factory = new CouponStrategyFactory(List.of(bookStrategy, categoryStrategy));

        // when
        CouponTypeStrategy result = factory.getStrategy(CouponType.CATEGORY);

        // then
        assertThat(result).isEqualTo(categoryStrategy);
    }
}
