package shop.chaekmate.api.coupon.service.strategy.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.api.client.CoreApiClient;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyCreateRequest;
import shop.chaekmate.api.coupon.dto.request.CouponPolicyUpdateRequest;
import shop.chaekmate.api.coupon.dto.response.BooksGetResponse;
import shop.chaekmate.api.coupon.dto.response.CouponPolicyGetResponse;
import shop.chaekmate.api.coupon.entity.CouponAppliedBook;
import shop.chaekmate.api.coupon.entity.CouponPolicy;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.repository.CouponAppliedBookRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookCouponStrategy implements CouponTypeStrategy {
    private final CouponAppliedBookRepository couponAppliedBookRepository;

    private final CoreApiClient coreApiClient;

    @Override
    public boolean supports(CouponType couponType) {
        return CouponType.BOOK == couponType;
    }

    @Override
    public void create(CouponPolicy couponPolicy, CouponPolicyCreateRequest request) {
        List<CouponAppliedBook> books = request.ids().stream()
                .map(id -> new CouponAppliedBook(couponPolicy, id))
                .toList();

        couponAppliedBookRepository.saveAllInBatch(books);
    }

    @Override
    public void update(CouponPolicy couponPolicy, CouponPolicyUpdateRequest request) {
        couponAppliedBookRepository.softDeleteAllByCouponPolicy(couponPolicy);

        List<CouponAppliedBook> books = request.ids().stream()
                .map(id -> new CouponAppliedBook(couponPolicy, id))
                .toList();

        couponAppliedBookRepository.saveAllInBatch(books);
    }

    @Override
    public void delete(CouponPolicy couponPolicy) {
        couponAppliedBookRepository.softDeleteAllByCouponPolicy(couponPolicy);
    }

    @Override
    public CouponPolicyGetResponse get(CouponPolicy couponPolicy) {
        List<Long> ids = couponAppliedBookRepository.findAllByCouponPolicyId(couponPolicy.getId())
                .stream()
                .map(CouponAppliedBook::getBookId)
                .toList();

        List<BooksGetResponse> responses = coreApiClient.getBooksIds(ids).data();

        return CouponPolicyGetResponse.ofBook(couponPolicy, responses);
    }
}
