package shop.chaekmate.api.coupon.service.strategy.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.api.coupon.dto.request.BookCouponCheckRequest;
import shop.chaekmate.api.coupon.entity.CouponAppliedBook;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.repository.CouponAppliedBookRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookCouponApplicableStrategy implements CouponApplicableStrategy {

    private final CouponAppliedBookRepository couponAppliedBookRepository;

    @Override
    public boolean supports(CouponType couponType) {
        return CouponType.BOOK == couponType;
    }

    @Override
    public boolean isApplicable(CouponPolicy policy, BookCouponCheckRequest book) {
        List<CouponAppliedBook> appliedBooks = couponAppliedBookRepository.findAllByCouponPolicyId(policy.getId());

        for (CouponAppliedBook appliedBook : appliedBooks) {
            if (appliedBook.getBookId() == book.bookId()) {
                return true;
            }
        }

        return false;
    }
}
