/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co

 */
package co.yixiang.mp.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.yixiang.enums.WechatNameEnum;
import co.yixiang.mp.config.WxMpConfiguration;
import co.yixiang.mp.domain.YxWechatReply;
import co.yixiang.mp.domain.YxWechatUserInfo;
import co.yixiang.mp.service.YxWechatReplyService;
import co.yixiang.mp.service.YxWechatUserInfoService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
public class SubscribeHandler extends AbstractHandler {

    @Autowired
    private YxWechatReplyService yxWechatReplyService;
    @Autowired
    private YxWechatUserInfoService yxWechatUserInfoService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {


        String str = "你好，欢迎关注益药!";
        log.info("关注公众号处理方法：关注人的openid={},toUser={}",wxMessage.getFromUser(),wxMessage.getToUser());
        YxWechatReply wechatReply = yxWechatReplyService.isExist("subscribe");
        if(!ObjectUtil.isNull(wechatReply)){
            str = JSONObject.parseObject(wechatReply.getData()).getString("content");
        }

        try {

            // 根据openId 获取详细信息
            WxMpService wxService = WxMpConfiguration.getWxMpService(WechatNameEnum.WECHAT.getValue());
            WxMpUser wxMpUser =  wxService.getUserService().userInfo(wxMessage.getFromUser());

            log.info("关注公众号处理方法：wxMpUser={}", JSONUtil.parseObj(wxMpUser).toString());

            if(wxMpUser != null && StrUtil.isNotBlank(wxMpUser.getUnionId()) ) {
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("union_id",wxMpUser.getUnionId());
                queryWrapper.eq("wechat_name",WechatNameEnum.WECHAT.getValue());
                queryWrapper.select("id");
                YxWechatUserInfo yxWechatUserInfo = yxWechatUserInfoService.getOne(queryWrapper,false);
                if(yxWechatUserInfo == null) {
                    yxWechatUserInfo = new YxWechatUserInfo();
                }

                BeanUtils.copyProperties(wxMpUser,yxWechatUserInfo);
                yxWechatUserInfo.setWechatName(WechatNameEnum.WECHAT.getValue());
                yxWechatUserInfoService.saveOrUpdate(yxWechatUserInfo);

                // 用户表里的openid 更新
                yxWechatUserInfoService.updateWechatOpenidByUniqueId(wxMpUser.getUnionId(),wxMpUser.getOpenId());
            }


            WxMpXmlOutMessage msg= WxMpXmlOutMessage.TEXT()
                    .content(str)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
            return msg;
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }



        return null;
    }



}
