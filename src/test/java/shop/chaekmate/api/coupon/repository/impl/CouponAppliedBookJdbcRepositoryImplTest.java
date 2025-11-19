package shop.chaekmate.api.coupon.repository.impl;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import shop.chaekmate.api.config.QueryDslConfig;
import shop.chaekmate.api.coupon.entity.CouponAppliedBook;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;
import shop.chaekmate.api.coupon.repository.CouponAppliedBookJdbcRepository;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@SuppressWarnings("NonAsciiCharacters")
@Import({CouponAppliedBookJdbcRepositoryImpl.class, QueryDslConfig.class})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouponAppliedBookJdbcRepositoryImplTest {
    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private CouponAppliedBookJdbcRepository couponAppliedBookRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 여러_쿠폰이_적용된_도서를_벌크_삽입을_한다() {
        // given
        CouponPolicy couponPolicy = new CouponPolicy(
                "name",
                CouponType.BIRTHDAY,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                null,
                null,
                DiscountType.AMOUNT,
                5_000,
                1_000,
                50_000L,
                100L
        );

        couponPolicyRepository.save(couponPolicy);

        List<CouponAppliedBook> couponAppliedBooks = List.of(
                new CouponAppliedBook(couponPolicy, 101L),
                new CouponAppliedBook(couponPolicy, 102L),
                new CouponAppliedBook(couponPolicy, 103L)
        );

        // when
        couponAppliedBookRepository.saveAllInBatch(couponAppliedBooks);

        // then
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM coupon_applied_book WHERE coupon_policy_id = ?",
                Integer.class,
                1L
        );

        assertThat(count).isEqualTo(3);
    }
}
