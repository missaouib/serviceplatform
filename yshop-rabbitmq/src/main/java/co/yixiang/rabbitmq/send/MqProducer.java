package co.yixiang.rabbitmq.send;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhouhang
 * @version 1.0
 * @date 2021/5/9 9:07
 */
@Component
public class MqProducer {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    // 业务交换机
    @Value("${spring.rabbitmq.bizExchangeName}")
    private String bizExchangeName;
    // 业务延迟交换机
    @Value("${spring.rabbitmq.bizDelayExchangeName}")
    private String bizDelayExchangeName;

    @Value("${spring.rabbitmq.deadExchangeName}")
    private String deadExchangeName;

    public void send(String queueName,String msg) {


        this.rabbitTemplate.convertSendAndReceive(queueName, msg.getBytes());


    }

    public void send2(String bizRouteKey,String msg){
        rabbitTemplate.convertAndSend(bizExchangeName,bizRouteKey,msg.getBytes());
    }

    public void sendToDeadQueue(String bizRouteKey,String msg){
        rabbitTemplate.convertAndSend(deadExchangeName,bizRouteKey,msg.getBytes());
    }

    public void sendDelayQueue(String bizRouteKey,String msg,Integer expiration){
        rabbitTemplate.convertAndSend(bizDelayExchangeName, bizRouteKey, msg.getBytes(), message -> {
            if (expiration != null && expiration > 0) {
                message.getMessageProperties().setHeader("x-delay", expiration);
            }
            return message;
        });
    }
}
