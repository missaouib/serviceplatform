package co.yixiang.modules.zhongan;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.logging.domain.Log;
import co.yixiang.logging.service.LogService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;

/**
 * @author zhouhang
 * @date 2021-04-29
 */

@Component

@Slf4j
public class Consumer4ZhonganpuyaoDelay {

   @Autowired
   private ZhongAnPuYaoServiceImpl zhongAnPuYaoService;

    @Autowired
    private LogService logService;


    @Value("${zhonganpuyao.delayQueueName}")
    private String queueName;

    @RabbitHandler
    @RabbitListener(queues = "${zhonganpuyao.delayQueueName}")
    public void consumer(Message message, Channel channel) throws IOException, InterruptedException {
        try {
            log.info("消费队列信息：{}" , new String(message.getBody(), "UTF-8"));
            String body = new String(message.getBody(), "UTF-8");

            JSONObject jsonObject = JSONUtil.parseObj(body);

            String orderNo = jsonObject.getStr("orderNo");
            zhongAnPuYaoService.sendOrderInfo(orderNo);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }catch (SocketTimeoutException ex){
            ex.printStackTrace();
            //出现异常手动放回队列
            Thread.sleep(2000);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log log = new Log();
            log.setMethod(getClass().getName() + "  " + queueName);
            log.setParams(new String(message.getBody(), "UTF-8"));
            log.setLogType("queue");
            log.setDescription(ex.getMessage());
            log.setCreateTime(new Timestamp(System.currentTimeMillis()));
            logService.save(log);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }

    }
}
