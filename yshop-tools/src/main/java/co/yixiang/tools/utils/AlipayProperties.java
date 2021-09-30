package co.yixiang.tools.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayProperties {


    public static String format;

    public static String charset;

    public static String signType;

    public static String serverUrlH5;

    public static String serverUrl;

    public static String appIdH5;

    public static String appId;

    public static String notifyUrlH5;

    public static String notifyUrl;

    public static String returnUrlH5;

    public static String privateKey;

    public static String publicKey;

    public static String publicKeyH5;


    @Value("${alipay.appId}")
    public void setAppId(String appId) {
        AlipayProperties.appId = appId;
    }
    @Value("${alipay.format}")
    public  void setFormat(String format) {
        AlipayProperties.format = format;
    }
    @Value("${alipay.charset}")
    public  void setCharset(String charset) {
        AlipayProperties.charset = charset;
    }
    @Value("${alipay.signType}")
    public  void setSignType(String signType) {
        AlipayProperties.signType = signType;
    }
    @Value("${alipay.serverUrlH5}")
    public  void setServerUrlH5(String serverUrlH5) {
        AlipayProperties.serverUrlH5 = serverUrlH5;
    }
    @Value("${alipay.serverUrl}")
    public  void setServerUrl(String serverUrl) {
        AlipayProperties.serverUrl = serverUrl;
    }
    @Value("${alipay.appIdH5}")
    public  void setAppIdH5(String appIdH5) {
        AlipayProperties.appIdH5 = appIdH5;
    }
    @Value("${alipay.notifyUrlH5}")
    public  void setNotifyUrlH5(String notifyUrlH5) {
        AlipayProperties.notifyUrlH5 = notifyUrlH5;
    }
    @Value("${alipay.notifyUrl}")
    public  void setNotifyUrl(String notifyUrl) {
        AlipayProperties.notifyUrl = notifyUrl;
    }
    @Value("${alipay.returnUrlH5}")
    public  void setReturnUrlH5(String returnUrlH5) {
        AlipayProperties.returnUrlH5 = returnUrlH5;
    }
    @Value("${alipay.privateKey}")
    public  void setPrivateKey(String privateKey) {
        AlipayProperties.privateKey = privateKey;
    }
    @Value("${alipay.publicKey}")
    public  void setPublicKey(String publicKey) {
        AlipayProperties.publicKey = publicKey;
    }
    @Value("${alipay.publicKeyH5}")
    public  void setPublicKeyH5(String publicKeyH5) {
        AlipayProperties.publicKeyH5 = publicKeyH5;
    }
}
