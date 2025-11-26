package shop.chaekmate.api.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.api.coupon.dto.request.BookCouponCheckRequest;
import shop.chaekmate.api.coupon.dto.request.BooksAvailableCouponsRequest;
import shop.chaekmate.api.coupon.dto.request.CalculateDiscountRequest;
import shop.chaekmate.api.coupon.dto.request.CouponIssueRequest;
import shop.chaekmate.api.coupon.dto.response.*;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.IssuedCoupon;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.exception.AlreadyIssuedCouponException;
import shop.chaekmate.api.coupon.repository.CouponPolicyRepository;
import shop.chaekmate.api.coupon.repository.IssuedCouponRepository;
import shop.chaekmate.api.coupon.service.strategy.order.CouponApplicableStrategy;
import shop.chaekmate.api.coupon.service.strategy.order.CouponApplicableStrategyFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IssuedCouponService {

    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    private final CouponApplicableStrategyFactory strategyFactory;

    public List<IssuedCouponResponse> getAvailableCoupons(Long memberId) {
        List<IssuedCoupon> coupons = issuedCouponRepository
                .findAvailableCouponsByMemberId(memberId, LocalDateTime.now());

        List<IssuedCouponResponse> responses = new ArrayList<>();
        for (IssuedCoupon coupon : coupons) {
            responses.add(IssuedCouponResponse.from(coupon));
        }

        return responses;
    }

    public List<UsedCouponResponse> getUsedCoupons(Long memberId) {
        List<IssuedCoupon> coupons = issuedCouponRepository
                .findUsedCouponsByMemberId(memberId);

        List<UsedCouponResponse> responses = new ArrayList<>();
        for (IssuedCoupon coupon : coupons) {
            responses.add(UsedCouponResponse.from(coupon));
        }

        return responses;
    }

    @Transactional
    public CouponIssueResponse issueCoupon(Long memberId, CouponIssueRequest request) {
        Long couponPolicyId = request.couponPolicyId();

        CouponPolicy couponPolicy = couponPolicyRepository.getById(couponPolicyId);

        if (issuedCouponRepository
                .existsByMemberIdAndCouponPolicyId(memberId, couponPolicyId)) {
            throw new AlreadyIssuedCouponException(couponPolicyId);
        }


        // DB에서도 만약을 위해 memberId와 couponPolicyId를 유니크 제약조건으로 걸어두어 예외처리 함.
        try {
            LocalDateTime now = LocalDateTime.now();
            IssuedCoupon issuedCoupon = new IssuedCoupon(memberId, couponPolicy, now);
            issuedCouponRepository.save(issuedCoupon);

            return CouponIssueResponse.from(issuedCoupon);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyIssuedCouponException(couponPolicyId);
        }
    }

    // 여러 책에 대해 사용 가능한 쿠폰 조회(주문서 페이지에서)
    public BooksAvailableCouponsResponse getAvailableCouponsForBooks(
            Long memberId,
            BooksAvailableCouponsRequest request) {
        List<IssuedCoupon> allCoupons = issuedCouponRepository.finaAvailableCouponForBooks(memberId, request.books());

        Map<Long,List<Long>> bookCouponMap = new HashMap<>();

        List<BookCouponCheckRequest> books = request.books();
        for (BookCouponCheckRequest book : books) {
            List<Long> applicableCouponIds = new ArrayList<>();

            for (IssuedCoupon coupon : allCoupons) {
                if (isCouponApplicableToBook(coupon, book)) {
                    applicableCouponIds.add(coupon.getId());
                }
            }

            bookCouponMap.put(book.bookId(), applicableCouponIds);
        }

        List<IssuedCouponSimpleResponse> couponResponses = new ArrayList<>();
        for (IssuedCoupon coupon : allCoupons) {
            IssuedCouponSimpleResponse response = IssuedCouponSimpleResponse.from(coupon);
            couponResponses.add(response);
        }

        return new BooksAvailableCouponsResponse(couponResponses, bookCouponMap);
    }

    // 특정 쿠폰이 특정 책에 적용 가능한지 판단
    private boolean isCouponApplicableToBook(
            IssuedCoupon coupon,
            BookCouponCheckRequest book) {
        CouponPolicy policy = coupon.getCouponPolicy();

        if (book.amount() < policy.getMinAvailableAmount()) {
            return false;
        }

        CouponType type = policy.getType();

        if (type == CouponType.WELCOME || type == CouponType.BIRTHDAY) {
            return true;
        }

        CouponApplicableStrategy strategy = strategyFactory.getStrategy(type);

        if (strategy == null) {
            return false;
        }

        return strategy.isApplicable(policy, book);
    }

    public CalculateDiscountResponse calculateDiscount(
            Long memberId,
            Long issuedCouponId,
            CalculateDiscountRequest request
    ) {
        IssuedCoupon issuedCoupon = issuedCouponRepository
                .findById(issuedCouponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        if (issuedCoupon.getMemberId() != memberId) {
            throw new IllegalArgumentException("본인의 쿠폰만 사용할 수 있습니다.");
        }

        if (issuedCoupon.getUsedAt() != null) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        if (LocalDateTime.now().isAfter(issuedCoupon.getExpiredAt())) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }

        int discountAmount = issuedCoupon.calculateDiscountAmount(request.orderAmount());

        return new CalculateDiscountResponse(discountAmount);
    }

    @Transactional
    public void useCoupon(Long memberId, Long issuedCouponId) {
        IssuedCoupon issuedCoupon = issuedCouponRepository
                .findById(issuedCouponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        if (issuedCoupon.getUsedAt() != null) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        if (LocalDateTime.now().isAfter(issuedCoupon.getExpiredAt())) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }

        issuedCoupon.use();
    }
}
