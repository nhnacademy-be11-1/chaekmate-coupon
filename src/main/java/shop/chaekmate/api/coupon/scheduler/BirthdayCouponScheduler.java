package shop.chaekmate.api.coupon.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.chaekmate.api.coupon.service.BirthdayCouponService;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BirthdayCouponScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final BirthdayCouponService birthdayCouponService;

//    @Scheduled(cron = "0 0 0 1 * ?")
    @Scheduled(initialDelay = 5 * 60 * 1000)
    public void issueBirthdayCoupons() {
        String lockKey = String.format(
                "batch:birthday-coupon:%d-%-2d",
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonthValue());

        String lockValue = generateLockValue();

        Duration ttl = Duration.ofDays(1);
        log.info("생일 쿠폰 발급 시도: key={}, server={}", lockKey, lockKey);

        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, ttl);

        if (Boolean.FALSE.equals(acquired)) {
            return;
        }

        try {
            log.info("생일 쿠폰 발급 시작: {}", lockKey);

            birthdayCouponService.issueMonthlyBirthdayCoupons(
                    LocalDateTime.now().getMonthValue());

            log.info("생일 쿠폰 배치 완료: {}", lockValue);
        } catch (Exception e) {
            log.error("생일 쿠폰 배치 실패: key={}", lockKey, e);
        } finally {
            releaseLock(lockKey, lockValue);
        }
    }

    private String generateLockValue() {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            long timestamp = System.currentTimeMillis();

            return String.format("%s-%d", hostName, timestamp);
        } catch (Exception e) {
            return "coupon-server-" + System.currentTimeMillis();
        }
    }

    private void releaseLock(String lockKey, String lockValue) {
        try {
            String currentValue = redisTemplate.opsForValue().get(lockKey);

            if (lockValue.equals(currentValue)) {
                redisTemplate.delete(lockKey);
                log.info("락 해제 성공: key={}", lockKey);
            } else {
                log.warn("락 해제 실패 (다른 서버 소유): key={}", lockKey);
            }
        } catch (Exception e) {
            log.error("락 해제 중 에러:  key={}", lockKey, e);
        }
    }
}
