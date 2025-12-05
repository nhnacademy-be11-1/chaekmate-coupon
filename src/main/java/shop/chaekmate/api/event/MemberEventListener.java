package shop.chaekmate.api.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import shop.chaekmate.api.coupon.service.IssuedCouponService;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberEventListener {

    private final IssuedCouponService issuedCouponService;

    @RabbitListener(queues = "${rabbitmq.member.coupon-queue}")
    public void handleMemberSignedUp(MemberSignedUpEvent event) {
        log.info("회원가입 이벤트 수신: memberId={}", event.memberId());

        try {
            issuedCouponService.issueWelcomeCoupon(event.memberId());

            log.info("웰컴 쿠폰 발급 완료: memberId={}", event.memberId());
        } catch (Exception e) {
            log.error("웰컴 쿠폰 발급 실패: memberId={}",
                    event.memberId(), e);

            throw e;
        }
    }
}
