/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制，未经购买不得使用
 * 购买后可获得全部源代码（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 */
package co.yixiang.modules.alipay.web.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WechatController
 * @Author hupeng <610796224@qq.com>
 * @Date 2019/11/5
 **/
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "支付宝模块", tags = "支付宝:支付宝模块", description = "支付宝模块")
public class AlipayController extends BaseController {

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

    @Value("${meideyi.delayQueueName}")
    private String bizRoutekeyMeideyi;

    @Value("${zhonganpuyao.delayQueueName}")
    private String bizRoutekeyZhonganpuyao;

    /**
     * 支付宝支付 回调
     */
    @AnonymousAccess
    @PostMapping("/aliPay/notify")
    @ApiOperation(value = "支付宝支付回调", notes = "支付宝支付回调")
    public String renotify(HttpServletRequest request) {
        try (InputStream is = request.getInputStream()) {
            String str = StringUtil.inputStreamToString(is);
            log.info("aliPay/notify 支付宝回调参数字符串：" + str);
            Map<String, String> retMap = alipayPaySplit(str);
            log.info("aliPay/notify 支付宝回调参数：" + retMap);
            boolean flag = false;
            try {
                flag = AlipaySignature.rsaCheckV1(retMap, publicKey, "UTF-8", "RSA2");
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
            if (flag) {
                if ("TRADE_SUCCESS".equals(retMap.get("trade_status")) && StringUtils.isEmpty(retMap.get("out_biz_no"))) {
                    String attach ="";
                    String mchId="";
                    if(retMap.get("passback_params").contains("#")){
                        attach = retMap.get("passback_params").split("#")[0];
                        mchId = retMap.get("passback_params").split("#")[1];
                    }else{
                        attach=retMap.get("passback_params");
                    }

                    String outTradeNo = retMap.get("out_trade_no");
                    QueryWrapper queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("pay_out_trade_no",outTradeNo);
                    YxStoreOrder orderInfo = orderService.getOne(queryWrapper,false);

                    String tradeNo = retMap.get("trade_no").toString();
                    YxStoreOrder storeOrder = new YxStoreOrder();
                    storeOrder.setTradeNo(tradeNo);
                    storeOrder.setPayFrom("alipay");
                    storeOrder.setMerchantNumber(mchId);
                    if (BillDetailEnum.TYPE_3.getValue().equals(attach)) {
                        if (orderInfo == null) return WxPayNotifyResponse.success("处理成功!");
                        if (OrderInfoEnum.PAY_STATUS_1.getValue().equals(orderInfo.getPaid())) {
                            return WxPayNotifyResponse.success("处理成功!");
                        }
                        orderService.paySuccess(orderInfo.getOrderId(), "alipay");
                    }
                    QueryWrapper<YxStoreOrder> wrapper = new QueryWrapper<>();
                    wrapper.eq("order_id", orderInfo.getOrderId());
                    yxStoreOrderMapper.update(storeOrder, wrapper);
                }
            }
            String resp = "success";
            log.info("支付回调响服务器:{}", resp);
            return resp;
        } catch (Exception e) {
            log.error("notify error!", e);
            return "success";
        }
    }

    /**
     * 支付宝支付 回调
     */
    @AnonymousAccess
    @PostMapping("/aliPay/notifyH5")
    @ApiOperation(value = "支付宝支付回调", notes = "支付宝支付回调")
    public String notifyH5(HttpServletRequest request) {
        try (InputStream is = request.getInputStream()) {
            String str = StringUtil.inputStreamToString(is);
            log.info("aliPay/notifyH5支付宝回调参数字符串：" + str);
            Map<String, String> retMap = alipayPaySplit(str);
            log.info("aliPay/notifyH5支付宝回调参数：" + retMap);
            boolean flag = false;
            try {
                flag = AlipaySignature.rsaCheckV1(retMap, publicKeyH5, "UTF-8", "RSA2");
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
            if (flag) {
                if ("TRADE_SUCCESS".equals(retMap.get("trade_status")) && StringUtils.isEmpty(retMap.get("out_biz_no"))) {
                    String attach ="";
                    String mchId="";
                    if(retMap.get("passback_params").contains("#")){
                        attach = retMap.get("passback_params").split("#")[0];
                        mchId = retMap.get("passback_params").split("#")[1];
                    }else{
                        attach=retMap.get("passback_params");
                    }

                    String outTradeNo = retMap.get("out_trade_no");
                    QueryWrapper queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("pay_out_trade_no",outTradeNo);
                    YxStoreOrder orderInfo = orderService.getOne(queryWrapper,false);

                    String tradeNo = retMap.get("trade_no").toString();


                    YxStoreOrder storeOrder = new YxStoreOrder();
                    storeOrder.setTradeNo(tradeNo);
                    storeOrder.setPayFrom("alipayH5");
                    storeOrder.setMerchantNumber(mchId);
                    if (BillDetailEnum.TYPE_3.getValue().equals(attach)) {
                        if (orderInfo == null) return WxPayNotifyResponse.success("处理成功!");
                        if (OrderInfoEnum.PAY_STATUS_1.getValue().equals(orderInfo.getPaid())) {
                            return WxPayNotifyResponse.success("处理成功!");
                        }
                        orderService.paySuccess(orderInfo.getOrderId(), "alipay");
                    }
                    QueryWrapper<YxStoreOrder> wrapper = new QueryWrapper<>();
                    wrapper.eq("order_id", orderInfo.getOrderId());
                    yxStoreOrderMapper.update(storeOrder, wrapper);
                }
            }
            String resp = "success";
            log.info("支付回调响服务器:{}", resp);
            return resp;
        } catch (Exception e) {
            log.error("notify error!", e);
        }
        return null;

    }

    public static void main(String[] args) {
        String publiKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsoOcOrpuVusSP4JUQRVBfc9SDIuAFlPrAG2QOmCWEoxMsNMGv7HIWKnY1cg1dqLNJOcr6cdbcjlgdk6uVdbdSbTmAFUSLvFf7ZrU5C6CR/m0lfl9h8z4l+EIJNc17364ggscHVE3CiPw/37H3/CtNxdxhpopdpbo1jOeUHcWbM0OCS1gF56O8UI/CAN4f1fcF6DdfSBW+f5uRS0J5U7dCAvB0TBNw7XowsPmQ57jpWtLQxm9b7eCnp8an3VCLOv858ijAnG1qqfp2OOwqhLZUtfgdebHVcO1kuQbSVvv13+Yr+TdBvj+5FeUJIus0IcHX9q4vu3WorJXZFpYPqI9owIDAQAB";
        String s = "gmt_create=2021-08-12+10%3A23%3A16&charset=UTF-8&seller_email=zhifubao%40sph-zshare.com&subject=%E6%94%AF%E4%BB%98%E5%AE%9D%E6%94%AF%E4%BB%98&sign=AscMQr0hBdbIkhhowyom6TXJGX7Mb2WhcimmCCsDPXzVWmWH%2B%2FcMWhIfKmv2Mc9rKkLcec9ce%2F6xu%2Fo56%2FBgDgGOTVee48ir3kjXrBB%2BXgJueTt6QsqVUZJKdiCjJ2j9Oy4DoyCxKzT9i9m2Z453kX0rcVgTd5LtnWOL1RMs4jHsF8Tn0Uew6iBg5KWxReF%2F%2Bo%2BtmaNouEySjvOj%2F7azSSnQxHeQxmrS00W4haS8UDsQipV5rx2ZuYa93h9Efl0dVsbq2xC0qq3sYaJoKYkfTpXkco3ZLjbn7hD3QoI1ykF%2FNX4Gb3oRTkTybpcV6VWiHUR0SjgNZq13X5DrbMEGmQ%3D%3D&buyer_id=2088902444303553&invoice_amount=0.00&notify_id=2021081200222102517003551438083122&fund_bill_list=%5B%7B%22amount%22%3A%220.02%22%2C%22fundChannel%22%3A%22COUPON%22%7D%5D&notify_type=trade_status_sync&trade_status=TRADE_SUCCESS&receipt_amount=0.02&buyer_pay_amount=0.02&app_id=2021002164645220&sign_type=RSA2&seller_id=2088221447441083&gmt_payment=2021-08-12+10%3A25%3A17&notify_time=2021-08-12+11%3A51%3A03&passback_params=pay_product%262021002164645220&version=1.0&out_trade_no=1425644020243103744_TS&total_amount=0.02&trade_no=2021081222001403551453483331&auth_app_id=2021002164645220&buyer_logon_id=134****3170&point_amount=0.00";
        Map<String, String> retMap = alipayPaySplit(s);
        System.out.println(JSONObject.toJSON(retMap));
        System.out.println(StringUtils.isEmpty(retMap.get("trade_status")));
//        try {
//            boolean  flag = AlipaySignature.rsaCheckV1(retMap, publiKey, "UTF-8", "RSA2");
//            System.out.println(flag);
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }

    }


    public static Map<String, String> alipayPaySplit(String str) {
        try {
            str = URLDecoder.decode(str, "UTF-8");
            log.info("str:" + str);
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
