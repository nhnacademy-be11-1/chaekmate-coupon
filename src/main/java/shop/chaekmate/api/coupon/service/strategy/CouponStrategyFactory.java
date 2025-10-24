package shop.chaekmate.api.coupon.service.strategy;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.api.coupon.entity.type.CouponType;
import shop.chaekmate.api.coupon.exception.InvalidCouponType;

@Component
@RequiredArgsConstructor
public class CouponStrategyFactory {
    private final List<CouponTypeStrategy> strategies;

    public CouponTypeStrategy getStrategy(CouponType couponType) {
        return strategies.stream()
                .filter(s -> s.supports(couponType))
                .findFirst()
                .orElseThrow(() -> new InvalidCouponType(couponType));
    }
}
