package co.yixiang.rabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhoujinlai
 * @date 2021-04-29
 */

@Configuration
public class RabbitConfig {
    @Bean
    public Queue Hello() {
        return new Queue("hello");
    }

    @Bean
    public Queue OneToMany() {
        return new Queue("oneToMany");
    }


    @Bean
    public Queue ObjectQueue() {
        return new Queue("object");
    }
}
