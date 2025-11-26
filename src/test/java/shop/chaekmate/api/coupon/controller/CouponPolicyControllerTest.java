package shop.chaekmate.api.coupon.controller;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.api.config.QueryDslConfig;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.IssuedCoupon;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepository;
import shop.chaekmate.api.coupon.repository.IssuedCouponRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouponPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Test
    void 발급_가능한_쿠폰_정책_목록을_조회한다() throws Exception {
        Long memberId = 1L;
        saveCouponPolicy("베스트셀러 10% 할인", CouponType.BOOK);
        saveCouponPolicy("IT 도서 20% 할인", CouponType.CATEGORY);

        mockMvc.perform(get("/coupon-policies/available")
                        .header("X-Member-Id", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].couponName").value("IT 도서 20% 할인"))
                .andExpect(jsonPath("$.data[0].alreadyIssued").value(false))
                .andExpect(jsonPath("$.data[1].couponName").value("베스트셀러 10% 할인"))
                .andExpect(jsonPath("$.data[1].alreadyIssued").value(false));
    }

    @Test
    void 이미_발급받은_쿠폰은_alreadyIssued가_true다() throws Exception {
        Long memberId = 1L;
        CouponPolicy policy = saveCouponPolicy("이미 발급받은 쿠폰", CouponType.BOOK);

        IssuedCoupon issuedCoupon = new IssuedCoupon(memberId, policy, LocalDateTime.now());
        issuedCouponRepository.save(issuedCoupon);

        mockMvc.perform(get("/coupon-policies/available")
                        .header("X-Member-Id", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].alreadyIssued").value(true));
    }

    @Test
    void WELCOME과_BIRTHDAY_타입은_조회되지_않는다() throws Exception {
        Long memberId = 1L;
        saveCouponPolicy("신규 가입 쿠폰", CouponType.WELCOME);
        saveCouponPolicy("생일 쿠폰", CouponType.BIRTHDAY);
        saveCouponPolicy("베스트셀러 쿠폰", CouponType.BOOK);

        mockMvc.perform(get("/coupon-policies/available")
                        .header("X-Member-Id", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))  // BOOK만
                .andExpect(jsonPath("$.data[0].couponName").value("베스트셀러 쿠폰"));
    }

    @Test
    void 발급_가능한_쿠폰이_없으면_빈_배열을_반환한다() throws Exception {
        Long memberId = 1L;

        mockMvc.perform(get("/coupon-policies/available")
                        .header("X-Member-Id", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    private CouponPolicy saveCouponPolicy(String name, CouponType type) {
        CouponPolicy policy = new CouponPolicy(
                name, type, CouponAppliedPeriodType.THIRTY_DAYS,
                null, null,
                DiscountType.RATE, 10,
                10000, 50000L, 100L
        );
        return couponPolicyRepository.save(policy);
    }
}
