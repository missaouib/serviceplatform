package co.yixiang;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.model.User;
import co.yixiang.mp.service.impl.PdfServiceImpl;
import co.yixiang.rabbitmq.send.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName RabbitMqHelloTest
 * @Author Smith
 * @Date 2019/1/25 13:54
 * @Description TODO
 * @Version 4.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class RabbitMqHelloTest {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private MqProducer mqProducer;

    // 延迟队列名称
    @Value("${spring.rabbitmq.delayQueueName}")
    private String delayQueueName;



    @Test
    public void testMessageSend() throws InterruptedException {
        messageSender.send();
        Thread.sleep(1000);
    }

    @Autowired
    private OneToManySender oneToManySender;

    @Autowired
    private PdfServiceImpl pdfService;

    @Test
    public void oneToMany() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            oneToManySender.send(i);
        }
        Thread.sleep(10000l);
    }

    @Autowired
    private ManyToMany manyToMany;

    @Test
    public void manyToMany() throws InterruptedException {

        for (int i = 0; i < 100; i++) {
            oneToManySender.send(i);
            manyToMany.send(i);
        }
        Thread.sleep(10000l);
    }


    @Autowired
    private ObjectSender objectSend;

    @Test
    public void sendObject() throws InterruptedException {
        User user = new User();
        user.setName("王智");
        user.setPass("123456");
        objectSend.send(user);
        Thread.sleep(1000l);
    }

    @Autowired
    private TopicSender topicSender;

    @Test
    public void topic1() throws Exception {
        topicSender.send1();
        Thread.sleep(1000l);
    }

    @Test
    public void topic2() throws Exception {
        topicSender.send2();
        Thread.sleep(1000l);
    }

    @Autowired
    private FanoutSender fanoutSender;

    @Test
    public void fanoutSender() throws Exception {
        fanoutSender.send();
        Thread.sleep(1000l);
    }


    @Test
    public void delayQueue(){

        cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("orderNo","12345678");
        jsonObject.put("status",5);
        jsonObject.put("desc","测试延迟订单");
        jsonObject.put("time", DateUtil.now());
log.info("发送延迟队列信息时间");
        mqProducer.sendDelayQueue(delayQueueName,jsonObject.toString(),5000);

    }


    @Test
    public void pdfService(){
        try{
            pdfService.generatePdf();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
