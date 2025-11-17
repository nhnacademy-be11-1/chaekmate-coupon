package shop.chaekmate.api.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.api.coupon.dto.response.IssuedCouponResponse;
import shop.chaekmate.api.coupon.dto.response.UsedCouponResponse;
import shop.chaekmate.api.coupon.service.IssuedCouponService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("issued-coupons")
public class IssuedCouponController {

    private final IssuedCouponService issuedCouponService;

    @GetMapping("/available")
    public ResponseEntity<List<IssuedCouponResponse>> getAvailableCoupons(
            @RequestHeader("X-USER-ID") Long memberId) {
        List<IssuedCouponResponse> responses = issuedCouponService.getAvailableCoupons(memberId);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/used")
    public ResponseEntity<List<UsedCouponResponse>> getUsedCoupons(
            @RequestHeader("X-USER-ID") Long memberId) {
        List<UsedCouponResponse> responses = issuedCouponService.getUsedCoupons(memberId);

        return ResponseEntity.ok(responses);
    }
}
