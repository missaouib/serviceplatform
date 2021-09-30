/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.modules.shop.rest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.constant.ShopConstants;
import co.yixiang.dozer.service.IGenerator;
import co.yixiang.enums.OrderChangeTypeEnum;
import co.yixiang.enums.OrderInfoEnum;
import co.yixiang.enums.OrderStatusEnum;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.exception.BadRequestException;
import co.yixiang.logging.aop.log.Log;
import co.yixiang.modules.activity.service.YxStorePinkService;
import co.yixiang.modules.shop.domain.OrderUserInfo;
import co.yixiang.modules.shop.domain.YxStoreOrder;
import co.yixiang.modules.shop.domain.YxStoreOrderStatus;
import co.yixiang.modules.shop.domain.YxWechatUser;
import co.yixiang.modules.shop.service.YxExpressService;
import co.yixiang.modules.shop.service.YxStoreOrderService;
import co.yixiang.modules.shop.service.YxStoreOrderStatusService;
import co.yixiang.modules.shop.service.YxWechatUserService;
import co.yixiang.modules.shop.service.dto.*;
import co.yixiang.modules.shop.service.param.ExpressParam;
import co.yixiang.modules.yiyaobao.service.OrderServiceImpl;
import co.yixiang.modules.zhengdatianqing.service.impl.ZhengDaTianQingServiceImpl;
import co.yixiang.mp.service.YxTemplateService;
import co.yixiang.mp.service.dto.OrderTemplateMessage;
import co.yixiang.mp.yiyaobao.vo.OrderVo;
import co.yixiang.rabbitmq.send.MqProducer;
import co.yixiang.tools.express.ExpressService;
import co.yixiang.tools.express.dao.ExpressInfo;
import co.yixiang.utils.OrderUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author hupeng
 * @date 2019-10-14
 */
@Api(tags = "商城:订单管理")
@RestController
@RequestMapping("api")
@Slf4j
public class StoreOrderController {

    @Value("${yshop.apiUrl}")
    private String apiUrl;

    private final IGenerator generator;
    private final YxStoreOrderService yxStoreOrderService;
    private final YxStoreOrderStatusService yxStoreOrderStatusService;
    private final YxExpressService yxExpressService;
    private final YxWechatUserService wechatUserService;
    private final RedisTemplate<String, String> redisTemplate;
    private final YxTemplateService templateService;
    private final ExpressService expressService;

    @Autowired
    private ZhengDaTianQingServiceImpl zhengDaTianQingService;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private MqProducer mqProducer;

    @Value("${yiyaobao.delayQueueName}")
    private String bizRoutekeyYiyaobao;



    public StoreOrderController(IGenerator generator, YxStoreOrderService yxStoreOrderService, YxStoreOrderStatusService yxStoreOrderStatusService,
                                YxExpressService yxExpressService, YxWechatUserService wechatUserService,
                                RedisTemplate<String, String> redisTemplate,
                                YxTemplateService templateService, YxStorePinkService storePinkService,
                                ExpressService expressService) {
        this.generator = generator;
        this.yxStoreOrderService = yxStoreOrderService;
        this.yxStoreOrderStatusService = yxStoreOrderStatusService;
        this.yxExpressService = yxExpressService;
        this.wechatUserService = wechatUserService;
        this.redisTemplate = redisTemplate;
        this.templateService = templateService;
        this.expressService = expressService;
    }

    /**@Valid
     * 根据商品分类统计订单占比
     */
    @GetMapping("/yxStoreOrder/orderCount")
    @ApiOperation(value = "根据商品分类统计订单占比",notes = "根据商品分类统计订单占比",response = ExpressParam.class)
    public ResponseEntity orderCount(){
        OrderCountDto orderCountDto  = yxStoreOrderService.getOrderCount();
        return new ResponseEntity(orderCountDto, HttpStatus.OK);
    }

    @GetMapping(value = "/data/count")
    @AnonymousAccess
    public ResponseEntity getCount() {
        return new ResponseEntity(yxStoreOrderService.getOrderTimeData(), HttpStatus.OK);
    }

    @GetMapping(value = "/data/chart")
    @AnonymousAccess
    public ResponseEntity getChart() {
        return new ResponseEntity(yxStoreOrderService.chartCount(), HttpStatus.OK);
    }


    @ApiOperation(value = "查询订单")
    @GetMapping(value = "/yxStoreOrder")
    @PreAuthorize("@el.check('admin','YXSTOREORDER_ALL','YXSTOREORDER_SELECT')")
    public ResponseEntity getYxStoreOrders(YxStoreOrderQueryCriteria criteria,
                                           Pageable pageable,
                                           @RequestParam(name = "orderStatus") String orderStatus,
                                           @RequestParam(name = "orderType") String orderType) {


        criteria.setShippingType(1);//默认查询所有快递订单
        //订单状态查询
        if (StrUtil.isNotEmpty(orderStatus)) {
            switch (orderStatus) {
                case "0":  // 未支付
                    criteria.setIsDel(0);
                    criteria.setPaid(0);
                   // criteria.setStatus(Arrays.asList(0));
                    criteria.setRefundStatus(0);
                    break;
                case "1":  // 未发货
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(0));
                    criteria.setRefundStatus(0);
                    break;
                case "2":  // 待收货
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(1));
                    criteria.setRefundStatus(0);
                    break;
                case "3": // 待评价
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(2));
                    criteria.setRefundStatus(0);
                    break;
                case "4":  // 交易完成
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(3));
                    criteria.setRefundStatus(0);
                    break;
                case "5":  // 待审核
                    criteria.setIsDel(0);
                    //criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(5));
                    criteria.setRefundStatus(0);
                    break;
                case "-1":  // 申请退款/退款中
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setRefundStatus(1);
                    break;
                case "-2": // 已退款
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setRefundStatus(2);
                    break;
                case "-4":
                    criteria.setStatus(Arrays.asList(6,7,8));
                    break;
            }
        }
        //订单类型查询
        if (StrUtil.isNotEmpty(orderType)) {
            switch (orderType) {
                case "1":
                    criteria.setBargainId(0);
                    criteria.setCombinationId(0);
                    criteria.setSeckillId(0);
                    break;
                case "2":
                    criteria.setNewCombinationId(0);
                    break;
                case "3":  // 秒杀
                    criteria.setNewSeckillId(0);
                    break;
                case "4":
                    criteria.setNewBargainId(0);
                    break;
                case "5":
                    criteria.setShippingType(2);
                    break;
            }
        }

        // 如果是罗氏订单，则同步订单状态
/*        if(ProjectNameEnum.ROCHE_SMA.getValue().equals(criteria.getProjectCode())) {
            yxStoreOrderService.syncRocheOrderStatus();
        }*/
        if("yiyao".equals(criteria.getProjectCode())) {
            criteria.setProjectCode("");
        }

        return new ResponseEntity(yxStoreOrderService.queryAll(criteria, pageable), HttpStatus.OK);
    }


    @ApiOperation(value = "发货")
    @PutMapping(value = "/yxStoreOrder")
    @PreAuthorize("@el.check('admin','YXSTOREORDER_ALL','YXSTOREORDER_EDIT')")
    public ResponseEntity update(@Validated @RequestBody YxStoreOrder resources) {
        if (StrUtil.isBlank(resources.getDeliveryName())) throw new BadRequestException("请选择快递公司");
        if (StrUtil.isBlank(resources.getDeliveryId())) throw new BadRequestException("快递单号不能为空");
        YxExpressDto expressDTO = generator.convert(yxExpressService.getById(Integer.valueOf(resources
                .getDeliveryName())),YxExpressDto.class);
        if (ObjectUtil.isNull(expressDTO)) {
            throw new BadRequestException("请先添加快递公司");
        }
        resources.setStatus(1);
        resources.setDeliveryType("express");
        resources.setDeliveryName(expressDTO.getName());
        resources.setDeliverySn(expressDTO.getCode());

        yxStoreOrderService.update(resources);

        YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
        storeOrderStatus.setOid(resources.getId());
        storeOrderStatus.setChangeType("delivery_goods");
        storeOrderStatus.setChangeMessage("已发货 快递公司：" + resources.getDeliveryName()
                + " 快递单号：" + resources.getDeliveryId());
        storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());

        yxStoreOrderStatusService.save(storeOrderStatus);

        //模板消息通知
        try {
            YxWechatUserDto wechatUser = generator.convert(wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",resources.getUid())),YxWechatUserDto.class);
            if (ObjectUtil.isNotNull(wechatUser)) {
                //公众号与小程序打通统一公众号模板通知
                if (StrUtil.isNotBlank(wechatUser.getOpenid())) {
                    templateService.deliverySuccessNotice(resources.getOrderId(),
                            expressDTO.getName(),resources.getDeliveryId(),wechatUser.getOpenid());
                }
            }
        } catch (Exception e) {
            log.info("当前用户不是微信用户不能发送模板消息哦!");
        }

        //加入redis，7天后自动确认收货
        String redisKey = String.valueOf(StrUtil.format("{}{}",
                ShopConstants.REDIS_ORDER_OUTTIME_UNCONFIRM,resources.getId()));
        redisTemplate.opsForValue().set(redisKey, resources.getOrderId(),
                ShopConstants.ORDER_OUTTIME_UNCONFIRM, TimeUnit.DAYS);


        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "订单核销")
    @PutMapping(value = "/yxStoreOrder/check")
    @PreAuthorize("@el.check('admin','YXSTOREORDER_ALL','YXSTOREORDER_EDIT')")
    public ResponseEntity check(@Validated @RequestBody YxStoreOrder resources) {
        if (StrUtil.isBlank(resources.getVerifyCode())) throw new BadRequestException("核销码不能为空");
        YxStoreOrderDto storeOrderDTO = generator.convert(yxStoreOrderService.getById(resources.getId()),YxStoreOrderDto.class);
        if(!resources.getVerifyCode().equals(storeOrderDTO.getVerifyCode())){
            throw new BadRequestException("核销码不对");
        }
        if(OrderInfoEnum.PAY_STATUS_0.getValue().equals(storeOrderDTO.getPaid())){
            throw new BadRequestException("订单未支付");
        }

        /**
        if(storeOrderDTO.getStatus() > 0) throw new BadRequestException("订单已核销");

        if(storeOrderDTO.getCombinationId() > 0 && storeOrderDTO.getPinkId() > 0){
            YxStorePinkDTO storePinkDTO = storePinkService.findById(storeOrderDTO.getPinkId());
            if(!OrderInfoEnum.PINK_STATUS_2.getValue().equals(storePinkDTO.getStatus())){
                throw new BadRequestException("拼团订单暂未成功无法核销");
            }
        }
         **/

        //远程调用API
        RestTemplate rest = new RestTemplate();
        String url = StrUtil.format(apiUrl+"/order/admin/order_verific/{}", resources.getVerifyCode());
        String text = rest.getForObject(url, String.class);


        JSONObject jsonObject = JSON.parseObject(text);

        Integer status = jsonObject.getInteger("status");
        String msg = jsonObject.getString("msg");

        if(status != 200) throw new BadRequestException(msg);


        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @ApiOperation(value = "退款")
    @PostMapping(value = "/yxStoreOrder/refund")
    @PreAuthorize("@el.check('admin','YXSTOREORDER_ALL','YXSTOREORDER_EDIT','ZHONGSHANORDER_REFUND','FINANCE_REFUND','YXSTOREORDER_REFUND')")
    public ResponseEntity refund(@Validated @RequestBody YxStoreOrder resources) {
        yxStoreOrderService.refund(resources);

        //模板消息通知
        try {
            YxWechatUserDto wechatUser = generator.convert(wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",resources.getUid())),YxWechatUserDto.class);
            if (ObjectUtil.isNotNull(wechatUser)) {
                //公众号与小程序打通统一公众号模板通知
                if (StrUtil.isNotBlank(wechatUser.getOpenid())) {
                    templateService.refundSuccessNotice(resources.getOrderId(),
                            resources.getPayPrice().toString(),wechatUser.getOpenid(),
                            OrderUtil.stampToDate(resources.getAddTime().toString()));
                }
            }
        } catch (Exception e) {
            log.info("当前用户不是微信用户不能发送模板消息哦!");
        }


        // 小程序订阅通知
        try {
            YxWechatUserDto wechatUser = generator.convert(wechatUserService.getOne(new QueryWrapper<YxWechatUser>().eq("uid",resources.getUid())),YxWechatUserDto.class);
            if (ObjectUtil.isNotNull(wechatUser)) {
                //公众号与小程序打通统一公众号模板通知
                if (StrUtil.isNotBlank(wechatUser.getRoutineOpenid())) {

                    String page = "pages/wode/orderDetail?orderId="+resources.getOrderId();
                    OrderTemplateMessage message = new OrderTemplateMessage();
                    message.setOrderDate( OrderUtil.stampToDate(resources.getAddTime().toString()));
                    message.setOrderId(resources.getOrderId());
                    message.setOrderStatus("已退款");
                    message.setRemark("益药商城为您服务");

                    templateService.sendDYTemplateMessage(wechatUser.getRoutineOpenid(),page,message);
                }
            }
        } catch (Exception e) {
            log.info("（退款）发送小程序订阅通知失败!");
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @Log("删除")
    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/yxStoreOrder/{id}")
    @PreAuthorize("@el.check('admin','YXSTOREORDER_ALL','YXSTOREORDER_DELETE')")
    public ResponseEntity delete(@PathVariable Integer id) {
        //if(StrUtil.isNotEmpty("22")) throw new BadRequestException("演示环境禁止操作");
        yxStoreOrderService.removeById(id);
        return new ResponseEntity(HttpStatus.OK);
    }


    @Log("修改订单")
    @ApiOperation(value = "修改订单")
    @PostMapping(value = "/yxStoreOrder/edit")
    @PreAuthorize("hasAnyRole('admin','YXSTOREORDER_ALL','YXSTOREORDER_EDIT')")
    public ResponseEntity editOrder(@RequestBody YxStoreOrder resources) {
        if (ObjectUtil.isNull(resources.getPayPrice())) throw new BadRequestException("请输入支付金额");
        if (resources.getPayPrice().doubleValue() < 0) throw new BadRequestException("金额不能低于0");

        YxStoreOrderDto storeOrder = generator.convert(yxStoreOrderService.getById(resources.getId()),YxStoreOrderDto.class);
        //判断金额是否有变动,生成一个额外订单号去支付

        int res = NumberUtil.compare(storeOrder.getPayPrice().doubleValue(), resources.getPayPrice().doubleValue());
        if (res != 0) {
            String orderSn = IdUtil.getSnowflake(0, 0).nextIdStr();
            resources.setExtendOrderId(orderSn);
        }


        yxStoreOrderService.saveOrUpdate(resources);

        YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
        storeOrderStatus.setOid(resources.getId());
        storeOrderStatus.setChangeType("order_edit");
        storeOrderStatus.setChangeMessage("修改订单价格为：" + resources.getPayPrice());
        storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());

        yxStoreOrderStatusService.save(storeOrderStatus);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("修改订单")
    @ApiOperation(value = "修改订单")
    @PostMapping(value = "/yxStoreOrder/udpateNotNull")
    @PreAuthorize("hasAnyRole('admin','YXSTOREORDER_ALL','YXSTOREORDER_EDIT')")
    public ResponseEntity udpateNotNull(@RequestBody YxStoreOrder resources) {
        //修改订单状态、物流单号  服务药师  服务药店  服务联系电话  模板消息提示说明

            Boolean falg=false;
            if((resources.getStatus()!=null && resources.getStatus() != 11  && !(resources.getPaid()!=null && resources.getPaid() ==1 && resources.getStatus()== 0)) || (resources.getRefundStatus()!=null && resources.getRefundStatus() == 2)){
                falg=true;
            }
            if(resources.getRefundStatus()!=null && resources.getRefundStatus()==2){
                YxStoreOrder storeOrder =  yxStoreOrderService.getOne(new QueryWrapper<YxStoreOrder>().eq("id",resources.getId()).select("pay_price"));
//                storeOrder.setReturnType(resources.getReturnType());
//                if( ! OrderStatusEnum.STATUS_6.getValue().equals(storeOrder.getStatus()) ) {
//                    yxStoreOrderService.yiyaobaoCancelOrder(storeOrder);
//                }
                resources.setRefundPrice(storeOrder.getPayPrice());
                resources.setRefundFactTime(new Date());
            }




        try {
            yxStoreOrderService.saveOrUpdate(resources);
            if(falg){
                yxStoreOrderService.updateStatusSendTemplateMessage(resources);
            }
        }catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> map = new LinkedHashMap<>(2);
            map.put("status", HttpStatus.BAD_REQUEST.value());
            map.put("message", "更新失败！");
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }


        if(resources.getRefundStatus() != null && resources.getRefundStatus()==2) {
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.REFUND_PRICE.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.REFUND_PRICE.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(resources.getStatus()!= null && resources.getStatus() == 3) {  // 已完成
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.ClOSE_ORDER.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.ClOSE_ORDER.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(resources.getStatus()!= null &&resources.getStatus() == 11) {  // 付款待确认
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.TO_BE_CONFIRMED_PAY.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.TO_BE_CONFIRMED_PAY.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(resources.getStatus()!= null && resources.getPaid() != null && resources.getStatus() == 0 && resources.getPaid() == 1) {  //已付款
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.PAID.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.PAID.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }else if(resources.getStatus()!= null && resources.getPaid() != null && resources.getStatus() == 1 && resources.getPaid() == 1) {  //已发货
            YxStoreOrderStatus storeOrderStatus = new YxStoreOrderStatus();
            storeOrderStatus.setOid(resources.getId());
            storeOrderStatus.setChangeType(OrderChangeTypeEnum.DELIVERY_GOODS.getValue());
            storeOrderStatus.setChangeMessage(OrderChangeTypeEnum.DELIVERY_GOODS.getDesc());
            storeOrderStatus.setChangeTime(OrderUtil.getSecondTimestampTwo());
            yxStoreOrderStatusService.save(storeOrderStatus);
        }


        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("status", HttpStatus.OK.value());
        map.put("message", "更新成功！");
        return new ResponseEntity(map,HttpStatus.OK);
    }

    @Log("发送模板消息")
    @ApiOperation(value = "发送模板消息")
    @PostMapping(value = "/yxStoreOrder/sendTemplateMessage")
    @PreAuthorize("hasAnyRole('admin','YXSTOREORDER_ALL','YXSTOREORDER_EDIT')")
    public ResponseEntity sendTemplateMessage(@RequestBody YxStoreOrder resources) {
        yxStoreOrderService.sendTemplateMessage(resources);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("修改订单备注")
    @ApiOperation(value = "修改订单备注")
    @PostMapping(value = "/yxStoreOrder/remark")
    @PreAuthorize("hasAnyRole('admin','YXSTOREORDER_ALL','YXSTOREORDER_EDIT')")
    public ResponseEntity editOrderRemark(@RequestBody YxStoreOrder resources) {
        if (StrUtil.isBlank(resources.getRemark())) throw new BadRequestException("请输入备注");
        yxStoreOrderService.saveOrUpdate(resources);
        return new ResponseEntity(HttpStatus.OK);
    }


    /**@Valid
     * 获取物流信息,根据传的订单编号 ShipperCode快递公司编号 和物流单号,
     */
    @PostMapping("/yxStoreOrder/express")
    @ApiOperation(value = "获取物流信息",notes = "获取物流信息",response = ExpressParam.class)
    public ResponseEntity express( @RequestBody ExpressParam expressInfoDo){
        /*ExpressInfo expressInfo = expressService.getExpressInfo(expressInfoDo.getOrderCode(),
                expressInfoDo.getShipperCode(), expressInfoDo.getLogisticCode());*/
        YxStoreOrder yxStoreOrder = yxStoreOrderService.getOne(new QueryWrapper<YxStoreOrder>().eq("order_id",expressInfoDo.getOrderCode()));
        if(yxStoreOrder == null) {
            yxStoreOrder = yxStoreOrderService.getOne(new QueryWrapper<YxStoreOrder>().eq("id",expressInfoDo.getOrderCode()));
        }

        String yiyaobaoOrderId = yxStoreOrder.getYiyaobaoOrderId();

        if(StrUtil.isBlank(yiyaobaoOrderId) ) {
            OrderVo orderVo = orderService.getYiyaobaoOrderbyOrderIdSample(yxStoreOrder.getOrderId());
            if (orderVo!=null) {
                yiyaobaoOrderId = orderVo.getId();
            }else{
                return new ResponseEntity(new ExpressInfo(), HttpStatus.OK);
            }
        }

        expressInfoDo.setYiyaobaoOrderId(yiyaobaoOrderId);
        ExpressInfo expressInfo = orderService.queryOrderLogisticsProcess(expressInfoDo);

        if(!expressInfo.isSuccess()) throw new BadRequestException(expressInfo.getReason());
        return new ResponseEntity(expressInfo, HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/yxStoreOrder/download")
    @PreAuthorize("@el.check('admin','cate:list')")
    public void download(HttpServletResponse response,
                         YxStoreOrderQueryCriteria criteria,
                         Pageable pageable,
                         @RequestParam(name = "orderStatus") String orderStatus,
                         @RequestParam(name = "orderType") String orderType,
                         @RequestParam(name = "listContent") String listContent) throws IOException, ParseException {
        List<YxStoreOrderDto> list;
        if(StringUtils.isEmpty(listContent)){
            list =  (List)getYxStoreList(criteria, pageable, orderStatus, orderType).get("content");
        }else {
            List<String> idList = JSONArray.parseArray(listContent).toJavaList(String.class);
            list = (List)yxStoreOrderService.queryAll(idList).get("content");
        }
        yxStoreOrderService.download(list, response);
    }

    public Map<String,Object> getYxStoreList(YxStoreOrderQueryCriteria criteria,
                                             Pageable pageable,
                                             String orderStatus,
                                             String orderType){
        criteria.setShippingType(1);//默认查询所有快递订单
        //订单状态查询
        if (StrUtil.isNotEmpty(orderStatus)) {
            switch (orderStatus) {
                case "0":  // 未支付
                    criteria.setIsDel(0);
                    criteria.setPaid(0);
                    criteria.setStatus(Arrays.asList(0));
                    criteria.setRefundStatus(0);
                    break;
                case "1":  // 未发货
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(0));
                    criteria.setRefundStatus(0);
                    break;
                case "2":  // 待收货
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(1));
                    criteria.setRefundStatus(0);
                    break;
                case "3": // 待评价
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(2));
                    criteria.setRefundStatus(0);
                    break;
                case "4":  // 交易完成
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(3));
                    criteria.setRefundStatus(0);
                    break;
                case "-1":  // 申请退款/退款中
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setRefundStatus(1);
                    break;
                case "-2": // 已退款
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setRefundStatus(2);
                    break;
                case "-4":
                    criteria.setStatus(Arrays.asList(7,8));
                    break;
            }
        }
        //订单类型查询
        if (StrUtil.isNotEmpty(orderType)) {
            switch (orderType) {
                case "1":
                    criteria.setBargainId(0);
                    criteria.setCombinationId(0);
                    criteria.setSeckillId(0);
                    break;
                case "2":
                    criteria.setNewCombinationId(0);
                    break;
                case "3":
                    criteria.setNewSeckillId(0);
                    break;
                case "4":
                    criteria.setNewBargainId(0);
                    break;
                case "5":
                    criteria.setShippingType(2);
                    break;
            }
        }
        return yxStoreOrderService.queryAll(criteria, pageable);
    }

    @Log("追回订单回执")
    @ApiOperation(value = "追回订单回执")
    @PostMapping(value = "/yxStoreOrder/recover")
    public ResponseEntity orderRecover(@RequestBody YxStoreOrder resources) {
        if (StrUtil.isBlank(resources.getOrderId())) throw new BadRequestException("请输入订单号");
        zhengDaTianQingService.orderRecover(resources.getOrderId());

        return new ResponseEntity(HttpStatus.OK);
    }


    @Log("订单退货")
    @ApiOperation(value = "订单退货")
    @PostMapping(value = "/yxStoreOrder/return")
    public ResponseEntity orderReturn(@RequestBody YxStoreOrder resources) {
        if (StrUtil.isBlank(resources.getOrderId())) throw new BadRequestException("请输入订单号");
        Boolean flag = zhengDaTianQingService.orderReturn(resources.getOrderId(), DateUtil.now());
        if(flag) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }

    @Log("第三方商城下单")
    @ApiOperation(value = "第三方商城下单")
    @PostMapping(value = "/yxStoreOrder/create")
    public ResponseEntity create(@RequestBody YxStoreOrder4PCDto resources) {

        YxStoreOrder order = yxStoreOrderService.createOrder(resources);
        if(order != null) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }


    @ApiOperation(value = "查询PC订单")
    @GetMapping(value = "/yxStoreOrder/pc")
    public ResponseEntity getYxStoreOrders4PC(YxStoreOrderQueryCriteria criteria,
                                           Pageable pageable,
                                           @RequestParam(name = "orderStatus",required = false) String orderStatus,
                                           @RequestParam(name = "orderType",required = false) String orderType) {


        criteria.setShippingType(1);//默认查询所有快递订单
        //订单状态查询
        if (StrUtil.isNotEmpty(orderStatus)) {
            switch (orderStatus) {
                case "0":  // 未支付
                    criteria.setIsDel(0);
                    criteria.setPaid(0);
                    criteria.setStatus(Arrays.asList(0));
                    criteria.setRefundStatus(0);
                    break;
                case "1":  // 未发货
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(0));
                    criteria.setRefundStatus(0);
                    break;
                case "2":  // 待收货
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(1));
                    criteria.setRefundStatus(0);
                    break;
                case "3": // 待评价
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(2));
                    criteria.setRefundStatus(0);
                    break;
                case "4":  // 交易完成
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setStatus(Arrays.asList(3));
                    criteria.setRefundStatus(0);
                    break;
                case "-1":  // 申请退款/退款中
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setRefundStatus(1);
                    break;
                case "-2": // 已退款
                    criteria.setIsDel(0);
                    criteria.setPaid(1);
                    criteria.setRefundStatus(2);
                    break;
                case "-4":
                    criteria.setStatus(Arrays.asList(7,8));
                    break;
            }
        }
        //订单类型查询
        if (StrUtil.isNotEmpty(orderType)) {
            switch (orderType) {
                case "1":
                    criteria.setBargainId(0);
                    criteria.setCombinationId(0);
                    criteria.setSeckillId(0);
                    break;
                case "2":
                    criteria.setNewCombinationId(0);
                    break;
                case "3":
                    criteria.setNewSeckillId(0);
                    break;
                case "4":
                    criteria.setNewBargainId(0);
                    break;
                case "5":
                    criteria.setShippingType(2);
                    break;
            }
        }


        return new ResponseEntity(yxStoreOrderService.queryAll4PC(criteria, pageable), HttpStatus.OK);
    }



    @ApiOperation(value = "查询PC订单统计")
    @GetMapping(value = "/yxStoreOrder/statistics")
    @AnonymousAccess
    public ResponseEntity getYxStoreOrdersStatistics() {

        return new ResponseEntity(yxStoreOrderService.getStatistics(), HttpStatus.OK);
    }

    @Log("处方审核")
    @ApiOperation(value = "处方审核")
    @PostMapping(value = "/yxStoreOrder/check")
    public ResponseEntity orderCheck(@RequestBody YxStoreOrder resources) {
        yxStoreOrderService.orderCheck(resources);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("更新订单中信息")
    @ApiOperation(value = "更新订单信息")
    @PostMapping(value = "/yxStoreOrder/userInfo")
    public ResponseEntity updateOrderUserInfo(@RequestBody OrderUserInfo resources) {
        yxStoreOrderService.updateOrderUserInfo(resources);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("下发订单到益药宝门店系统")
    @ApiOperation(value = "下发订单到益药宝门店系统")
    @PostMapping(value = "/yxStoreOrder/sendOrder2YiyaobaoStore")
    public ResponseEntity sendOrder2YiyaobaoStore(@RequestBody YxStoreOrder resources) {

        log.info("pc端下发订单到益药宝，项目编码：[{}], 订单编码：[{}]", resources.getProjectCode(), resources.getOrderId());
        cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("orderNo",resources.getOrderId());
        jsonObject.put("desc",  resources.getProjectCode() + "下发订单" );
        jsonObject.put("projectCode",resources.getProjectCode());
        jsonObject.put("time", DateUtil.now());
        mqProducer.sendDelayQueue(bizRoutekeyYiyaobao,jsonObject.toString(),2000);

      //  Boolean flag = orderService.sendOrder2YiyaobaoStore(resources.getOrderId());

        return new ResponseEntity(HttpStatus.OK);
    }



    @ApiOperation(value = "用户取消订单，待确认")
    @PostMapping(value = "/yxStoreOrder/cancelConfirm")
    public ResponseEntity cancelConfirm(@RequestBody String jsonStr) {
        yxStoreOrderService.cancelConfirm(jsonStr);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/yxStoreOrder/downloadByProjectCode")
    @PreAuthorize("@el.check('admin','cate:list')")
    public void downloadByProjectCode(HttpServletResponse response,
                         @RequestParam(name = "startTime") String startTime,
                         @RequestParam(name = "endTime") String endTime,
                         @RequestParam(name = "projectCode") String projectCode) throws IOException, ParseException {
       yxStoreOrderService.downloadByProjectCode(startTime,endTime,projectCode,response);
    }

    @ApiOperation(value = "获取订单详情")
    @GetMapping(value = "/yxStoreOrder/getDetalById")
    public ResponseEntity getDetalById(@RequestParam(name = "id") Integer id) {
        return   new ResponseEntity(yxStoreOrderService.getDetalById(id), HttpStatus.OK);
    }


    @Log("roche订单导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/yxStoreOrder/rocheDownload")
    public void rocheDownload(HttpServletResponse response,
                         YxStoreOrderQueryCriteria criteria,
                         Pageable pageable,
                         @RequestParam(name = "orderStatus") String orderStatus,
                         @RequestParam(name = "orderType") String orderType,
                         @RequestParam(name = "listContent") String listContent) throws IOException, ParseException {
        List<YxStoreOrderDto> list;
        if(StringUtils.isEmpty(listContent)){
            list =  (List)getYxStoreList(criteria, pageable, orderStatus, orderType).get("content");
        }else {
            List<String> idList = JSONArray.parseArray(listContent).toJavaList(String.class);
            list = (List)yxStoreOrderService.queryAll(idList).get("content");
        }

        List<RocheOrderDto> rocheOrderDtoList = yxStoreOrderService.convert2RocheOrder(list);

        yxStoreOrderService.download4RocheSma(rocheOrderDtoList, response);
    }

}
