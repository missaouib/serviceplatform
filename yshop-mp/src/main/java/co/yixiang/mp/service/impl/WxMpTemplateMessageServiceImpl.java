/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.mp.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeData;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import co.yixiang.mp.config.ShopKeyUtils;
import co.yixiang.mp.config.WxMaConfiguration;
import co.yixiang.mp.config.WxMpConfiguration;
import co.yixiang.mp.service.WxMpTemplateMessageService;
import co.yixiang.mp.service.dto.OrderTemplateMessage;
import co.yixiang.utils.RedisUtil;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Component
public class WxMpTemplateMessageServiceImpl implements WxMpTemplateMessageService {

    @Override
    public String sendWxMpTemplateMessage(String openId, String templateId, String url, Map<String,String> map){
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId(templateId)
                .url(url)
                .build();
        map.forEach( (k,v)-> { templateMessage.addData(new WxMpTemplateData(k, v, "#000000"));} );
        String msgId = null;
        WxMpService wxService = WxMpConfiguration.getWxMpService("");
        try {
            msgId =   wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return msgId;
    }

    @Override
    public String sendWxMpTemplateMessage(String openId, String templateId, String url, Map<String,String> map,String wechatName){
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId(templateId)
                .url(url)
                .build();
        map.forEach( (k,v)-> { templateMessage.addData(new WxMpTemplateData(k, v, "#000000"));} );
        String msgId = null;
        WxMpService wxService = WxMpConfiguration.getWxMpService(wechatName);
        try {
            msgId =   wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return msgId;
    }

    @Override
    public String sendDYTemplateMessage( String openId, String templateId, String page, OrderTemplateMessage message) {

        String miniAppId = RedisUtil.get(ShopKeyUtils.getWxAppAppId());

        WxMaSubscribeMessage subscribeMessage = new WxMaSubscribeMessage();

        //跳转小程序页面路径
        subscribeMessage.setPage(page);
        //模板消息id
        subscribeMessage.setTemplateId(templateId);
        //给谁推送 用户的openid （可以调用根据code换openid接口)
        subscribeMessage.setToUser(openId);
        //==========================================创建一个参数集合========================================================
        ArrayList<WxMaSubscribeData> wxMaSubscribeData = new ArrayList<>();

//        订阅消息参数值内容限制说明
//              ---摘自微信小程序官方：https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/subscribe-message/subscribeMessage.send.html
//        参数类别 	参数说明 	参数值限制 	   说明
//        thing.DATA 	事物 	20个以内字符 	可汉字、数字、字母或符号组合
//        number.DATA 	数字 	32位以内数字 	只能数字，可带小数
//        letter.DATA 	字母 	32位以内字母 	只能字母
//        symbol.DATA 	符号 	5位以内符号 	只能符号
//        character_string.DATA 	字符串 	32位以内数字、字母或符号 	可数字、字母或符号组合
//        time.DATA 	时间 	24小时制时间格式（支持+年月日） 	例如：15:01，或：2019年10月1日 15:01
//        date.DATA 	日期 	年月日格式（支持+24小时制时间） 	例如：2019年10月1日，或：2019年10月1日 15:01
//        amount.DATA 	金额 	1个币种符号+10位以内纯数字，可带小数，结尾可带“元” 	可带小数
//        phone_number.DATA 	电话 	17位以内，数字、符号 	电话号码，例：+86-0766-66888866
//        car_number.DATA 	车牌 	8位以内，第一位与最后一位可为汉字，其余为字母或数字 	车牌号码：粤A8Z888挂
//        name.DATA 	姓名 	10个以内纯汉字或20个以内纯字母或符号 	中文名10个汉字内；纯英文名20个字母内；中文和字母混合按中文名算，10个字内
//        phrase.DATA 	汉字 	5个以内汉字 	5个以内纯汉字，例如：配送中

        /*
        *
        * 订单ID
{{character_string1.DATA}}

订单状态
{{phrase2.DATA}}

订单时间
{{date3.DATA}}

提示说明
{{thing4.DATA}}
        * */

        String orderId = message.getOrderId();
        String orderStatus = message.getOrderStatus();
        String orderDate = message.getOrderDate();
        String remark = message.getRemark();

        //第一个内容： 订单ID
        WxMaSubscribeData wxMaSubscribeData1 = new WxMaSubscribeData();
        wxMaSubscribeData1.setName("character_string1");
        wxMaSubscribeData1.setValue(orderId);
        //每个参数 存放到大集合中
        wxMaSubscribeData.add(wxMaSubscribeData1);

        // 第二个内容：订单状态
        WxMaSubscribeData wxMaSubscribeData2 = new WxMaSubscribeData();
        wxMaSubscribeData2.setName("phrase2");
        wxMaSubscribeData2.setValue(orderStatus);
        wxMaSubscribeData.add(wxMaSubscribeData2);

        // 第三个内容：订单时间
        WxMaSubscribeData wxMaSubscribeData3 = new WxMaSubscribeData();
        wxMaSubscribeData3.setName("date3");
        wxMaSubscribeData3.setValue(orderDate);
        wxMaSubscribeData.add(wxMaSubscribeData3);

        // 第四个内容：提示说明
        WxMaSubscribeData wxMaSubscribeData4 = new WxMaSubscribeData();
        wxMaSubscribeData4.setName("thing4");
        wxMaSubscribeData4.setValue(remark);
        wxMaSubscribeData.add(wxMaSubscribeData4);

        //把集合给大的data
        subscribeMessage.setData(wxMaSubscribeData);
        //=========================================封装参数集合完毕========================================================

        try {
            //获取微信小程序配置：
            final WxMaService wxService = WxMaConfiguration.getMaService(miniAppId);
            //进行推送
            wxService.getMsgService().sendSubscribeMsg(subscribeMessage);
            return "推送成功";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "推送失败";


    }

    @Override
    public String sendWxMpTemplateMessageMiniApp(String openId, String templateId, String url, Map<String, String> map, String wechatName, String miniAppid, String miniPagePath) {
        WxMpTemplateMessage.MiniProgram miniProgram = new WxMpTemplateMessage.MiniProgram();
        miniProgram.setAppid(miniAppid);
        miniProgram.setPagePath(miniPagePath);
        miniProgram.setUsePath(false);

        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId(templateId)
                .url(url)
                .miniProgram(miniProgram)
                .build();
        map.forEach( (k,v)-> { templateMessage.addData(new WxMpTemplateData(k, v, "#000000"));} );
        String msgId = null;
        WxMpService wxService = WxMpConfiguration.getWxMpService(wechatName);
        try {
            msgId =   wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return msgId;
    }
}
