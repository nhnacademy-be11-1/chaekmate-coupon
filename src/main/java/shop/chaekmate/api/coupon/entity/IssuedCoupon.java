package shop.chaekmate.api.coupon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.api.common.entity.BaseEntity;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Table(name = "issued_coupon")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE issued_coupon SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class IssuedCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long memberId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "coupon_policy_id", nullable = false)
    private CouponPolicy couponPolicy;

    private LocalDateTime usedAt;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public IssuedCoupon(Long memberId, CouponPolicy couponPolicy, LocalDateTime issuedAt, LocalDateTime expiredAt) {
        this.memberId = memberId;
        this.couponPolicy = couponPolicy;
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
    }

    public void use() {
        this.usedAt = LocalDateTime.now();
    }
}
