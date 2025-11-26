package shop.chaekmate.api.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableFeignClients(basePackages = "shop.chaekmate.api.client")
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
}
