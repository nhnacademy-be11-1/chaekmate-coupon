package shop.chaekmate.api.coupon.repository;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.api.config.QueryDslConfig;
import shop.chaekmate.api.coupon.entity.CouponAppliedCategory;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouponAppliedCategoryRepositoryTest {
    @Autowired
    private CouponAppliedCategoryRepository couponAppliedCategoryRepository;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    void softDeleteAllByCouponPolicy_호출시_deletedAt이_정상적으로_갱신된다() {
        // given
        CouponPolicy couponPolicy = couponPolicyRepository.save(new CouponPolicy(
                "카테고리 쿠폰",
                CouponType.CATEGORY,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5),
                DiscountType.AMOUNT,
                3000,
                1000,
                50000L,
                100L
        ));

        List<CouponAppliedCategory> categories = List.of(
                new CouponAppliedCategory(couponPolicy, 11L),
                new CouponAppliedCategory(couponPolicy, 12L),
                new CouponAppliedCategory(couponPolicy, 13L)
        );
        couponAppliedCategoryRepository.saveAll(categories);

        // when
        couponAppliedCategoryRepository.softDeleteAllByCouponPolicy(couponPolicy);

        Long deletedCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM coupon_applied_category WHERE deleted_at IS NOT NULL",
                Long.class
        );
        assertThat(deletedCount).isEqualTo(3L);

        List<CouponAppliedCategory> remaining = couponAppliedCategoryRepository.findAll();
        assertThat(remaining).isEmpty();
    }
}
