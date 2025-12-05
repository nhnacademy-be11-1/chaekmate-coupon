package shop.chaekmate.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MemberQueueProperties.class)
public class RabbitMqConfig {

    private final MemberQueueProperties memberQueueProperties;
    private final RabbitmqProperties rabbitmqProperties;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqProperties.host());
        connectionFactory.setPort(rabbitmqProperties.port());
        connectionFactory.setUsername(rabbitmqProperties.username());
        connectionFactory.setPassword(rabbitmqProperties.password());
        return connectionFactory;
    }

    @Bean
    public FanoutExchange memberFanoutExchange() {
        return new FanoutExchange(
                memberQueueProperties.exchange(),
                true,
                false
        );
    }

    @Bean
    public Queue memberCouponQueue() {
        return QueueBuilder.durable(memberQueueProperties.couponQueue())
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key",
                        memberQueueProperties.couponDlq())
                .build();
    }

    @Bean
    public Queue memberDlqQueue() {
        return new Queue(memberQueueProperties.couponDlq(), true);
    }

    @Bean
    public Binding memberCouponBinding() {
        return BindingBuilder
                .bind(memberCouponQueue())
                .to(memberFanoutExchange());
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
