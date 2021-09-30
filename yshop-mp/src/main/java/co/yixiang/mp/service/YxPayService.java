/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.mp.service;

import cn.hutool.core.util.StrUtil;
import co.yixiang.enums.WechatNameEnum;
import co.yixiang.exception.ErrorRequestException;
import co.yixiang.mp.config.ShopKeyUtils;
import co.yixiang.mp.config.WxPayConfiguration;
import co.yixiang.mp.handler.RedisHandler;
import co.yixiang.mp.utils.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMwebOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName 公众号支付YxPayService
 * @Author hupeng <610796224@qq.com>
 * @Date 2020/3/1
 **/
@Service
//@AllArgsConstructor
@Slf4j
public class YxPayService {

    @Autowired
    private RedisHandler redisHandler;

    @Value("${server.ip}")
    private String ip;

    /**
     * 微信公众号支付
     *
     * @param orderId
     * @param openId   公众号openid
     * @param body
     * @param totalFee
     * @return
     * @throws WxPayException
     */
    public WxPayMpOrderResult wxPay(String orderId, String openId, String body,
                                    Integer totalFee,String attach) throws WxPayException {

        String apiUrl = redisHandler.getVal(ShopKeyUtils.getApiUrl(WechatNameEnum.WECHAT.getValue()));
        if (StrUtil.isBlank(apiUrl)) throw new ErrorRequestException("请配置api地址");
//        orderId = orderId + "_" + DateUtil.formatDate(new Date(), "yyMMddHHmmss");

        WxPayService wxPayService = WxPayConfiguration.getPayService(WechatNameEnum.WECHAT.getValue());
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setDeviceInfo("WEB");
        orderRequest.setTradeType("JSAPI");
        orderRequest.setOpenid(openId);
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(orderId);
        orderRequest.setTotalFee(totalFee);
        orderRequest.setSpbillCreateIp(ip);
        orderRequest.setNotifyUrl(apiUrl + "/api/wechat/notify");
        orderRequest.setAttach(attach+"#"+wxPayService.getConfig().getMchId());


        WxPayMpOrderResult orderResult = wxPayService.createOrder(orderRequest);

        return orderResult;

    }


    /**
     * 微信H5支付
     *
     * @param orderId
     * @param body
     * @param totalFee
     * @return
     * @throws WxPayException
     */
    public WxPayMwebOrderResult wxH5Pay(String orderId, String body,
                                        Integer totalFee,String attach) throws WxPayException {

        String apiUrl = redisHandler.getVal(ShopKeyUtils.getApiUrl(WechatNameEnum.WECHAT.getValue()));
        if (StrUtil.isBlank(apiUrl)) throw new ErrorRequestException("请配置api地址");
//        orderId = orderId + "_" + DateUtil.formatDate(new Date(), "yyMMddHHmmss");

        WxPayService wxPayService = WxPayConfiguration.getPayService(WechatNameEnum.WECHAT.getValue());
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setDeviceInfo("WEB");
        orderRequest.setTradeType("MWEB");
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(orderId);
        orderRequest.setTotalFee(totalFee);
        orderRequest.setSpbillCreateIp(ip);
        orderRequest.setNotifyUrl(apiUrl + "/api/wechat/notify");
        orderRequest.setAttach(attach+"#"+wxPayService.getConfig().getMchId());
        log.info("微信h5 配置："+ JSONObject.toJSONString(wxPayService.getConfig()) +",WxPayUnifiedOrderRequest:"+JSONObject.toJSONString(orderRequest));
        WxPayMwebOrderResult orderResult = wxPayService.createOrder(orderRequest);

        return orderResult;

    }

    /**
     * 微信app支付
     *
     * @param orderId
     * @param body
     * @param totalFee
     * @return
     * @throws WxPayException
     */
    public WxPayAppOrderResult appPay(String orderId, String body,
                                      Integer totalFee, String attach) throws WxPayException {

        String apiUrl = redisHandler.getVal(ShopKeyUtils.getApiUrl(WechatNameEnum.WECHAT.getValue()));
        if (StrUtil.isBlank(apiUrl)) throw new ErrorRequestException("请配置api地址");
//        orderId = orderId + "_" + DateUtil.formatDate(new Date(), "yyMMddHHmmss");

        WxPayService wxPayService = WxPayConfiguration.getAppPayService(WechatNameEnum.WECHAT.getValue());
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();

        orderRequest.setTradeType("APP");
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(orderId);
        orderRequest.setTotalFee(totalFee);
        orderRequest.setSpbillCreateIp("127.0.0.1");
        orderRequest.setNotifyUrl(apiUrl + "/api/wechat/notify");
        orderRequest.setAttach(attach+"#"+wxPayService.getConfig().getMchId());

        WxPayAppOrderResult appOrderResult = wxPayService.createOrder(orderRequest);

        return appOrderResult;

    }


    /**
     * 退款
     * @param orderId
     * @param totalFee
     * @throws WxPayException
     */
    public void refundOrder(String orderId, Integer totalFee) throws WxPayException {
        String apiUrl = redisHandler.getVal(ShopKeyUtils.getApiUrl(WechatNameEnum.WECHAT.getValue()));
        if (StrUtil.isBlank(apiUrl)) throw new ErrorRequestException("请配置api地址");

        WxPayService wxPayService = WxPayConfiguration.getPayService(WechatNameEnum.WECHAT.getValue());
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();

        wxPayRefundRequest.setTotalFee(totalFee);//订单总金额
        wxPayRefundRequest.setOutTradeNo(orderId);
        wxPayRefundRequest.setOutRefundNo(orderId);
        wxPayRefundRequest.setRefundFee(totalFee);//退款金额
        wxPayRefundRequest.setNotifyUrl(apiUrl + "/api/notify/refund");

        wxPayService.refund(wxPayRefundRequest);
    }


    /**
     * 企业打款
     * @param openid
     * @param no
     * @param userName
     * @param amount
     * @throws WxPayException
     */
    public void entPay(String openid,String no,String userName,Integer amount) throws WxPayException{
        WxPayService wxPayService = WxPayConfiguration.getPayService(WechatNameEnum.WECHAT.getValue());
        EntPayRequest entPayRequest = new EntPayRequest();

        entPayRequest.setOpenid(openid);
        entPayRequest.setPartnerTradeNo(no);
        entPayRequest.setCheckName("FORCE_CHECK");
        entPayRequest.setReUserName(userName);
        entPayRequest.setAmount(amount);
        entPayRequest.setDescription("提现");
        entPayRequest.setSpbillCreateIp("127.0.0.1");
        wxPayService.getEntPayService().entPay(entPayRequest);

    }


}
