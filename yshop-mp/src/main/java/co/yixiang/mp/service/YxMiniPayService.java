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
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName 小程序支付YxPayService
 * @Author hupeng <610796224@qq.com>
 * @Date 2020/3/12
 **/
@Service
@AllArgsConstructor
public class YxMiniPayService {

    private final RedisHandler redisHandler;

    /**
     * 小程序支付
     *
     * @param orderId
     * @param openId   小程序openid
     * @param body
     * @param totalFee
     * @return
     * @throws WxPayException
     */
    public WxPayMpOrderResult wxPay(String orderId, String openId, String body,
                                    Integer totalFee,String attach,String mchName) throws WxPayException {

        String apiUrl = redisHandler.getVal(ShopKeyUtils.getApiUrl(WechatNameEnum.WECHAT.getValue()));
        if (StrUtil.isBlank(apiUrl)) throw new ErrorRequestException("请配置api地址");
//        orderId = orderId + "_" + DateUtil.formatDate(new Date(), "yyMMddHHmmss");

        WxPayService wxPayService = WxPayConfiguration.getWxAppPayService(mchName);
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();

        orderRequest.setTradeType("JSAPI");
        orderRequest.setOpenid(openId);
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(orderId);
        orderRequest.setTotalFee(totalFee);
        orderRequest.setSpbillCreateIp("127.0.0.1");
        orderRequest.setNotifyUrl(apiUrl + "/api/wechat/notify");
        orderRequest.setAttach(attach+"#"+wxPayService.getConfig().getMchId());


        WxPayMpOrderResult orderResult = wxPayService.createOrder(orderRequest);

        return orderResult;

    }


    /**
     * 小程序支付
     *
     * @param orderId
     * @param openId   小程序openid
     * @param body
     * @param totalFee
     * @return
     * @throws WxPayException
     */
    public WxPayMpOrderResult wxPay4zhongan(String orderId, String openId, String body,
                                    Integer totalFee,String attach,String mchName) throws WxPayException {

        String apiUrl = redisHandler.getVal(ShopKeyUtils.getApiUrl(WechatNameEnum.WECHAT.getValue()));
        if (StrUtil.isBlank(apiUrl)) throw new ErrorRequestException("请配置api地址");
//        orderId = orderId + "_" + DateUtil.formatDate(new Date(), "yyMMddHHmmss");

        WxPayService wxPayService = WxPayConfiguration.getWxAppPayService4zhongan(mchName);
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();

        orderRequest.setTradeType("JSAPI");
        orderRequest.setOpenid(openId);
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(orderId);
        orderRequest.setTotalFee(totalFee);
        orderRequest.setSpbillCreateIp("127.0.0.1");
        orderRequest.setNotifyUrl(apiUrl + "/api/wechat/notify");
        orderRequest.setAttach(attach+"#"+wxPayService.getConfig().getMchId());


        WxPayMpOrderResult orderResult = wxPayService.createOrder(orderRequest);

        return orderResult;

    }



    /**
     * 退款
     * @param orderId
     * @param totalFee
     * @throws WxPayException
     */
    public void refundOrder(String orderId, Integer totalFee,String mchName) throws WxPayException {
        String apiUrl = redisHandler.getVal(ShopKeyUtils.getApiUrl(WechatNameEnum.WECHAT.getValue()));
        if (StrUtil.isBlank(apiUrl)) throw new ErrorRequestException("请配置api地址");

        WxPayService wxPayService = WxPayConfiguration.getWxAppPayService(mchName);
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();

        wxPayRefundRequest.setTotalFee(totalFee);//订单总金额
        wxPayRefundRequest.setOutTradeNo(orderId);
        wxPayRefundRequest.setOutRefundNo(orderId);
        wxPayRefundRequest.setRefundFee(totalFee);//退款金额
        wxPayRefundRequest.setNotifyUrl(apiUrl + "/api/notify/refund");

        wxPayService.refund(wxPayRefundRequest);
    }


    /**
     * 退款
     * @param orderId
     * @param totalFee
     * @throws WxPayException
     */
    public void refundOrder4zhongan(String orderId, Integer totalFee,String mchName) throws WxPayException {
        String apiUrl = redisHandler.getVal(ShopKeyUtils.getApiUrl(WechatNameEnum.WECHAT.getValue()));
        if (StrUtil.isBlank(apiUrl)) throw new ErrorRequestException("请配置api地址");

        WxPayService wxPayService = WxPayConfiguration.getWxAppPayService4zhongan(mchName);
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
        WxPayService wxPayService = WxPayConfiguration.getWxAppPayService(WechatNameEnum.WECHAT.getValue());
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
