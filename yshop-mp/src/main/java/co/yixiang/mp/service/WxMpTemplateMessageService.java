/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.mp.service;

import co.yixiang.mp.service.dto.OrderTemplateMessage;

import java.util.Map;

public interface WxMpTemplateMessageService {

    String sendWxMpTemplateMessage(String openId, String templateId, String url, Map<String, String> map);

    String sendWxMpTemplateMessage(String openId, String templateId, String url, Map<String, String> map,String wechatName);

    // 微信小程序订阅通知-- 收到互联网医院的电子处方，通知客户完善订单信息
    String sendDYTemplateMessage(String openId, String templateId, String page, OrderTemplateMessage message);

    String sendWxMpTemplateMessageMiniApp(String openId, String templateId, String url, Map<String, String> map,String wechatName,String miniAppid,String miniPagePath);
}
