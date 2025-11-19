package shop.chaekmate.api.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.api.coupon.dto.response.AvailableCouponPolicyResponse;
import shop.chaekmate.api.coupon.service.CouponPolicyService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon-policies")
public class CouponPolicyController {

    private final CouponPolicyService couponPolicyService;

    @GetMapping("/available")
    public ResponseEntity<List<AvailableCouponPolicyResponse>> getAvailableCouponPolicies(
            @RequestHeader("X-Member-Id") Long memberId) {
        List<AvailableCouponPolicyResponse> responses =
                couponPolicyService.getAvailableCouponPolicies(memberId);
        return ResponseEntity.ok(responses);
    }
}
