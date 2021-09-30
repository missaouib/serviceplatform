package co.yixiang.modules.yaolian;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.logging.domain.Log;
import co.yixiang.logging.service.LogService;
import co.yixiang.modules.meideyi.MeideyiServiceImpl;
import co.yixiang.modules.yaolian.service.YaolianServiceImpl;
import co.yixiang.modules.yaolian.service.dto.YaolianOrderRefund;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author zhouhang
 * @date 2021-04-29
 */

@Component

@Slf4j
public class Consumer4Yaolian {

   @Autowired
   private YaolianServiceImpl yaolianService;
    @Autowired
    private LogService logService;


    @Value("${yaolian.queueName}")
    private String queueName;

    // @RabbitHandler
    //  @RabbitListener(queues = "${yaolian.queueName}")
    public void consumer(Message message, Channel channel) throws IOException, InterruptedException {
        try {
            log.info("消费队列信息：{}" , new String(message.getBody(), "UTF-8"));
            String body = new String(message.getBody(), "UTF-8");

            JSONObject jsonObject = JSONUtil.parseObj(body);
            String status = jsonObject.getStr("status");
            if(OrderStatusEnum.STATUS_6.getValue().toString().equals(status)) {  // 审核不通过
                String originalOrderNo = jsonObject.getStr("originalOrderNo");
                String checkFailReason = jsonObject.getStr("reason");
                YaolianOrderRefund yaolianOrderRefund = new YaolianOrderRefund();
                yaolianOrderRefund.setOrderNo(originalOrderNo);
                yaolianOrderRefund.setReason(checkFailReason);
                yaolianService.pushOrderRefundInfo(yaolianOrderRefund);
            } else if (OrderStatusEnum.STATUS_3.getValue().toString().equals(status)) { // 已完成
                String orderNo = jsonObject.getStr("orderNo");
                yaolianService.pushOrderInfo(orderNo);
            }


            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (SocketTimeoutException ex){
            ex.printStackTrace();
            //出现异常手动放回队列
            Thread.sleep(2000);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }catch (Exception ex) {
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
