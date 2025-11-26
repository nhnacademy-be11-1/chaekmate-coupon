package shop.chaekmate.api.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.api.coupon.dto.request.BooksAvailableCouponsRequest;
import shop.chaekmate.api.coupon.dto.request.CalculateDiscountRequest;
import shop.chaekmate.api.coupon.dto.request.CouponIssueRequest;
import shop.chaekmate.api.coupon.dto.response.*;
import shop.chaekmate.api.coupon.service.IssuedCouponService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("issued-coupons")
public class IssuedCouponController {

    private final IssuedCouponService issuedCouponService;

    @GetMapping("/available")
    public ResponseEntity<List<IssuedCouponResponse>> getAvailableCoupons(
            @RequestHeader("X-Member-Id") Long memberId) {
        List<IssuedCouponResponse> responses = issuedCouponService.getAvailableCoupons(memberId);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/used")
    public ResponseEntity<List<UsedCouponResponse>> getUsedCoupons(
            @RequestHeader("X-Member-Id") Long memberId) {
        List<UsedCouponResponse> responses = issuedCouponService.getUsedCoupons(memberId);

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<CouponIssueResponse> issueCoupon(
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody CouponIssueRequest request) {
        CouponIssueResponse response = issuedCouponService.issueCoupon(memberId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 주문 페이지 쿠폰조회
    // 주문 서버에서 호출 api
    @PostMapping("/for-books")
    public ResponseEntity<BooksAvailableCouponsResponse> getAvailableCouponsForBooks(
            @RequestHeader("X-Member-Id") Long memberId,
            @RequestBody @Valid BooksAvailableCouponsRequest request) {
        BooksAvailableCouponsResponse response =
                issuedCouponService.getAvailableCouponsForBooks(memberId, request);
        return ResponseEntity.ok(response);
    }

    // 할인 금액 계산
    // 주문 서버에서 호출 api
    @PostMapping("/{issuedCouponId}/calculate")
    public ResponseEntity<CalculateDiscountResponse> calculateDiscount(
            @RequestHeader("X-Member-Id") Long memberId,
            @PathVariable Long issuedCouponId,
            @RequestBody @Valid CalculateDiscountRequest request) {
        CalculateDiscountResponse response =
                issuedCouponService.calculateDiscount(memberId, issuedCouponId, request);
        return ResponseEntity.ok(response);
    }

    // 쿠폰 사용처리
    // 주문 서버에서 호출 api
    @PostMapping("/{issuedCouponId}/use")
    public ResponseEntity<Void> useCoupon(
            @RequestHeader("X-Member-Id") Long memberId,
            @PathVariable Long issuedCouponId) {
        issuedCouponService.useCoupon(memberId, issuedCouponId);
        return ResponseEntity.ok().build();
    }
}
