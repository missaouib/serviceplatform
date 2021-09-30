/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.rabbit.rest;

import co.yixiang.dozer.service.IGenerator;
import co.yixiang.exception.BadRequestException;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.model.User;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.service.YxStoreOrderService;
import co.yixiang.modules.system.domain.Dict;
import co.yixiang.modules.system.rest.SysUserController;
import co.yixiang.modules.system.service.DictService;
import co.yixiang.modules.system.service.dto.DictDto;
import co.yixiang.modules.system.service.dto.DictQueryCriteria;
import co.yixiang.rabbitmq.send.*;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

/**
* @author zhoujinlai
* @date 2021-04-29
*/
@Slf4j
@Api(tags = "test：rabbit")
@RestController
@RequestMapping("/api/rabbit")
public class RabbitController {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private YxStoreOrderService orderService;


    @GetMapping(value = "/testMessageSend")
    public void testMessageSend() throws InterruptedException {
        messageSender.send();
        Thread.sleep(1000);
    }

    @Autowired
    private OneToManySender oneToManySender;

    @GetMapping(value = "/oneToMany")
    public void oneToMany() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            oneToManySender.send(i);
        }
        Thread.sleep(10000L);
    }

    @Autowired
    private ManyToMany manyToMany;

    @GetMapping(value = "/manyToMany")
    public void manyToMany() throws InterruptedException {

        for (int i = 0; i < 100; i++) {
            oneToManySender.send(i);
            manyToMany.send(i);
        }
        Thread.sleep(10000L);
    }


    @Autowired
    private ObjectSender objectSend;

    @GetMapping(value = "/sendObject")
    public void sendObject() throws InterruptedException {
        User user = new User();
        user.setName("王智");
        user.setPass("123456");
        objectSend.send(user);
        Thread.sleep(1000L);
    }

    @Autowired
    private TopicSender topicSender;

    @GetMapping(value = "/topic1")
    public void topic1() throws Exception {
        topicSender.send1();
        Thread.sleep(1000L);
    }

    @GetMapping(value = "/topic2")
    public void topic2() throws Exception {
        topicSender.send2();
        Thread.sleep(1000L);
    }

    @Autowired
    private FanoutSender fanoutSender;

    @GetMapping(value = "/fanoutSender")
    public void fanoutSender() throws Exception {
        fanoutSender.send();
        Thread.sleep(1000L);
    }

    @Autowired
    private MqProducer mqProducer;

    @GetMapping(value = "/toMshRabbit")
    public void toMshRabbit(@Validated @RequestBody JSONObject jsonObject) throws Exception {
        mqProducer.sendDelayQueue(jsonObject.get("mshTopic").toString(),jsonObject.get("demandId").toString(),2000);
    }

}
