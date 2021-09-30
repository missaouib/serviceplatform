package co.yixiang.rabbitmq.receive;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
/**
 * @author zhoujinlai
 * @date 2021-04-29
 */

@Component
@RabbitListener(queues = "oneToMany")
public class OneToManyReceive2 {

    @RabbitHandler
    public void process(String context){
        System.out.println("oneToMany 2 : " + context);
    }

}
