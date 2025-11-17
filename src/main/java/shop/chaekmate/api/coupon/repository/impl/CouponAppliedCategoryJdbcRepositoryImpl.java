package shop.chaekmate.api.coupon.repository.impl;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.chaekmate.api.coupon.entity.CouponAppliedCategory;
import shop.chaekmate.api.coupon.repository.CouponAppliedCategoryJdbcRepository;

@RequiredArgsConstructor
@Repository
public class CouponAppliedCategoryJdbcRepositoryImpl implements CouponAppliedCategoryJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${jdbc-template-batch-size}")
    private int batchSize;

    @Override
    public void saveAllInBatch(Collection<CouponAppliedCategory> couponAppliedCategories) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO `coupon_applied_category` (coupon_policy_id, category_id, created_at, updated_at) VALUES (?, ?, ?, ?)",
                couponAppliedCategories,
                batchSize,
                (PreparedStatement ps, CouponAppliedCategory couponAppliedCategory) -> {
                    LocalDateTime now = LocalDateTime.now();
                    ps.setLong(1, couponAppliedCategory.getCouponPolicy().getId());
                    ps.setLong(2, couponAppliedCategory.getCategoryId());
                    ps.setTimestamp(3, Timestamp.valueOf(now));
                    ps.setTimestamp(4, Timestamp.valueOf(now));
                }
        );
    }
}
