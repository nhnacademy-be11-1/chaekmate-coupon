package shop.chaekmate.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.member")
public record MemberQueueProperties(
        String exchange,
        String couponQueue,
        String couponDlq
) {
}
