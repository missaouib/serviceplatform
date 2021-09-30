package co.yixiang.modules.msh;

import co.yixiang.logging.domain.Log;
import co.yixiang.logging.service.LogService;
import co.yixiang.modules.meideyi.MeideyiServiceImpl;
import co.yixiang.modules.msh.service.MshDemandListService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * @author zhoujinlai
 * @date 2021-06-04
 */

@Component
@Slf4j
public class ConsumerMsh {

   @Autowired
   private MshDemandListService mshDemandListService;

    @Autowired
    private LogService logService;

    @Value("${msh.queueName}")
    private String mshQueueName;

    @RabbitHandler
    @RabbitListener(queues = "${msh.queueName}")
    public void consumer(Message message, Channel channel) throws IOException, InterruptedException {
        try {
            log.info("msh消费队列信息：{}" , new String(message.getBody(), "UTF-8"));
            String body = new String(message.getBody(), "UTF-8");
            if(StringUtils.isNotEmpty(body)){
                mshDemandListService.sendDemandListInfo(body);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }catch (SocketTimeoutException ex){
            ex.printStackTrace();
            //出现异常手动放回队列
            Thread.sleep(2000);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log log = new Log();
            log.setMethod(getClass().getName() + "  " + mshQueueName);
            log.setParams(new String(message.getBody(), "UTF-8"));
            log.setLogType("queue");
            log.setDescription(ex.getMessage());
            log.setCreateTime(new Timestamp(System.currentTimeMillis()));
            logService.save(log);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }

    }
}
