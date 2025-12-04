package shop.chaekmate.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "spring.rabbitmq")
public record RabbitmqProperties(
        String host,
        int port,
        String username,
        String password
) {
}
