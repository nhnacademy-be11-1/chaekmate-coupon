package shop.chaekmate.api.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyCreateRequest;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyUpdateRequest;
import shop.chaekmate.api.coupon.dto.response.CouponPolicyGetResponse;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.entity.type.DiscountType;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepository;
import shop.chaekmate.api.coupon.service.strategy.CouponStrategyFactory;
import shop.chaekmate.api.coupon.service.strategy.CouponTypeStrategy;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Rollback
@Transactional
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouponAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @MockitoBean
    private CouponStrategyFactory couponStrategyFactory;

    @Test
    void 쿠폰정책_생성_API_정상동작하고_DB에_저장된다() throws Exception {
        // given
        CouponPolicyCreateRequest request = new CouponPolicyCreateRequest(
                "생일쿠폰",
                CouponType.BIRTHDAY,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                List.of(1L, 2L),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                DiscountType.AMOUNT,
                5_000,
                1_000,
                50_000L,
                100L
        );

        given(couponStrategyFactory.getStrategy(any())).willReturn(mock(CouponTypeStrategy.class));

        // when & then
        mockMvc.perform(post("/admin/coupon-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        // DB 검증
        List<CouponPolicy> all = couponPolicyRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getName()).isEqualTo("생일쿠폰");
        assertThat(all.get(0).getDiscountValue()).isEqualTo(5000);
    }

    @Test
    void 쿠폰정책_수정_API_정상동작하고_DB에_업데이트된다() throws Exception {
        // given
        CouponPolicy policy = couponPolicyRepository.save(new CouponPolicy(
                "기존쿠폰",
                CouponType.CATEGORY,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                DiscountType.AMOUNT,
                5_000,
                1_000,
                50_000L,
                100L
        ));

        CouponPolicyUpdateRequest request = new CouponPolicyUpdateRequest(
                "수정된쿠폰",
                CouponType.CATEGORY,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                List.of(1L),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                DiscountType.RATE,
                10,
                0,
                20_000L,
                50L
        );

        CouponTypeStrategy mockStrategy = mock(CouponTypeStrategy.class);
        given(couponStrategyFactory.getStrategy(any())).willReturn(mockStrategy);

        // when
        mockMvc.perform(patch("/admin/coupon-policies/{id}", policy.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // then
        CouponPolicy updated = couponPolicyRepository.findById(policy.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("수정된쿠폰");
        assertThat(updated.getDiscountType()).isEqualTo(DiscountType.RATE);
        assertThat(updated.getMaxAppliedAmount()).isEqualTo(20000L);
    }

    @Test
    void 쿠폰정책_삭제_API_정상동작하고_DB에서_삭제된다() throws Exception {
        // given
        CouponPolicy policy = couponPolicyRepository.save(new CouponPolicy(
                "삭제대상쿠폰",
                CouponType.CATEGORY,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                DiscountType.AMOUNT,
                5_000,
                1_000,
                50_000L,
                100L
        ));

        given(couponStrategyFactory.getStrategy(any())).willReturn(mock(CouponTypeStrategy.class));

        // when
        mockMvc.perform(delete("/admin/coupon-policies/{id}", policy.getId()))
                .andExpect(status().isNoContent());

        // then
        boolean exists = couponPolicyRepository.existsById(policy.getId());
        assertThat(exists).isFalse();
    }

    @Test
    void 쿠폰정책_목록조회_API_정상동작하고_JSON_리턴된다() throws Exception {
        // given
        couponPolicyRepository.save(new CouponPolicy(
                "목록쿠폰",
                CouponType.CATEGORY,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                DiscountType.AMOUNT,
                3_000,
                1_000,
                50_000L,
                100L
        ));

        // when & then
        mockMvc.perform(get("/admin/coupon-policies")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("목록쿠폰"))
                .andExpect(jsonPath("$[0].discountType").value("정액"));
    }

    @Test
    void 쿠폰정책_상세조회_API_정상동작하고_JSON_리턴된다() throws Exception {
        // given
        CouponPolicy couponPolicy = couponPolicyRepository.save(new CouponPolicy(
                "상세쿠폰",
                CouponType.CATEGORY,
                CouponAppliedPeriodType.MONTH_LAST_DAY,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                DiscountType.AMOUNT,
                3_000,
                1_000,
                50_000L,
                100L
        ));

        CouponTypeStrategy mockStrategy = mock(CouponTypeStrategy.class);
        given(couponStrategyFactory.getStrategy(any())).willReturn(mockStrategy);
        CouponPolicyGetResponse response = new CouponPolicyGetResponse(
                couponPolicy.getId(),
                couponPolicy.getName(),
                couponPolicy.getType().getName(),
                null,
                couponPolicy.getAppliedPeriodType().getName(),
                couponPolicy.getAppliedStartedAt(),
                couponPolicy.getAppliedExpiredAt(),
                couponPolicy.getDiscountType().getName(),
                couponPolicy.getDiscountValue(),
                couponPolicy.getMinAvailableAmount(),
                couponPolicy.getMaxAppliedAmount(),
                couponPolicy.getRemainingQuantity()
        );

        given(mockStrategy.get(any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/coupon-policies/{id}", couponPolicy.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("상세쿠폰"))
                .andExpect(jsonPath("$.discountValue").value(3000));
    }
}
