package shop.chaekmate.api.coupon.repository.impl;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.chaekmate.api.coupon.entity.CouponAppliedBook;
import shop.chaekmate.api.coupon.repository.CouponAppliedBookJdbcRepository;

@RequiredArgsConstructor
@Repository
public class CouponAppliedBookJdbcRepositoryImpl implements CouponAppliedBookJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${jdbc-template-batch-size}")
    private int batchSize;

    @Override
    public void saveAllInBatch(Collection<CouponAppliedBook> couponAppliedBooks) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO `coupon_applied_book` (coupon_policy_id, book_id, created_at, updated_at) VALUES (?, ?, ?, ?)",
                couponAppliedBooks,
                batchSize,
                (PreparedStatement ps, CouponAppliedBook couponAppliedBook) -> {
                    LocalDateTime now = LocalDateTime.now();
                    ps.setLong(1, couponAppliedBook.getCouponPolicy().getId());
                    ps.setLong(2, couponAppliedBook.getBookId());
                    ps.setTimestamp(3, Timestamp.valueOf(now));
                    ps.setTimestamp(4, Timestamp.valueOf(now));
                }
        );
    }
}
