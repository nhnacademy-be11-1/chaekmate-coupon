package shop.chaekmate.api.coupon.repository;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.api.config.QueryDslConfig;
import shop.chaekmate.api.coupon.entity.CouponAppliedBook;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouponAppliedBookRepositoryTest {
    @Autowired
    private CouponAppliedBookRepository couponAppliedBookRepository;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    void softDeleteAllByCouponPolicy_호출시_deletedAt이_정상적으로_갱신된다() {
        // given
        CouponPolicy couponPolicy = couponPolicyRepository.save(new CouponPolicy(
                "도서 쿠폰",
                CouponType.BOOK,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                DiscountType.AMOUNT,
                5000,
                1000,
                50000L,
                100L
        ));

        List<CouponAppliedBook> books = List.of(
                new CouponAppliedBook(couponPolicy, 101L),
                new CouponAppliedBook(couponPolicy, 102L),
                new CouponAppliedBook(couponPolicy, 103L)
        );
        couponAppliedBookRepository.saveAll(books);

        // when
        couponAppliedBookRepository.softDeleteAllByCouponPolicy(couponPolicy);

        // then
        Long deletedCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM coupon_applied_book WHERE deleted_at IS NOT NULL",
                Long.class
        );
        assertThat(deletedCount).isEqualTo(3L);

        List<CouponAppliedBook> remaining = couponAppliedBookRepository.findAll();
        assertThat(remaining).isEmpty();
    }
}
