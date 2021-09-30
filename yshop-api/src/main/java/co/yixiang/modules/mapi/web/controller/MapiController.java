/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.mapi.web.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.common.web.controller.BaseController;
import co.yixiang.enums.BillDetailEnum;
import co.yixiang.enums.OrderInfoEnum;
import co.yixiang.enums.ProjectNameEnum;
import co.yixiang.enums.WechatNameEnum;
import co.yixiang.modules.order.entity.YxStoreOrder;
import co.yixiang.modules.order.mapper.YxStoreOrderMapper;
import co.yixiang.modules.order.service.YxStoreOrderService;
import co.yixiang.modules.order.utils.StringUtil;
import co.yixiang.modules.order.web.vo.YxStoreOrderQueryVo;
import co.yixiang.modules.shop.service.YxSystemConfigService;
import co.yixiang.modules.user.entity.YxUserRecharge;
import co.yixiang.modules.user.service.YxUserRechargeService;
import co.yixiang.mp.config.WxPayConfiguration;
import co.yixiang.rabbitmq.send.MqProducer;
import co.yixiang.tools.utils.mpai.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @ClassName WechatController
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/11/5
 **/
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "翼支付模块", tags = "翼支付模块", description = "翼支付模块")
public class MapiController extends BaseController {

    private final YxStoreOrderService orderService;
    private final YxSystemConfigService systemConfigService;
    private final YxUserRechargeService userRechargeService;

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private YxStoreOrderMapper yxStoreOrderMapper;

    @Value("${alipay.publicKey}")
    private String publicKey;
    @Value("${alipay.publicKeyH5}")
    private String publicKeyH5;

    // 业务队列绑定业务交换机的routeKey
    @Value("${meideyi.delayQueueName}")
    private String bizRoutekeyMeideyi;

    @Value("${zhonganpuyao.delayQueueName}")
    private String bizRoutekeyZhonganpuyao;

    /**
     * 翼支付 支付 回调
     */
    @AnonymousAccess
    @PostMapping("/mapi/notifyH5")
    @ApiOperation(value = "翼支付回调", notes = "翼支付回调")
    public JSONObject notifyH5(HttpServletRequest request) {
        try (InputStream is = request.getInputStream()) {
            String str = StringUtil.inputStreamToString(is);
            log.info("翼支付回调参数字符串：" + str);
            Map retMap = JSON.parseObject(str);

            String serviceName=MapiProperties.serviceCertificateName;
            log.info("serviceName：" + serviceName);

            boolean isOk =  com.bestpay.api.util.SignatureUtil.checkSign(retMap, serviceName);
            log.info("isOk：" + isOk);
//            String checksign = String.valueOf(retMap.get("sign"));
//            String checkContent = assembelSignaturingData(retMap);
//
//            InputStream pubStream = SignatureProgram.class.getClassLoader().getResourceAsStream(MapiProperties.serviceCertificateName);
//            byte pubByte[] = new byte[2048] ;
//            pubStream.read(pubByte);
//            pubStream.close();
//            X509Certificate x509Certificate = CryptoUtil.base64StrToCert(Base64Encrypt.getBASE64ForByte(pubByte));
//            BouncyCastleProvider bouncyCastleProvider2 = new BouncyCastleProvider();
//            Signature signatureCheck = Signature.getInstance("SHA1withRSA",bouncyCastleProvider2);
//            boolean flag = SignatureUtil.verify(signatureCheck,checkContent,checksign,x509Certificate.getPublicKey());

            if (isOk) {
                if("SUCCESS".equals(retMap.get("tradeStatus"))){
                    String attach = retMap.get("memo").toString();
                    String outTradeNo = retMap.get("outTradeNo").toString();
                    String tradeNo = retMap.get("tradeNo").toString();

                    QueryWrapper queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("pay_out_trade_no",outTradeNo);
                    YxStoreOrder orderInfo = orderService.getOne(queryWrapper,false);

                    YxStoreOrder storeOrder = new YxStoreOrder();
                    storeOrder.setTradeNo(tradeNo);
                    storeOrder.setPayFrom("h5");


                    JSONObject result=new JSONObject();
                    result.put("statusCode",200);
                    result.put("outTradeNo",outTradeNo);
                    result.put("tradeNo",tradeNo);

                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("success",true);
                    jsonObject.put("result",result);
                    log.info("支付回调响服务器:{}", jsonObject);

                    if (BillDetailEnum.TYPE_3.getValue().equals(attach)) {
                        if (orderInfo == null) return jsonObject;
                        if (OrderInfoEnum.PAY_STATUS_1.getValue().equals(orderInfo.getPaid())) {
                            return jsonObject;
                        }
                        orderService.paySuccess(orderInfo.getOrderId(), "mapi");
                    } else if (BillDetailEnum.TYPE_1.getValue().equals(attach)) {
                        //处理充值
                        YxUserRecharge userRecharge = userRechargeService.getInfoByOrderId(orderInfo.getOrderId());
                        if (userRecharge == null) return jsonObject;
                        if (OrderInfoEnum.PAY_STATUS_1.getValue().equals(userRecharge.getPaid())) {
                            return jsonObject;
                        }
                        userRechargeService.updateRecharge(userRecharge);
                    }
                    QueryWrapper<YxStoreOrder> wrapper = new QueryWrapper<>();
                    wrapper.eq("order_id", orderInfo.getOrderId());
                    yxStoreOrderMapper.update(storeOrder, wrapper);

                    return jsonObject;
                }else if("FAIL".equals(retMap.get("tradeStatus"))){
                    JSONObject result=new JSONObject();
                    result.put("statusCode",200);
                    result.put("outTradeNo",retMap.get("outTradeNo").toString());
                    result.put("tradeNo",retMap.get("tradeNo").toString());

                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("success",true);
                    jsonObject.put("result",result);
                    log.info("支付回调响服务器:{}", jsonObject);
                    return jsonObject;

                }
            }
        } catch (Exception e) {
            log.error("notify error!", e);
        }
        return null;
    }


    /**
     * 支付宝支付 回调
     */
    @AnonymousAccess
    @PostMapping("/mapi/returnNotifyH5")
    @ApiOperation(value = "翼支付退款回调", notes = "翼支付退款回调")
    public String returnNotifyH5(HttpServletRequest request) {
        try (InputStream is = request.getInputStream()) {
            String str = StringUtil.inputStreamToString(is);
            log.info("翼支付退款回调参数字符串：" + str);
            Map<String, String> retMap = mapiPaySplit(str);
            log.info("翼支付退款回调参数：" + retMap);

            String checksign = String.valueOf(retMap.get("sign"));
            String checkContent = assembelSignaturingData(retMap);

            InputStream pubStream = SignatureProgram.class.getClassLoader().getResourceAsStream("服务器证书.crt");
            byte pubByte[] = new byte[2048] ;
            pubStream.read(pubByte);
            pubStream.close();
            X509Certificate x509Certificate = CryptoUtil.base64StrToCert(Base64Encrypt.getBASE64ForByte(pubByte));
            BouncyCastleProvider bouncyCastleProvider2 = new BouncyCastleProvider();
            Signature signatureCheck = Signature.getInstance("SHA1withRSA",bouncyCastleProvider2);
            boolean flag = SignatureUtil.verify(signatureCheck,checkContent,checksign,x509Certificate.getPublicKey());

            if (flag) {
                if("200".equals(retMap.get("resultCode"))){
                    String outTradeNo = retMap.get("outTradeNo");
                    String refundFee = retMap.get("tradeAmt");

                    QueryWrapper queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("pay_out_trade_no",outTradeNo);
                    YxStoreOrder orderInfo = orderService.getOne(queryWrapper,false);

                    if (orderInfo.getRefundStatus() == 2 || orderInfo.getNeedRefund() == 2) {
                        return WxPayNotifyResponse.success("处理成功!");
                    }
                    YxStoreOrder storeOrder = new YxStoreOrder();
                    //修改状态
                    storeOrder.setId(orderInfo.getId());
                    storeOrder.setRefundStatus(2);
                    storeOrder.setNeedRefund(2);
                    storeOrder.setRefundPrice(new BigDecimal(refundFee));
                    orderService.updateById(storeOrder);

                    try {
                        if (ProjectNameEnum.ZHONGANPUYAO.getValue().equals(orderInfo.getProjectCode()) || ProjectNameEnum.ZHONGANMANBING.getValue().equals(orderInfo.getProjectCode())) {
                            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                            jsonObject.put("orderNo", orderInfo.getOrderId());
                            jsonObject.put("status", orderInfo.getStatus().toString());
                            jsonObject.put("desc", "众安普药已退款订单");
                            jsonObject.put("time", DateUtil.now());
                            mqProducer.sendDelayQueue(bizRoutekeyZhonganpuyao, jsonObject.toString(),2000);

                        } else if (ProjectNameEnum.MEIDEYI.getValue().equals(orderInfo.getProjectCode())) {
                            cn.hutool.json.JSONObject jsonObject = JSONUtil.createObj();
                            jsonObject.put("orderNo", orderInfo.getOrderId());
                            jsonObject.put("status", "-2");
                            jsonObject.put("desc", "美德医已退款订单");
                            jsonObject.put("time", DateUtil.now());
                            mqProducer.sendDelayQueue(bizRoutekeyMeideyi, jsonObject.toString(),2000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            String resp = "SUCCESS";
            log.info("支付回调响服务器:{}", resp);
            return resp;
        } catch (Exception e) {
            log.error("notify error!", e);
        }
        return null;

    }

    public static void main(String[] args) {
        String publiKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsoOcOrpuVusSP4JUQRVBfc9SDIuAFlPrAG2QOmCWEoxMsNMGv7HIWKnY1cg1dqLNJOcr6cdbcjlgdk6uVdbdSbTmAFUSLvFf7ZrU5C6CR/m0lfl9h8z4l+EIJNc17364ggscHVE3CiPw/37H3/CtNxdxhpopdpbo1jOeUHcWbM0OCS1gF56O8UI/CAN4f1fcF6DdfSBW+f5uRS0J5U7dCAvB0TBNw7XowsPmQ57jpWtLQxm9b7eCnp8an3VCLOv858ijAnG1qqfp2OOwqhLZUtfgdebHVcO1kuQbSVvv13+Yr+TdBvj+5FeUJIus0IcHX9q4vu3WorJXZFpYPqI9owIDAQAB";
        String s = "gmt_create=2021-07-14+09%3A49%3A30&charset=UTF-8&seller_email=zhifubao%40sph-zshare.com&subject=%E4%B8%8A%E6%B5%B7%E7%9B%8A%E8%8D%AF%E8%8D%AF%E4%B8%9A&sign=RmXF15c%2BHfYGfPCMmq0aINWA%2B2QIRKnMUTsoxyo4AZpnUEZR0S3Mz1L%2BYh7pD7Jq68YqxPzEPTG6noS9Zjk0SELR90O1qMA3J6dwGuTPIZAm5NBLi7oLqponMS8I5XcS7m71rjSAypo2uQDPthTraqmzfCv6Ju0YV9tl52tRm49rK2VHFByxRHC9Bq%2BwFHK65%2FbgYm%2BqAtXmGLi7ssJ1g4qE1Pk8zqO0Qn1WvtnpbI9%2BWpSJatNTn%2B4d1BTlb608RU4oiQFLfwYUE2Srb%2FCfn%2BqH3uNsQZaAkhoQ9gNQxHid1zULuqmmSLrwNfg8iJ4LHw5nnP0ZAHIiRfYxXUJG5g%3D%3D&body=%E6%94%AF%E4%BB%98%E5%AE%9D%E6%94%AF%E4%BB%98&buyer_id=2088902112235014&notify_id=2021071400222095137035011452559224&notify_type=trade_status_sync&trade_status=TRADE_CLOSED&app_id=2021002150672444&sign_type=RSA2&seller_id=2088221447441083&gmt_payment=2021-07-14+09%3A49%3A30&notify_time=2021-07-14+09%3A51%3A37&gmt_refund=2021-07-14+09%3A51%3A36.631&out_biz_no=1415126234332921856&passback_params=pay_product&version=1.0&out_trade_no=1415126234332921856&total_amount=0.60&refund_fee=0.60&trade_no=2021071422001435011433387454&auth_app_id=2021002150672444&buyer_logon_id=180***%40189.cn&gmt_close=2021-07-14+09%3A51%3A36";
        Map<String, String> retMap = mapiPaySplit(s);
        System.out.println(JSONObject.toJSON(retMap));
        System.out.println(StringUtils.isEmpty(retMap.get("trade_status")));
        try {
            boolean  flag = AlipaySignature.rsaCheckV1(retMap, publiKey, "UTF-8", "RSA2");
            System.out.println(flag);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

    }


    static String assembelSignaturingData(Map data) {
        StringBuilder sb = new StringBuilder();
        TreeMap<String, Object> treeMap = new TreeMap(data);
        for (Map.Entry<String, Object> ent : treeMap.entrySet()) {
            String name = ent.getKey();
            if (/* !"signType".equals(name) &&*/ !"sign".equals(name)) {
                sb.append(name).append('=').append(ent.getValue()).append('&');
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static Map<String, String> mapiPaySplit(String str) {
        try {
            str = URLDecoder.decode(str, "UTF-8");
            System.err.println("str:" + str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<String, String>();
        String[] param = str.split("&");
        for (String keyvalue : param) {
            int index = keyvalue.indexOf("=");
            map.put(keyvalue.substring(0, index), keyvalue.substring(index + 1));
        }
        return map;
    }

}
