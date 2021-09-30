package co.yixiang.tools.service.impl;

import cn.hutool.core.util.StrUtil;

import co.yixiang.tools.express.util.HttpUtils;
import co.yixiang.tools.service.dto.MD5;
import co.yixiang.tools.service.dto.SmsResult;
import co.yixiang.tools.service.dto.TeddyResult;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SmsServiceImpl {




    /**
     * 泰迪熊短信供应商
     *
     * @param title
     * @param msg
     * @param phones
     * @return
     */
    public SmsResult sendTeddy(String title, String msg, String phones) {
        try {
            SmsResult smsResult = new SmsResult();
            String result = "";
            msg = title + msg;
            String memo = msg.length() < 1000 ? msg.trim() : msg.trim().substring(0, 1000);
            Map m = new HashMap();
            String urlStr = "http://cmd-sp.teddymobile.cn/api/sms/multiSend";
            m.put("account", "td_shzx");
            m.put("password", MD5.MD5Encode("shzx0716"));
            Map data = new HashMap();
            data.put("phones", phones);
            data.put("content", msg.substring(msg.indexOf("】", 0) + 1));
            data.put("sign", msg.substring(0, msg.indexOf("】", 0) + 1));
            m.put("data", data);
            String jsonStr = JSON.toJSONString(m);
            log.info("泰迪熊短信发送内容[{}]",jsonStr);
            result = HttpUtils.postJson(urlStr, jsonStr, null);
           log.info("泰迪熊短信返回[{}]",result);
            TeddyResult teddyResult = JSON.parseObject(result, TeddyResult.class);
            // sysSmsSendService.generate(msg, phones, teddyResult.getError_msg(), teddyResult.getError_code() + "");

            smsResult.setError(String.valueOf(teddyResult.getError_code()));
            return smsResult;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 发送支付短链短信
     *
     * @param medStr         通用名列表
     * @param orderId        订单ID
     * @param receiverMobile 接受手机
     * @param totalAmount   支付总金额 tot
     *
     */
   /* public void sendRemindMessage(String medStr, String orderId, String receiverMobile, BigDecimal totalAmount) {
        //--发送短信给用户
        StringBuffer message = new StringBuffer();
       // String longUrl = "https://h5.yiyao-mall.com/orderPay?orderId=%s";
        String longUrl =  payUrl;
        longUrl = String.format(longUrl, orderId);
       // String shortUrl = ShortUrlUtil.createShortUrl(longUrl);
        String shortUrl = shortUrlService.createShortUrl(longUrl);

        if(StrUtil.isBlank(shortUrl)) {
            shortUrl = longUrl;
        }

        String remindmessage = "【益药】您提交的处方已审核通过，预订信息：%s，合计金额：%s人民币。请点击链接进行支付。 %s";
// medStr  = medStr + orderDetail.getCommonName() + " "+orderDetail.getAmount()+"盒"+",";
        if (StrUtil.isNotBlank(medStr)) {
            medStr = medStr.substring(0, medStr.length() - 1);
        }

        remindmessage = String.format(remindmessage, medStr, totalAmount.toString(), shortUrl);
        message.append(remindmessage);
        // log.info("接收电话："+receiverMobile+"；短信内容："+message.toString());
        sendTeddy("", message.toString(), receiverMobile);
    }*/
}