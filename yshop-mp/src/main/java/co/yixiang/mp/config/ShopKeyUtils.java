package co.yixiang.mp.config;

import co.yixiang.constant.ShopConstants;
import co.yixiang.constant.SystemConfigConstants;
import co.yixiang.enums.WechatNameEnum;

/**
 * 处理缓存key值的统一入口，方面后面扩展，
 * 例如:多租户就要在每个key后拼接上租户ID，只要统一修改这里就可以了
 */
public class ShopKeyUtils {
    /**
     *扩展值，默认为空， 把这个值追加到所有key值上
     */
    private static  String getExtendValue(){
        String extendValue= "";
        return  extendValue;
    }

    //*********************************begin yx_system_config 通用值 *****************************************************

    /**
     * api_url
     */
    public static  String getApiUrl(String wechatName){
        String apiUrl="";
        if(WechatNameEnum.YIAOSHITONG.getValue().equals(wechatName)) {
             apiUrl= SystemConfigConstants.API_URL_YAOSHITONG;
        }else{
             apiUrl= SystemConfigConstants.API_URL;
        }

        return  apiUrl;
    }
    /**
     * site_url
     */
    public static  String getSiteUrl(String wechatName){
        String siteUrl = "";
        if(WechatNameEnum.YIAOSHITONG.getValue().equals(wechatName)) {
            siteUrl= SystemConfigConstants.SITE_URL_YAOSHITONG;
        }else{
            siteUrl= SystemConfigConstants.SITE_URL;
        }

        return  siteUrl;
    }
    /**
     * 腾讯mapkey tengxun_map_key
     */
    public static  String getTengXunMapKey(){
        String tengxunMapKey= SystemConfigConstants.TENGXUN_MAP_KEY;
        return  tengxunMapKey;
    }

    //*********************************begin yx_system_config 业务字段 *****************************************************
    /**
     * store_self_mention
     */
    public static  String getStoreSelfMention(){
        String storeSelfMention= SystemConfigConstants.STORE_SEFL_MENTION;
        return  storeSelfMention+getExtendValue();
    }


    //*********************************begin yx_system_config 微信配置相关字段 *****************************************************

    /**
     * 微信公众号service
     */
    public static  String getYshopWeiXinMpSevice(String wechatName){
        String yshopWeiXinMpSevice="";
        if(WechatNameEnum.YIAOSHITONG.getValue().equals(wechatName)) {
            yshopWeiXinMpSevice= ShopConstants.YSHOP_WEIXIN_MP_SERVICE_YAOSHITONG;
        } else {
            yshopWeiXinMpSevice= ShopConstants.YSHOP_WEIXIN_MP_SERVICE;
        }

        return  yshopWeiXinMpSevice+getExtendValue();
    }

    /**
     * 微信公众号id
     */
    public static  String getWechatAppId(String wechatName){
        String wechatAppId="";
        if(WechatNameEnum.YIAOSHITONG.getValue().equals(wechatName)) {
            wechatAppId= SystemConfigConstants.WECHAT_APPID_YAOSHITONG;
        }else{
            wechatAppId= SystemConfigConstants.WECHAT_APPID;
        }

        return  wechatAppId+getExtendValue();
    }
    /**
     * 微信公众号secret
     */
    public static  String getWechatAppSecret(String wechatName){
        String wechatAppSecret = "";
        if(WechatNameEnum.YIAOSHITONG.getValue().equals(wechatName)) {
            wechatAppSecret= SystemConfigConstants.WECHAT_APPSECRET_YAOSHITONG;
        }else{
            wechatAppSecret= SystemConfigConstants.WECHAT_APPSECRET;
        }

        return  wechatAppSecret+getExtendValue();
    }
    /**
     * 微信公众号验证token
     */
    public static  String getWechatToken(String wechatName){
        String wechatToken = "";
        if(WechatNameEnum.YIAOSHITONG.getValue().equals(wechatName)) {
            wechatToken= SystemConfigConstants.WECHAT_TOKEN_YAOSHITONG;
        } else {
            wechatToken= SystemConfigConstants.WECHAT_TOKEN;
        }

        return  wechatToken+getExtendValue();
    }
    /**
     * 微信公众号 EncodingAESKey
     */
    public static  String getWechatEncodingAESKey(String wechatName){
        String wechatEncodingAESKey = "";
        if(WechatNameEnum.YIAOSHITONG.getValue().equals(wechatName)) {
            wechatEncodingAESKey= SystemConfigConstants.WECHAT_ENCODINGAESKEY_YAOSHITONG;
        }else {
            wechatEncodingAESKey= SystemConfigConstants.WECHAT_ENCODINGAESKEY;
        }

        return  wechatEncodingAESKey+getExtendValue();
    }
    /**
     * 微信支付service
     */
    public static  String getYshopWeiXinPayService(String wechatName){
        String yshopWeiXinPayService= "";
        if(WechatNameEnum.YIAOSHITONG.getValue().equals(wechatName)) {
            yshopWeiXinPayService= ShopConstants.YSHOP_WEIXIN_PAY_SERVICE_YAOSHITONG;
        }else if(WechatNameEnum.GUANGZHOU.getValue().equals(wechatName)){
            yshopWeiXinPayService= ShopConstants.YSHOP_WEIXIN_PAY_SERVICE_GUANGZHOU;
        }else{
            yshopWeiXinPayService= ShopConstants.YSHOP_WEIXIN_PAY_SERVICE;
        }

        return  yshopWeiXinPayService+getExtendValue();
    }
    /**
     * 商户号
     */
    public static  String getWxPayMchId(String wechatName){
        String wxPayMchId = "";
        if(WechatNameEnum.YIAOSHITONG.getValue().equals(wechatName)) {
            wxPayMchId= SystemConfigConstants.WXPAY_MCHID_YAOSHITONG;
        } else if(WechatNameEnum.GUANGZHOU.getValue().equals(wechatName)){
            wxPayMchId= SystemConfigConstants.WXPAY_MCHID_GUANGZHOU;
        }else{
            wxPayMchId= SystemConfigConstants.WXPAY_MCHID;
        }

        return  wxPayMchId+getExtendValue();
    }
    /**
     * 商户秘钥
     */
    public static  String getWxPayMchKey(String wechatName){
        String wxPayMchKey= "";
        if(WechatNameEnum.YIAOSHITONG.getValue().equals(wechatName)) {
            wxPayMchKey= SystemConfigConstants.WXPAY_MCHKEY_YAOSHITONG;
        } else if(WechatNameEnum.GUANGZHOU.getValue().equals(wechatName)){
            wxPayMchKey= SystemConfigConstants.WXPAY_MCHKEY_GUANGZHOU;
        }else{
            wxPayMchKey= SystemConfigConstants.WXPAY_MCHKEY;
        }

        return  wxPayMchKey+getExtendValue();
    }
    /**
     * 商户证书路径
     */
    public static  String getWxPayKeyPath(String wechatName){
        String wxPayKeyPath = "";
        if(WechatNameEnum.YIAOSHITONG.getValue().equals(wechatName)) {
            wxPayKeyPath= SystemConfigConstants.WXPAY_KEYPATH_YAOSHITONG;
        }else if(WechatNameEnum.GUANGZHOU.getValue().equals(wechatName)){
            wxPayKeyPath= SystemConfigConstants.WXPAY_KEYPATH_GUANGZHOU;
        }else{
            wxPayKeyPath= SystemConfigConstants.WXPAY_KEYPATH;
        }

        return  wxPayKeyPath+getExtendValue();
    }
    /**
     * 微信支付小程序service
     */
    public static  String getYshopWeiXinMiniPayService(){
        String yshopWeiXinMiniPayService= ShopConstants.YSHOP_WEIXIN_MINI_PAY_SERVICE;
        return  yshopWeiXinMiniPayService+getExtendValue();
    }
    /**
     * 微信支付app service
     */
    public static  String getYshopWeiXinAppPayService(){
        String yshopWeiXinAppPayService= ShopConstants.YSHOP_WEIXIN_APP_PAY_SERVICE;
        return  yshopWeiXinAppPayService+getExtendValue();
    }
    /**
     * 微信小程序id
     */
    public static  String getWxAppAppId(){
        String wxAppAppId= SystemConfigConstants.WXAPP_APPID;
        return  wxAppAppId+getExtendValue();
    }
    /**
     * 微信小程序秘钥
     */
    public static  String getWxAppSecret(){
        String wxAppSecret= SystemConfigConstants.WXAPP_SECRET;
        return  wxAppSecret+getExtendValue();
    }

    /**
     * 支付appId
     */
    public static  String getWxNativeAppAppId(){
        String wxNativeAppAppId= SystemConfigConstants.WX_NATIVE_APP_APPID;
        return  wxNativeAppAppId+getExtendValue();
    }

}
