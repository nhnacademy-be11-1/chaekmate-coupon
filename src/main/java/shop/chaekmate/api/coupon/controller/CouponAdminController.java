package shop.chaekmate.api.coupon.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.api.coupon.controller.docs.CouponAdminControllerDocs;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyCreateRequest;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyUpdateRequest;
import shop.chaekmate.api.coupon.dto.response.CouponPoliciesGetResponse;
import shop.chaekmate.api.coupon.dto.response.CouponPolicyGetResponse;
import shop.chaekmate.api.coupon.service.CouponService;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class CouponAdminController implements CouponAdminControllerDocs {
    private final CouponService couponService;

    // TODO: 권한 제어 필요
    @PostMapping("/coupon-policies")
    public ResponseEntity<Void> createCouponPolicy(@Valid @RequestBody CouponPolicyCreateRequest request) {
        long couponPolicyId = couponService.createCouponPolicy(request);
        return ResponseEntity.created(URI.create("admin/coupon-policies" + couponPolicyId)).build();
    }

    @PatchMapping("/coupon-policies/{couponPolicyId}")
    public ResponseEntity<Void> updateCouponPolicy(
            @PathVariable long couponPolicyId,
            @RequestBody CouponPolicyUpdateRequest request
    ) {
        couponService.updateCouponPolicy(couponPolicyId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/coupon-policies/{couponPolicyId}")
    public ResponseEntity<Void> deleteCouponPolicy(@PathVariable long couponPolicyId) {
        couponService.deleteCouponPolicy(couponPolicyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/coupon-policies")
    public ResponseEntity<List<CouponPoliciesGetResponse>> getCouponPolicies(
            @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        List<CouponPoliciesGetResponse> responses = couponService.getCouponPolices(pageable);
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/coupon-policies/{couponPolicyId}")
    public ResponseEntity<CouponPolicyGetResponse> getCouponPolicy(@PathVariable long couponPolicyId) {
        CouponPolicyGetResponse response = couponService.getCouponPolicy(couponPolicyId);
        return ResponseEntity.ok().body(response);
    }
}
