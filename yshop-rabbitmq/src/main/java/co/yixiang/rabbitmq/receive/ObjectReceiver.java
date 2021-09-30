package co.yixiang.rabbitmq.receive;

import co.yixiang.model.MessageModel;
import co.yixiang.model.User;
import co.yixiang.utils.JsonUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
/**
 * @author zhoujinlai
 * @date 2021-04-29
 */

@Component
public class ObjectReceiver {

    @RabbitHandler
    @RabbitListener(queues = "object")
    public void process(String message) {

        MessageModel messageModel = JsonUtil.getJsonToBean(message,MessageModel.class);

        System.out.println("Receiver object : " + messageModel);
    }

}
