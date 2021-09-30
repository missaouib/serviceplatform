package co.yixiang.rabbitmq.send;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @author zhoujinlai
 * @date 2021-04-29
 */

@Component
public class OneToManySender {

    @Autowired
    private AmqpTemplate rabbitmqTemplate;

    public void send(int i){
        String context = "oneToMany Spring Boot queue + " + " *********** " + i;
        System.out.println("Sender2 : " + context);
        this.rabbitmqTemplate.convertAndSend("oneToMany", context);
    }
}
