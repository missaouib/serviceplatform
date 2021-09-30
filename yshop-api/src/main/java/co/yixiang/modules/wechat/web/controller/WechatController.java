/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.wechat.web.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.api.ApiResult;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.enums.BillDetailEnum;
import co.yixiang.enums.OrderInfoEnum;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.enums.WechatNameEnum;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.mapper.YxStoreOrderMapper;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.order.web.vo.YxStoreOrderQueryVo;
import co.yixiang.modules.shop.service.YxSystemConfigService;
import co.yixiang.modules.user.entity.YxUserRecharge;
import co.yixiang.modules.user.service.YxUserRechargeService;

import co.yixiang.mp.config.WxMpConfiguration;
import co.yixiang.mp.config.WxPayConfiguration;
import co.yixiang.rabbitmq.send.MqProducer;
import co.yixiang.utils.JsonUtil;
import co.yixiang.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName WechatController
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/11/5
 **/
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "微信模块", tags = "微信:微信模块", description = "微信模块")
public class WechatController extends BaseController {

    private final YxStoreOrderService orderService;
    private final YxSystemConfigService systemConfigService;
    private final YxUserRechargeService userRechargeService;


    @Autowired
    private YxStoreOrderMapper yxStoreOrderMapper;


    @Autowired
    private MqProducer mqProducer;

    @Value("${meideyi.delayQueueName}")
    private String bizRoutekeyMeideyi;

    @Value("${zhonganpuyao.delayQueueName}")
    private String bizRoutekeyZhonganpuyao;
    /**
     * 微信分享配置
     */
    @AnonymousAccess
    @GetMapping("/share")
    @ApiOperation(value = "微信分享配置",notes = "微信分享配置")
    public ApiResult<Object> share() {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("img",systemConfigService.getData("wechat_share_img"));
        map.put("title",systemConfigService.getData("wechat_share_title"));
        map.put("synopsis",systemConfigService.getData("wechat_share_synopsis"));
        Map<String,Object> mapt = new LinkedHashMap<>();
        mapt.put("data",map);
        return ApiResult.ok(mapt);
    }

    /**
     * jssdk配置
     */
    @AnonymousAccess
    @GetMapping("/wechat/config")
    @ApiOperation(value = "jssdk配置",notes = "jssdk配置")
    public ApiResult<Object> jsConfig(@RequestParam(value = "url") String url,@RequestParam(value = "wechatName",required=false,defaultValue = "") String wechatName ) throws WxErrorException {
        String id = UUID.randomUUID().toString();
        log.info("/wechat/config 开始................wechatName={},url={}",wechatName,url);
        WxMpService wxService = WxMpConfiguration.getWxMpService(wechatName);
        WxJsapiSignature jsapiSignature = wxService.createJsapiSignature(url);
        log.info("jsapiSignature={}",jsapiSignature.toString());
        Map<String,Object> map = new LinkedHashMap<>();

        map.put("appId",jsapiSignature.getAppId());
        map.put("jsApiList",new String[]{"updateAppMessageShareData","openLocation","scanQRCode",
                "chooseWXPay","updateAppMessageShareData","updateTimelineShareData",
                "openAddress","editAddress","getLocation"});
        map.put("nonceStr",jsapiSignature.getNonceStr());
        map.put("signature",jsapiSignature.getSignature());
        map.put("timestamp",jsapiSignature.getTimestamp());
        map.put("url",jsapiSignature.getUrl());
        log.info("/wechat/config 结束................[{}],结果={}",id, JSONUtil.parseFromMap(map).toString());
        return ApiResult.ok(map);
    }



    /**
     * 微信支付/充值回调
     */
    @AnonymousAccess
    @PostMapping("/wechat/notify")
    @ApiOperation(value = "微信支付充值回调",notes = "微信支付充值回调")
    public String renotify(@RequestBody String xmlData) {
        try {
            WxPayService wxPayService = WxPayConfiguration.getPayService(WechatNameEnum.WECHAT.getValue());
            log.info("微信支付充值回调 xmlData：{}", xmlData);
            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);
            String outTradeNo = notifyResult.getOutTradeNo();
            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("pay_out_trade_no",outTradeNo);
            queryWrapper.select("order_id","paid","id");
            YxStoreOrder orderInfo = orderService.getOne(queryWrapper,false);

            log.info("微信支付充值回调 notifyResult.getAttach()：{}", notifyResult.getAttach());

            log.info("微信支付回调：{}", outTradeNo);
            String attach="";
            String mchId="";
            if(notifyResult.getAttach().contains("#")){
                attach = notifyResult.getAttach().split("#")[0];
                mchId = notifyResult.getAttach().split("#")[1];
            }else{
                attach=notifyResult.getAttach();
            }

            if(BillDetailEnum.TYPE_3.getValue().equals(attach)){
                if(orderInfo == null) return WxPayNotifyResponse.success("处理成功!");
                if(OrderInfoEnum.PAY_STATUS_1.getValue().equals(orderInfo.getPaid())){
                    return WxPayNotifyResponse.success("处理成功!");
                }
                orderService.paySuccess(orderInfo.getOrderId(),"weixin");
            }else if(BillDetailEnum.TYPE_1.getValue().equals(attach)){
                //处理充值
                YxUserRecharge userRecharge = userRechargeService.getInfoByOrderId(orderInfo.getOrderId());
                if(userRecharge == null) return WxPayNotifyResponse.success("处理成功!");
                if(OrderInfoEnum.PAY_STATUS_1.getValue().equals(userRecharge.getPaid())){
                    return WxPayNotifyResponse.success("处理成功!");
                }

                userRechargeService.updateRecharge(userRecharge);
            }
            YxStoreOrder storeOrder = new YxStoreOrder();
            storeOrder.setTradeNo(outTradeNo);
            /*if("WEB".equals(notifyResult.getDeviceInfo())){
                storeOrder.setIsChannel(0);
            }else{
                storeOrder.setIsChannel(1);
            }*/
            storeOrder.setMerchantNumber(mchId);
            QueryWrapper<YxStoreOrder> wrapper = new QueryWrapper<>();
            wrapper.eq("order_id",orderInfo.getOrderId());
            yxStoreOrderMapper.update(storeOrder,wrapper);

            return WxPayNotifyResponse.success("处理成功!");
        } catch (WxPayException e) {
            log.error(e.getMessage());
            return WxPayNotifyResponse.fail(e.getMessage());
        }

    }

    /**
     * 微信退款回调
     * @param xmlData
     * @return
     * @throws WxPayException
     */
    @AnonymousAccess
    @ApiOperation(value = "退款回调通知处理",notes = "退款回调通知处理")
    @PostMapping("/notify/refund")
    public String parseRefundNotifyResult(@RequestBody String xmlData) {
        try {
            WxPayService wxPayService = WxPayConfiguration.getPayService(WechatNameEnum.WECHAT.getValue());
            WxPayRefundNotifyResult result = wxPayService.parseRefundNotifyResult(xmlData);
            String outTradeNo = result.getReqInfo().getOutTradeNo();
            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("pay_out_trade_no",outTradeNo);
            YxStoreOrder orderInfo = orderService.getOne(queryWrapper,false);

            Integer refundFee = result.getReqInfo().getRefundFee()/100;
            if(orderInfo.getRefundStatus() == 2 || orderInfo.getNeedRefund() == 2 ){
                return WxPayNotifyResponse.success("处理成功!");
            }
            YxStoreOrder storeOrder = new YxStoreOrder();
            //修改状态
            storeOrder.setId(orderInfo.getId());
            storeOrder.setRefundStatus(2);
            storeOrder.setNeedRefund(2);
            storeOrder.setRefundPrice(BigDecimal.valueOf(refundFee));
            storeOrder.setRefundFactTime(new Date());
            orderService.updateOrderById(storeOrder);
            try{
                if(ProjectNameEnum.ZHONGANPUYAO.getValue().equals(orderInfo.getProjectCode()) || ProjectNameEnum.ZHONGANMANBING.getValue().equals(orderInfo.getProjectCode())) {

                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",orderInfo.getOrderId());
                    jsonObject.put("status",orderInfo.getStatus().toString());
                    jsonObject.put("desc","众安普药已退款订单");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);

                }else if(ProjectNameEnum.MEIDEYI.getValue().equals(orderInfo.getProjectCode())) {
                    cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                    jsonObject.put("orderNo",orderInfo.getOrderId());
                    jsonObject.put("status","-2");
                    jsonObject.put("desc","美德医已退款订单");
                    jsonObject.put("time", DateUtil.now());
                    mqProducer.sendDelayQueue(bizRoutekeyMeideyi, jsonObject.toString(),2000);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            return WxPayNotifyResponse.success("处理成功!");
        } catch (WxPayException e) {
            log.error(e.getMessage());
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }


    /**
     * 微信验证消息
     */
    @AnonymousAccess
    @GetMapping( value = "/wechat/serve",produces = "text/plain;charset=utf-8")
    @ApiOperation(value = "微信验证消息",notes = "微信验证消息")
    public String authGet(@RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr,
                          @RequestParam(name = "wechatName",required = false,defaultValue = "") String wechatName
                          ){
log.info("/wechat/serve get ,wehchatName={}",wechatName);
        final WxMpService wxService = WxMpConfiguration.getWxMpService(wechatName);
        if (wxService == null) {
            throw new IllegalArgumentException("未找到对应配置的服务，请核实！");
        }

        if (wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }

        return "fail";
    }


    @AnonymousAccess
    @PostMapping("/wechat/serve")
    @ApiOperation(value = "微信获取消息",notes = "微信获取消息")
    public void post(@RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature,
                     @RequestParam(name = "wechatName",required = false,defaultValue = "") String wechatName,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        log.info("/wechat/serve post ,wehchatName={}",wechatName);
        WxMpService wxService = WxMpConfiguration.getWxMpService(wechatName);

        if (!wxService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        String out = null;
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.route(inMessage,wechatName);
            if(outMessage == null) return;
            out = outMessage.toXml();;
        } else if ("aes".equalsIgnoreCase(encType)) {
            // aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, wxService.getWxMpConfigStorage(),
                    timestamp, nonce, msgSignature);
            WxMpXmlOutMessage outMessage = this.route(inMessage,wechatName);
            if(outMessage == null) return;

            out = outMessage.toEncryptedXml(wxService.getWxMpConfigStorage());
        }

        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(out);
        writer.close();
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message,String wechatName) {
        try {
            return WxMpConfiguration.getWxMpMessageRouter(wechatName).route(message);
        } catch (Exception e) {
            log.error("路由消息时出现异常！", e);
        }

        return null;
    }




}
