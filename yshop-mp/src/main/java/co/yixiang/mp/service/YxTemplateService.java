package co.yixiang.mp.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeData;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.date.DateUtil;
import co.yixiang.enums.WechatNameEnum;
import co.yixiang.mp.config.ShopKeyUtils;
import co.yixiang.mp.config.WxMaConfiguration;
import co.yixiang.mp.domain.YxWechatTemplate;
import co.yixiang.mp.service.dto.OrderTemplateMessage;
import co.yixiang.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName 微信公众号模板通知
 * @Author hupeng <610796224@qq.com>
 * @Date 2020/3/2
 **/
@Service
public class YxTemplateService {
    private final String PAY_SUCCESS_KEY = "OPENTM207791277"; //pay
    private final String DELIVERY_SUCCESS_KEY = "OPENTM200565259"; //Delivery
    private final String REFUND_SUCCESS_KEY = "OPENTM410119152"; //Refund
    private final String RECHARGE_SUCCESS_KEY = "OPENTM405847076"; //Recharge
    private final String REQUEST_KEY = "OPENTM405840001"; // 咨询提醒 （发给药师端）

    private final String NOTICE_KEY = "OPENTM405840003"; // 药师服务提醒（发给患者端）

    private final String MED_CYCLE_NOTICE_KEY = "OPENTM405840002"; //用药周期提醒

    private final String Order_Status_Notice_KEY = "OPENTM200565260"; //订单状态提醒

    @Autowired
    private YxWechatTemplateService templateService;
    @Autowired
    private WxMpTemplateMessageService templateMessageService;

     @Value("${yaoshitong.url}")
     private String yaoshitongUrl;
    /**
     * 支付成功通知
     * @param time
     * @param price
     * @param openid
     */
    public void rechargeSuccessNotice(String time,String price,String openid){
        String siteUrl = RedisUtil.get(ShopKeyUtils.getSiteUrl(WechatNameEnum.WECHAT.getValue()));
        YxWechatTemplate WechatTemplate = templateService.findByTempkey(RECHARGE_SUCCESS_KEY);
        Map<String,String> map = new HashMap<>();
        map.put("first","您的账户金币发生变动，详情如下：");
        map.put("keyword1","充值");
        map.put("keyword2",time);
        map.put("keyword3",price);
        map.put("remark","益药电商系统为你服务！");
        templateMessageService.sendWxMpTemplateMessage( openid
                ,WechatTemplate.getTempid(),
                siteUrl+"/user/account",map);
    }


    /**
     * 支付成功通知
     * @param orderId
     * @param price
     * @param openid
     */
    public void paySuccessNotice(String orderId,String price,String openid){
        String siteUrl = RedisUtil.get(ShopKeyUtils.getSiteUrl(WechatNameEnum.WECHAT.getValue()));
        YxWechatTemplate WechatTemplate = templateService.findByTempkey(PAY_SUCCESS_KEY);
        Map<String,String> map = new HashMap<>();
        map.put("first","您的订单已支付成功，我们会尽快为您发货。");
        map.put("keyword1",price);
        map.put("keyword2",orderId);//订单号
        map.put("remark","益药电商系统为你服务！");
        templateMessageService.sendWxMpTemplateMessage( openid
                ,WechatTemplate.getTempid(),
                siteUrl+"/order/detail/"+orderId,map);
    }

    /**
     * 退款成功通知
     * @param orderId
     * @param price
     * @param openid
     * @param time
     */
    public void refundSuccessNotice(String orderId,String price,String openid,String time){
        String siteUrl = RedisUtil.get(ShopKeyUtils.getSiteUrl(WechatNameEnum.WECHAT.getValue()));
        YxWechatTemplate WechatTemplate = templateService.findByTempkey(REFUND_SUCCESS_KEY);
        Map<String,String> map = new HashMap<>();
        map.put("first","您在益药商城的订单退款申请被通过，钱款将很快还至您的支付账户。");
        map.put("keyword1",orderId);//订单号
        map.put("keyword2",price);
        map.put("keyword3", time);
        map.put("remark","益药电商系统为你服务！");
        templateMessageService.sendWxMpTemplateMessage( openid
                ,WechatTemplate.getTempid(),
                siteUrl+"/order/detail/"+orderId,map);
    }

    /**
     * 发货成功通知
     * @param orderId
     * @param deliveryName
     * @param deliveryId
     * @param openid
     */
    public void deliverySuccessNotice(String orderId,String deliveryName,String deliveryId,String openid){
        String siteUrl = RedisUtil.get(ShopKeyUtils.getSiteUrl(WechatNameEnum.WECHAT.getValue()));
        YxWechatTemplate WechatTemplate = templateService.findByTempkey(DELIVERY_SUCCESS_KEY);
        Map<String,String> map = new HashMap<>();
        map.put("first","亲，宝贝已经启程了，好想快点来到你身边。");
        map.put("keyword1",orderId);//订单号
        map.put("keyword2",deliveryName);
        map.put("keyword3",deliveryId);
        map.put("remark","益药电商系统为你服务！");
        templateMessageService.sendWxMpTemplateMessage( openid
                ,WechatTemplate.getTempid(),
                siteUrl+"/order/detail/"+orderId,map);
    }

    /**
     * 药师通端，患者向药师发起咨询
     * @param
     * @param
     * @param
     * @param openid
     */
    public void requestNotice(String userName,String userPhone,String requestDate,String openid){
       // String siteUrl = RedisUtil.get(ShopKeyUtils.getSiteUrl(WechatNameEnum.WECHAT.getValue()));
        YxWechatTemplate WechatTemplate = templateService.findByTempkey(REQUEST_KEY);
        Map<String,String> map = new HashMap<>();
        map.put("first","患者向您发起了咨询服务，请及时处理。");
        map.put("keyword1",userName);//客户姓名
        map.put("keyword2",userPhone);//手机号码
        map.put("keyword3",requestDate);//咨询时间
        map.put("remark","药师通系统为你服务！");
        templateMessageService.sendWxMpTemplateMessage( openid
                ,WechatTemplate.getTempid(),
                yaoshitongUrl+"/archivesList",map,"yaoshitong");
    }


    /**
     * 药师通端，向药师通知患者用药周期
     * @param
     * @param
     * @param
     * @param openid
     */
    public void medCycleNotice(String drugstoreName,Integer amount,String openid){
        // String siteUrl = RedisUtil.get(ShopKeyUtils.getSiteUrl(WechatNameEnum.WECHAT.getValue()));
        YxWechatTemplate WechatTemplate = templateService.findByTempkey(MED_CYCLE_NOTICE_KEY);
        Map<String,String> map = new HashMap<>();
        map.put("first","患者用药周期提醒");
        map.put("keyword1",drugstoreName);//药房名称
        map.put("keyword2", DateUtil.now());//提醒时间
        map.put("keyword3","有"+amount+"条患者用药周期临近提醒信息，请安排时间回访");//汇总数据
        map.put("remark","药师通系统为你服务！");
        templateMessageService.sendWxMpTemplateMessage( openid
                ,WechatTemplate.getTempid(),
                yaoshitongUrl+"/user/remind",map,"yaoshitong");
    }


    // 微信小程序订阅通知-- 订单状态通知
    public void sendDYTemplateMessage( String openId,  String page, OrderTemplateMessage message) {

        YxWechatTemplate WechatTemplate = templateService.findByTempkey(Order_Status_Notice_KEY);

        templateMessageService.sendDYTemplateMessage(openId,WechatTemplate.getTempid(),page,message);


    }


    /**
     * 患者端，药师给患者留言提醒
     * @param
     * @param
     * @param
     * @param openid
     */
    public void requestNotice(String title, String content,String requestDate,String remark, String openid){
        // String siteUrl = RedisUtil.get(ShopKeyUtils.getSiteUrl(WechatNameEnum.WECHAT.getValue()));

        String miniAppid = RedisUtil.get(ShopKeyUtils.getWxAppAppId());
        String miniPagePath = "pages/wode/myPharmacist";
        YxWechatTemplate WechatTemplate = templateService.findByTempkey(NOTICE_KEY);
        Map<String,String> map = new HashMap<>();
        map.put("first",title);
        map.put("keyword1",content);//提醒内容
        map.put("keyword2",requestDate);//提醒时间
        map.put("remark",remark);
        templateMessageService.sendWxMpTemplateMessageMiniApp( openid
                ,WechatTemplate.getTempid(),
                miniPagePath,map,"",miniAppid,miniPagePath);
    }
}
