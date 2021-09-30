/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 */
package co.yixiang.tools.utils;

import co.yixiang.tools.domain.AlipayConfig;
import co.yixiang.utils.JsonUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝工具类
 * @author zhengjie
 * @date 2018/09/30 14:04:35
 */
@Slf4j
@Component
public class AlipayUtils {


    /**
     * 生成订单号
     * @return String
     */
    public String getOrderCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int a = (int) (Math.random() * 9000.0D) + 1000;
        System.out.println(a);
        Date date = new Date();
        String str = sdf.format(date);
        String[] split = str.split("-");
        String s = split[0] + split[1] + split[2];
        String[] split1 = s.split(" ");
        String s1 = split1[0] + split1[1];
        String[] split2 = s1.split(":");
        return split2[0] + split2[1] + split2[2] + a;
    }

    /**
     * 校验签名
     * @param request HttpServletRequest
     * @param alipay 阿里云配置
     * @return boolean
     */
    public boolean rsaCheck(HttpServletRequest request, AlipayConfig alipay) {

        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>(1);
        Map requestParams = request.getParameterMap();
        for (Object o : requestParams.keySet()) {
            String name = (String) o;
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        try {
            return AlipaySignature.rsaCheckV1(params,
                    alipay.getPublicKey(),
                    alipay.getCharset(),
                    alipay.getSignType());
        } catch (AlipayApiException e) {
            return false;
        }
    }

    public static String alipayH5Pay(String body, String subject, String outTradeNo, String timeoutExpress, String totalAmount,String appId,String orderId) {
        log.info("金额totalAmount:" + totalAmount);
        //实例化客户端
//        orderId = orderId + "_" + DateUtil.formatDate(new Date(), "yyMMddHHmmss");

        AlipayClient alipayClient = new DefaultAlipayClient(AlipayProperties.serverUrlH5,appId, AlipayProperties.privateKey, AlipayProperties.format, AlipayProperties.charset, AlipayProperties.publicKeyH5, AlipayProperties.signType);
        log.info("AlipayClient :" + JSONObject.toJSONString(alipayClient));
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeWapPayRequest alipayRequest=new AlipayTradeWapPayRequest();

        // 封装请求支付信息
        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
        model.setOutTradeNo(outTradeNo);
        model.setSubject(subject);
        model.setTotalAmount(totalAmount);
        model.setBody(body);
        model.setProductCode("QUICK_WAP_PAY");
        model.setPassbackParams("pay_product#"+appId);
        model.setTimeoutExpress(timeoutExpress);
        alipayRequest.setBizModel(model);
        // 设置异步通知地址
        alipayRequest.setNotifyUrl(AlipayProperties.notifyUrlH5);
        alipayRequest.setReturnUrl(AlipayProperties.returnUrlH5+"?orderId=" + orderId+"&price="+totalAmount);
        log.info("AlipayTradeWapPayRequest  :" + JSONObject.toJSONString(alipayRequest));

        try {
            String form = alipayClient.pageExecute(alipayRequest).getBody();
            log.info("alipayClient.pageExecute.getBody() from :" + form);
            return form;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getUserId(String auth_code) {
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayProperties.serverUrl,AlipayProperties.appId, AlipayProperties.privateKey,AlipayProperties.format, AlipayProperties.charset, AlipayProperties.publicKey, AlipayProperties.signType);
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        // 值为authorization_code时，代表用code换取
        request.setGrantType("authorization_code");
        //授权码，用户对应用授权后得到的
        request.setCode(auth_code);
        //这里使用execute方法
        try {
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            //刷新令牌，上次换取访问令牌时得到。见出参的refresh_token字段
            request.setRefreshToken(response.getAccessToken());
            if (response.isSuccess()) {
                return response.getUserId();
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String alipayTradeAppPay(String subject, String userid, String outTradeNo, String timeoutExpress, String totalAmount,String appId) {
        log.info("金额totalAmount:" + totalAmount);
        //实例化客户端
//        orderId = orderId + "_" + DateUtil.formatDate(new Date(), "yyMMddHHmmss");

        AlipayClient alipayClient = new DefaultAlipayClient(AlipayProperties.serverUrl,appId, AlipayProperties.privateKey, AlipayProperties.format, AlipayProperties.charset, AlipayProperties.publicKey, AlipayProperties.signType);
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。

        JSONObject json=new JSONObject();
        //订单号
        json.put("out_trade_no",outTradeNo);
        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
        json.put("total_amount",totalAmount);
        //描述
        json.put("subject",subject);
        //用户唯一标识id 这里必须使用buyer_id 参考文档
        json.put("buyer_id",userid);
        //回传参数
        json.put("passback_params","pay_product#"+appId);
        //对象转化为json字符串
        String jsonStr=json.toString();

        request.setBizContent(jsonStr);
        request.setNotifyUrl(AlipayProperties.notifyUrl);

        try {
            AlipayTradeCreateResponse response = alipayClient.execute(request);
            String trade_no = response.getTradeNo();// 获取返回的tradeNO。
            log.info("trade_no:"+trade_no);
            return trade_no;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String refundOrder(String tradeNo, String refundAmount, String serverUrl, String appId, String publicKey,String notityUrl) {
        log.info("开始调用支付宝退款接口******************************************************");
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl,appId, AlipayProperties.privateKey, AlipayProperties.format, AlipayProperties.charset,publicKey, AlipayProperties.signType);
        AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model=new AlipayTradeRefundModel();
        model.setTradeNo(tradeNo);
        model.setRefundAmount(refundAmount);
        alipay_request.setBizModel(model);
        alipay_request.setNotifyUrl(notityUrl);
        log.info("支付宝退款 refundOrder  :" + JSONObject.toJSONString(alipay_request));
        try {
            AlipayTradeRefundResponse response = alipayClient.execute(alipay_request);
            log.info("支付宝退款msg"+response.getMsg() + "\n");
            log.info("支付宝退款body"+response.getBody());
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("支付宝退款错误！", e.getMessage());
            return null;
        }
    }
}
