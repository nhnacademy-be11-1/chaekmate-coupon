package shop.chaekmate.api.coupon.service.strategy.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.api.coupon.entity.type.CouponType;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponApplicableStrategyFactory {

    private final List<CouponApplicableStrategy> strategies;

    public CouponApplicableStrategy getStrategy(CouponType couponType) {
        return strategies.stream()
                .filter(s -> s.supports(couponType))
                .findFirst()
                .orElse(null);
    }
}
