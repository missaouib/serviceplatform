package co.yixiang.modules.yiyaobao;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.yixiang.constant.ShopConstants;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.logging.domain.Log;
import co.yixiang.logging.service.LogService;
import co.yixiang.modules.shop.domain.Project;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.domain.YxSystemStore;
import co.yixiang.modules.shop.service.ProjectService;
import co.yixiang.modules.shop.service.YxStoreOrderService;
import co.yixiang.modules.shop.service.YxSystemStoreService;
import co.yixiang.modules.yiyaobao.service.OrderServiceImpl;
import co.yixiang.mp.yiyaobao.enums.YiyaobaoPayMethodEnum;
import co.yixiang.mp.yiyaobao.enums.YiyaobaoPayTypeEnum;
import co.yixiang.mp.yiyaobao.service.dto.YiyaobaoOrderInfo;
import co.yixiang.utils.OrderUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import java.util.Date;

/**
 * @author zhouhang
 * @date 2021-04-29
 */

@Component

@Slf4j
public class Consumer4OrderDelay {

   @Autowired
   private OrderServiceImpl yiyaobaoOrderService;

   @Autowired
   private YxStoreOrderService yxStoreOrderService;

   @Autowired
   private YxSystemStoreService yxSystemStoreService;

   @Autowired
   private ProjectService projectService;

    @Autowired
    private LogService logService;


    @Value("${yiyaobao.delayQueueName}")
    private String queueName;

    @RabbitHandler
    @RabbitListener(queues = "${yiyaobao.delayQueueName}")
    public void consumer(Message message, Channel channel) throws IOException, InterruptedException {
        try {
            log.info("?????????????????????{}" , new String(message.getBody(), "UTF-8"));
            String body = new String(message.getBody(), "UTF-8");

            JSONObject jsonObject = JSONUtil.parseObj(body);
            String orderNo = jsonObject.getStr("orderNo");
            String projectCode = jsonObject.getStr("projectCode");

           /* if(ProjectNameEnum.ZHONGANMANBING.getValue().equals(projectCode)) {
                projectCode = ProjectNameEnum.ZHONGANPUYAO.getValue();
            }*/

            Project project = projectService.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode,projectCode));

            String projectName = project.getProjectName();
            if(ProjectNameEnum.MSH.getValue().equals(projectCode)) {

            } else {
                // ???msh??????
                // ???????????????????????????
                LambdaQueryWrapper<YxStoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(YxStoreOrder::getOrderId,orderNo);
                lambdaQueryWrapper.select(YxStoreOrder::getStoreId,YxStoreOrder::getPaid, YxStoreOrder::getPayTime);
                YxStoreOrder yxStoreOrder = yxStoreOrderService.getOne(lambdaQueryWrapper,false);

                Integer storeId = yxStoreOrder.getStoreId();
                YxSystemStore yxSystemStore = yxSystemStoreService.getOne(new LambdaQueryWrapper<YxSystemStore>().eq(YxSystemStore::getId,storeId));
                if(ShopConstants.STORENAME_GUANGZHOU_CLOUD.equals(yxSystemStore.getName())) {
                    // ?????????
                    Boolean flag = yiyaobaoOrderService.sendOrder2YiyaobaoCloud(orderNo,projectName);
                    // ?????????????????? ?????????????????????id
                    String yiyaobaoOrderId = yiyaobaoOrderService.queryYiyaobaoOrderIdByPrescription(orderNo);
                    LambdaUpdateWrapper<YxStoreOrder> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.set(YxStoreOrder::getYiyaobaoOrderId,yiyaobaoOrderId);
                    updateWrapper.eq(YxStoreOrder::getOrderId,orderNo);
                    yxStoreOrderService.update(updateWrapper);


//                    String orderSource = yiyaobaoOrderService.queryOrderSourceCode(projectName);
//                    if(StrUtil.isBlank(orderSource)) {
//                        orderSource = "23";
//                    }
//                    YiyaobaoOrderInfo yiyaobaoOrderInfo = new YiyaobaoOrderInfo();
//                    yiyaobaoOrderInfo.setOrderSource(orderSource);
//                    yiyaobaoOrderInfo.setPrsNo(orderNo);
//                    if(new Integer(1).equals(yxStoreOrder.getPaid())) {
//                        yiyaobaoOrderInfo.setPayMethod(YiyaobaoPayMethodEnum.payMethod_13.getValue());  // ????????????
//                        yiyaobaoOrderInfo.setPayResult("10"); // ?????????
//                        Integer payTime = yxStoreOrder.getPayTime();
//                        if(payTime != null) {
//                            String payTimeStr = OrderUtil.stampToDate(String.valueOf(payTime));
//                            yiyaobaoOrderInfo.setPayTime(payTimeStr); // ????????????
//                        } else {
//                            yiyaobaoOrderInfo.setPayTime(DateUtil.formatDateTime(new Date()));
//                        }
//                        yiyaobaoOrderInfo.setPayType(YiyaobaoPayTypeEnum.payType_40.getValue());
//
//                    } else {
//                        yiyaobaoOrderInfo.setPayMethod("");
//                        yiyaobaoOrderInfo.setPayResult("");
//                        yiyaobaoOrderInfo.setPayTime(null);
//                        yiyaobaoOrderInfo.setPayType("");
//                    }

                    // ??????????????????????????????
//                    yiyaobaoOrderService.updateYiyaobaoOrderSourceByPrescripNo(orderNo,orderSource);
                  //  yiyaobaoOrderService.updateYiyaobaoOrderInoByPrescripNo(yiyaobaoOrderInfo);

                } else {
                    // ????????????
                    yiyaobaoOrderService.sendOrder2YiyaobaoStore(orderNo);

                    // ?????????????????? ?????????????????????id
                    String yiyaobaoOrderId = yiyaobaoOrderService.queryYiyaobaoOrderIdByOrderNo(orderNo);
                    LambdaUpdateWrapper<YxStoreOrder> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.set(YxStoreOrder::getYiyaobaoOrderId,yiyaobaoOrderId);
                    updateWrapper.eq(YxStoreOrder::getOrderId,orderNo);
                    yxStoreOrderService.update(updateWrapper);

                    String orderSource = yiyaobaoOrderService.queryOrderSourceCode(projectName);
                    if(StrUtil.isBlank(orderSource)) {
                        orderSource = "23";
                    }
                    YiyaobaoOrderInfo yiyaobaoOrderInfo = new YiyaobaoOrderInfo();
                    yiyaobaoOrderInfo.setOrderSource(orderSource);
                    yiyaobaoOrderInfo.setOrderNo(orderNo);
                    if(new Integer(1).equals(yxStoreOrder.getPaid())) {
                        yiyaobaoOrderInfo.setPayMethod(YiyaobaoPayMethodEnum.payMethod_13.getValue());  // ????????????
                        yiyaobaoOrderInfo.setPayResult("10"); // ?????????
                        Integer payTime = yxStoreOrder.getPayTime();
                        if(payTime != null) {
                            String payTimeStr = OrderUtil.stampToDate(String.valueOf(payTime));
                            yiyaobaoOrderInfo.setPayTime(payTimeStr); // ????????????
                        } else {
                            yiyaobaoOrderInfo.setPayTime(DateUtil.formatDateTime(new Date()));
                        }
                        yiyaobaoOrderInfo.setPayType(YiyaobaoPayTypeEnum.payType_40.getValue());

                    } else {
                        yiyaobaoOrderInfo.setPayMethod("");
                        yiyaobaoOrderInfo.setPayResult("");
                        yiyaobaoOrderInfo.setPayTime(null);
                        yiyaobaoOrderInfo.setPayType("");
                    }
                  //  yiyaobaoOrderService.updateYiyaobaoOrderSourceByOrderNo(orderNo,orderSource);
                 //   yiyaobaoOrderService.updateYiyaobaoOrderInfoByOrderNo(yiyaobaoOrderInfo);
                }

            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }catch (SocketTimeoutException ex){
            ex.printStackTrace();
            //??????????????????????????????
            Thread.sleep(2000);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }  catch (Exception ex) {
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
