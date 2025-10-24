package shop.chaekmate.api.coupon.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyCreateRequest;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyUpdateRequest;
import shop.chaekmate.api.coupon.dto.response.CouponPoliciesGetResponse;
import shop.chaekmate.api.coupon.dto.response.CouponPolicyGetResponse;

@Tag(name = "Coupon Admin", description = "관리자 쿠폰 정책 관리 API")
public interface CouponAdminControllerDocs {

    @Operation(
            summary = "쿠폰 정책 생성",
            description = "새로운 쿠폰 정책을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "쿠폰 정책 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    ResponseEntity<Void> createCouponPolicy(@RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "쿠폰 정책 생성 요청 데이터",
            required = true,
            content = @Content(schema = @Schema(implementation = CouponPolicyCreateRequest.class))
    ) CouponPolicyCreateRequest request);

    @Operation(
            summary = "쿠폰 정책 수정",
            description = "기존 쿠폰 정책을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "해당 쿠폰 정책을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    ResponseEntity<Void> updateCouponPolicy(
            @PathVariable long couponPolicyId,
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 쿠폰 정책 데이터",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CouponPolicyUpdateRequest.class))
            ) CouponPolicyUpdateRequest request
    );

    @Operation(
            summary = "쿠폰 정책 삭제",
            description = "쿠폰 정책을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 쿠폰 정책", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    ResponseEntity<Void> deleteCouponPolicy(@PathVariable long couponPolicyId);

    @Operation(
            summary = "쿠폰 정책 목록 조회",
            description = "모든 쿠폰 정책을 페이지 단위로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CouponPoliciesGetResponse.class)))
            }
    )
    ResponseEntity<List<CouponPoliciesGetResponse>> getCouponPolicies(Pageable pageable);

    @Operation(
            summary = "쿠폰 정책 상세 조회",
            description = "쿠폰 정책 ID로 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CouponPolicyGetResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 쿠폰 정책", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    ResponseEntity<CouponPolicyGetResponse> getCouponPolicy(@PathVariable long couponPolicyId);
}