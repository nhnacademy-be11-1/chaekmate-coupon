package shop.chaekmate.api.coupon.controller;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.IssuedCoupon;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepository;
import shop.chaekmate.api.coupon.repository.IssuedCouponRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)

class IssuedCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Test
    void 사용_가능한_쿠폰_목록을_조회한다() throws Exception {
        // given
        Long memberId = 1L;
        CouponPolicy policy1 = saveCouponPolicy("신규 가입 쿠폰", DiscountType.AMOUNT, 5000);
        CouponPolicy policy2 = saveCouponPolicy("생일 쿠폰", DiscountType.RATE, 10);

        List<IssuedCoupon> coupons = new ArrayList<>();
        coupons.add(createAvailableCoupon(memberId, policy1));
        coupons.add(createAvailableCoupon(memberId, policy2));
        issuedCouponRepository.saveAll(coupons);

        // when, then
        mockMvc.perform(get("/issued-coupons/available")
                        .header("X-Member-Id", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].couponName").value("신규 가입 쿠폰"))
                .andExpect(jsonPath("$[0].discountDescription").value("5,000원 할인"))
                .andExpect(jsonPath("$[1].couponName").value("생일 쿠폰"))
                .andExpect(jsonPath("$[1].discountDescription").value("10% 할인"));
    }

    @Test
    void 사용_가능한_쿠폰이_없으면_빈_배열을_반환한다() throws Exception {
        // given
        Long memberId = 999L;

        // when, then
        mockMvc.perform(get("/issued-coupons/available")
                        .header("X-Member-Id", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void 만료되거나_사용한_쿠폰은_조회되지_않는다() throws Exception {
        // given
        Long memberId = 1L;
        CouponPolicy policy = saveCouponPolicy("테스트 쿠폰", DiscountType.AMOUNT, 5000);

        List<IssuedCoupon> coupons = new ArrayList<>();
        coupons.add(createExpiredCoupon(memberId, policy));
        coupons.add(createUsedCoupon(memberId, policy));
        issuedCouponRepository.saveAll(coupons);

        // when, then
        mockMvc.perform(get("/issued-coupons/available")
                        .header("X-Member-Id", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void 사용한_쿠폰_목록을_조회한다() throws Exception {
        // given
        Long memberId = 1L;
        CouponPolicy policy = saveCouponPolicy("크리스마스 쿠폰", DiscountType.AMOUNT, 10000);

        IssuedCoupon usedCoupon = createUsedCoupon(memberId, policy);
        issuedCouponRepository.save(usedCoupon);

        // when, then
        mockMvc.perform(get("/issued-coupons/used")
                        .header("X-Member-Id", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].couponName").value("크리스마스 쿠폰"))
                .andExpect(jsonPath("$[0].discountDescription").value("10,000원 할인"))
                .andExpect(jsonPath("$[0].usedAt").exists());
    }

    @Test
    void 사용한_쿠폰이_없으면_빈_배열을_반환한다() throws Exception {
        // given
        Long memberId = 999L;

        // when, then
        mockMvc.perform(get("/issued-coupons/used")
                        .header("X-Member-Id", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void 미사용_쿠폰은_사용한_쿠폰_목록에_조회되지_않는다() throws Exception {
        // given
        Long memberId = 1L;
        CouponPolicy policy = saveCouponPolicy("미사용 쿠폰", DiscountType.AMOUNT, 5000);

        IssuedCoupon availableCoupon = createAvailableCoupon(memberId, policy);
        issuedCouponRepository.save(availableCoupon);

        // when, then
        mockMvc.perform(get("/issued-coupons/used")
                        .header("X-Member-Id", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private CouponPolicy saveCouponPolicy(String name, DiscountType discountType, int discountValue) {
        CouponPolicy policy = new CouponPolicy(
                name, CouponType.WELCOME, CouponAppliedPeriodType.THIRTY_DAYS,
                null, null, discountType, discountValue, 10000, 100000L, 100L
        );
        return couponPolicyRepository.save(policy);
    }

    private IssuedCoupon createAvailableCoupon(Long memberId, CouponPolicy policy) {
        LocalDateTime now = LocalDateTime.now();
        return new IssuedCoupon(memberId, policy, now);
    }

    private IssuedCoupon createUsedCoupon(Long memberId, CouponPolicy policy) {
        LocalDateTime now = LocalDateTime.now();
        IssuedCoupon coupon = new IssuedCoupon(memberId, policy, now.minusDays(10));
        coupon.use();
        return coupon;
    }

    private IssuedCoupon createExpiredCoupon(Long memberId, CouponPolicy policy) {
        LocalDateTime issuedAt = LocalDateTime.now().minusDays(40);
        return new IssuedCoupon(memberId, policy, issuedAt);
    }
}
