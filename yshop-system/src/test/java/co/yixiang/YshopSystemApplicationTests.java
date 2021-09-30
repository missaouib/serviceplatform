/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.mp.service.YxWechatUserInfoService;
import co.yixiang.rabbitmq.send.MqProducer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class YshopSystemApplicationTests {

    @Autowired
    private MqProducer mqProducer;

    // 业务队列绑定业务交换机的routeKey
    @Value("${ant.delayQueueName}")
    private String bizRoutekeyAnt;

    @Autowired
    private YxWechatUserInfoService yxWechatUserInfoService;

    @Test
    public void contextLoads() {

    }

    public static void main(String[] args) {
    }

    @Test
    public void test1(){


        cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("orderNo","111");
        jsonObject.put("status","222");
        jsonObject.put("desc","众安普药已退款订单");
        jsonObject.put("time", DateUtil.now());
        mqProducer.sendDelayQueue(bizRoutekeyAnt,jsonObject.toString(),2000);

    }
}

