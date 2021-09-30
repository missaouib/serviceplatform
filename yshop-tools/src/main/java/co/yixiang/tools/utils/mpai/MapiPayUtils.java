/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 */
package co.yixiang.tools.utils.mpai;

import co.yixiang.exception.BadRequestException;
import co.yixiang.utils.DateUtils;
import co.yixiang.utils.OrderUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bestpay.api.common.EnvEnum;
import com.bestpay.api.exception.BestpayApiException;
import com.bestpay.api.model.request.BestpayRequest;
import com.bestpay.api.model.response.BestpayResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 翼支付
 * @author zhoujinlai
 * @date 2021-07-29
 */
@Slf4j
@Component
public class MapiPayUtils {

    /**
     * 翼支付  h5支付
     * @param body
     * @param subject
     * @param orderId
     * @param totalAmount
     * @return
     */
    public static String mapiH5Pay(String body, String subject, String orderId, String totalAmount,String returnUrl,String parms) {
        BigDecimal bigDecimal = new BigDecimal(100);
        log.info("金额totalAmount:" + totalAmount);

        String userCertificateName = MapiProperties.userCertificateName;
        String serviceCertificateName = MapiProperties.serviceCertificateName;
        String passwd = MapiProperties.passwd;
        String alias = MapiProperties.alias;
        String keyStoreType = "PKCS12";

        log.info("MapiProperties.userCertificateName:" + userCertificateName);
        log.info("MapiProperties.serviceCertificateName:" + serviceCertificateName);
        log.info("MapiProperties.passwd:" + passwd);
        log.info("MapiProperties.path:" + MapiProperties.path);
        log.info("MapiProperties.alias:" + alias);
        log.info("MapiProperties.notifyUrl:" + MapiProperties.notifyUrl);
        log.info("MapiProperties.version:" + MapiProperties.version);

        UpdateDefaultBestpayClient bestpayClient = new UpdateDefaultBestpayClient(userCertificateName, serviceCertificateName, passwd, alias, keyStoreType);
        BestpayRequest request = new BestpayRequest();
        request.setEnvEnum(MapiProperties.path.equals("prd")?EnvEnum.PRODUCT:EnvEnum.TEST);
        request.setCommonParams("{" +
                "\"institutionType\":\"MERCHANT\"," +
                "\"institutionCode\":\""+MapiProperties.institutionCode+"\"" +
                "}");
        request.setPath("/uniformReceipt/proCreateOrder");
        request.setVersion(MapiProperties.version);
        request.setBizContent("{" +
                "\"accessCode\": \"CASHIER\"," +
                "\"ccy\": \"156\"," +
                "\"goodsInfo\": \"Mi6\"," +
                "\"mediumType\": \"WIRELESS\"," +
                "\"merchantNo\": \""+MapiProperties.merchantNo+"\"," +
                "\"notifyUrl\": \""+MapiProperties.notifyUrl+"\"," +
                "\"operator\": \""+MapiProperties.merchantNo+"\"," +
                "\"outTradeNo\": \""+orderId+"\"," +
                "\"requestDate\": \""+ DateUtils.getTime()+"\"," +
                "\"subject\": \""+subject+"\"," +
                "\"tradeAmt\": \""+bigDecimal.multiply(new BigDecimal(totalAmount)).intValue()+"\"," +
                "\"tradeChannel\": \"H5\"," +
                 "\"memo\": \"pay_product\"," +
                "}");

        try {
            BestpayResponse response = bestpayClient.execute(request);

            if (response.isSuccess()) {
                log.info("成功处理......response:"+response);
                log.info("成功处理......getResult:"+response.getResult());
                try {
                    return addSignatureProgram(response.getResult(),bigDecimal.multiply(new BigDecimal(totalAmount)).intValue()+"",subject,returnUrl,parms);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("失败处理......errorCode:"+response.getErrorCode()+" errorMsg:"+ response.getErrorMsg());
                return "";
            }
        } catch (BestpayApiException e) {
            e.printStackTrace();
            log.info(e.getErrMsg());
            log.info("异常处理......errorCode:"+e.getErrCode()+" errorMsg:"+e.getErrMsg());
        }
        return "";
    }


    public static String addSignatureProgram(String resultStr,String totalAmount,String subject,String returnUrl,String parms) throws GeneralSecurityException, IOException {
        JSONObject jsonObject=JSON.parseObject(resultStr);

        InputStream resourceAsStream = SignatureProgram.class.getClassLoader().getResourceAsStream(MapiProperties.userCertificateName);
        String passwd = MapiProperties.passwd;
        String alias = MapiProperties.alias;
        String keyStoreType = "PKCS12";
        BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
        Signature signature = Signature.getInstance("SHA256withRSA",bouncyCastleProvider);

        KeyCertInfo keyCertInfo = CryptoUtil.fileStreamToKeyCertInfo(resourceAsStream,passwd,keyStoreType,alias);


        Map<String,String> translateResultData=new HashMap<>();
        translateResultData.put("institutionCode",jsonObject.getString("merchantNo"));
        translateResultData.put("merchantNo",jsonObject.getString("merchantNo"));
        translateResultData.put("outTradeNo",jsonObject.getString("outTradeNo"));
        translateResultData.put("tradeAmt",totalAmount);
        translateResultData.put("institutionType","MERCHANT");
        translateResultData.put("signType","CA");
        translateResultData.put("platform","H5_4.0_route");
        translateResultData.put("tradeType","acquiring");
        translateResultData.put("tradeNo",jsonObject.getString("tradeNo"));
        translateResultData.put("tradeDesc",subject);
        translateResultData.put("merchantFrontUrl",returnUrl);
        translateResultData.put("merchantFrontUrlParms",URLEncoder.encode(parms));

        String content = assembelSignaturingData(translateResultData);

        String sign = SignatureUtil.sign(signature,content, (PrivateKey) keyCertInfo.getPrivateKey());
        translateResultData.put("sign",sign);

        TreeMap<String, Object> treeMap = new TreeMap(translateResultData);

        log.info(JSON.toJSONString(treeMap));
        String url="https://mapi.bestpay.com.cn/mapi/form/cashier/H5/pay?"+jsonToURL(treeMap);
        String from="<form  method=\"post\" action=\""+url+"\">" +
                        "<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n" +
                    "</form>\n" +
                    "<script>document.forms[0].submit();</script>";
        log.info("翼支付 from："+from);

        return from;
    }

    //顺序组装请求参数，用于签名/校验
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


    @SuppressWarnings({"deprecation","rawtypes"})
    public static String jsonToURL(TreeMap<String, Object> map) {
        String url = "";

        try {
            Iterator it = map.entrySet().iterator();

            StringBuffer sb = new StringBuffer();

            while(it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();

                sb.append(entry.getKey().toString() + "=");

                String value = entry.getValue().toString();

                if (value == null || value.isEmpty() || value.length() == 0) {
                    sb.append("&");

                } else {
                    sb.append(URLEncoder.encode(value) + "&");

                }

            }

            url = sb.toString().substring(0,sb.length() - 1);

        } catch(Exception e) {
            e.printStackTrace();

        }

        return url;

    }



    public static String refundOrder(String outTradeNo, String refundAmount,String orderId) {
        BigDecimal bigDecimal = new BigDecimal(100);
        log.info("金额totalAmount:" + refundAmount);

        String userCertificateName = MapiProperties.userCertificateName;
        String serviceCertificateName = MapiProperties.serviceCertificateName;
        String passwd = MapiProperties.passwd;
        String alias = MapiProperties.alias;
        log.info("userCertificateName:" + userCertificateName);
        log.info("serviceCertificateName:" + serviceCertificateName);
        log.info("passwd:" + passwd);
        log.info("alias:" + alias);

        String keyStoreType = "PKCS12";

        UpdateDefaultBestpayClient bestpayClient = new UpdateDefaultBestpayClient(userCertificateName, serviceCertificateName, passwd, alias, keyStoreType);
        BestpayRequest request = new BestpayRequest();
        request.setEnvEnum(MapiProperties.path.equals("prd")?EnvEnum.PRODUCT:EnvEnum.TEST);
        request.setCommonParams("{" +
                "\"institutionType\":\"MERCHANT\"," +
                "\"institutionCode\":\""+MapiProperties.institutionCode+"\"" +
                "}");
        request.setPath("/uniformReceipt/tradeRefund");
        request.setVersion(MapiProperties.version);
        request.setBizContent("{" +
                "\"merchantNo\":\""+MapiProperties.merchantNo+"\"," +
                "\"outTradeNo\":\""+outTradeNo+"\"," +
                "\"outRequestNo\":\""+orderId+ OrderUtil.getSecondTimestampTwo()+"\"," +
                "\"originalTradeDate\":\""+ DateUtils.getTime()+"\"," +
                "\"refundAmt\":\""+bigDecimal.multiply(new BigDecimal(refundAmount)).intValue()+"\"," +
                "\"requestDate\":\""+ DateUtils.getTime()+"\"," +
                "\"operator\":\""+MapiProperties.merchantNo+"\"," +
                "\"tradeChannel\":\"H5\"," +
                "\"ccy\":\"156\"," +
                "\"accessCode\":\"CASHIER\"," +
//                "\"notifyUrl\":\""+MapiProperties.returnNotifyUrl+"\"" +
                "}");

        try {
            BestpayResponse response = bestpayClient.execute(request);

            if (response.isSuccess()) {
                log.info("成功处理......response:"+response);
                log.info("成功处理......getResult:"+response.getResult());
                return response.getResult();
            } else {
                if(StringUtils.isNotEmpty(response.getErrorCode()) && response.getErrorCode().equals("REFUND_ALREADY")){
                    return  "已支付";
                }
                log.info("失败处理......errorCode:"+response.getErrorCode()+" errorMsg:"+ response.getErrorMsg());
                throw new BadRequestException("退款失败，"+response.getErrorMsg());
            }
        } catch (BestpayApiException e) {
            e.printStackTrace();
            log.info(e.getErrMsg());
            log.info("异常处理......errorCode:"+e.getErrCode()+" errorMsg:"+e.getErrMsg());
            throw new BadRequestException("退款失败，"+e.getErrMsg());
        }
    }




    public static void main(String[] args){

//        String userCertificateName = "广州上药益药药房有限公司.P12";
//        String serviceCertificateName = "天翼电子商务有限公司new.cer";
//        String passwd = "69524094";
//        String alias = "conname";
//        String keyStoreType = "PKCS12";
//        UpdateDefaultBestpayClient bestpayClient = new UpdateDefaultBestpayClient(userCertificateName, serviceCertificateName, passwd, alias, keyStoreType);
//        BestpayRequest request = new BestpayRequest();
//        request.setEnvEnum(EnvEnum.PRODUCT);
//        request.setCommonParams("{\n" +
//                "\t\"institutionType\":\"MERCHANT\",\n" +
//                "\t\"institutionCode\":\"3178035915054245\"\n" +
//                "}");
//        request.setPath("/uniformReceipt/proCreateOrder");
//        request.setVersion("1.0.3");
//        request.setBizContent("{\n" +
//                "\t\"accessCode\": \"CASHIER\",\n" +
//                "\t\"ccy\": \"156\",\n" +
//                "\t\"goodsInfo\": \"Mi6\",\n" +
//                "\t\"mediumType\": \"WIRELESS\",\n" +
//                "\t\"merchantNo\": \"3178035915054245\",\n" +
//                "\t\"notifyUrl\": \"www.baidu.com\",\n" +
//                "\t\"operator\": \"3178035915054245\",\n" +
//                "\t\"outTradeNo\": \"1441313749871886336_TS1632470979\",\n" +
//                "\t\"requestDate\": \"2021-09-24 16:09:39\",\n" +
//                "\t\"subject\": \"上海益药药业\",\n" +
//                "\t\"tradeAmt\": \"1\",\n" +
//                "\t\"memo\": \"pay_product\",\n" +
//                "\t\"tradeChannel\": \"H5\"\n" +
//                "}");
//
//        try {
//            BestpayResponse response = bestpayClient.execute(request);
//
//            if (response.isSuccess()) {
//                log.info("成功处理......response:"+response);
//                log.info("成功处理......getResult:"+response.getResult());
//            } else {
//                log.info("失败处理......errorCode:"+response.getErrorCode()+" errorMsg:"+ response.getErrorMsg());
//            }
//        } catch (BestpayApiException e) {
//            log.info("异常处理......errorCode:"+e.getErrCode()+" errorMsg:"+e.getErrMsg());
//        }

//        Map<String,String> translateResultData=new HashMap<>();
//        translateResultData.put("institutionType","MERCHANT");
//        translateResultData.put("signType","CA");
//        translateResultData.put("platform","H5_4.0_route");
//        translateResultData.put("tradeType","acquiring");
//        translateResultData.put("merchantFrontUrl","merchantFrontUrl");
//        TreeMap<String, Object> treeMap = new TreeMap(translateResultData);
//
//        log.info(JSON.toJSONString(treeMap));
//        String url=jsonToURL(treeMap);
//        System.out.println(url);

//        System.out.println(URLEncoder.encode("orderId=1441319490192146432_TS&price=0.01"));


        String userCertificateName = "广州上药益药药房有限公司.P12";
        String serviceCertificateName = "天翼电子商务有限公司new.cer";
        String passwd = "69524094";
        String alias = "conname";
        String keyStoreType = "PKCS12";
        UpdateDefaultBestpayClient bestpayClient = new UpdateDefaultBestpayClient(userCertificateName, serviceCertificateName, passwd, alias, keyStoreType);
        BestpayRequest request = new BestpayRequest();
        request.setEnvEnum(EnvEnum.PRODUCT);
        request.setCommonParams("{" +
                "\"institutionType\":\"MERCHANT\"," +
                "\"institutionCode\":\"3178035915054245\"" +
                "}");
        request.setPath("/uniformReceipt/tradeQuery");
        request.setVersion("1.0.3");
        request.setBizContent("{" +
                "\"outTradeNo\":\"1441999725417988096_TS1632634500\"," +
                "\"merchantNo\":\"3178035915054245\"," +
                "\"tradeDate\":\"2021-09-26 09:36:58\"," +
                "}");
        try {
            BestpayResponse response = bestpayClient.execute(request);

            if (response.isSuccess()) {
                log.info("成功处理......response:"+response);
                log.info("成功处理......getResult:"+response.getResult());
            } else {
                log.info("失败处理......errorCode:"+response.getErrorCode()+" errorMsg:"+ response.getErrorMsg());
            }
        } catch (BestpayApiException e) {
            log.info("异常处理......errorCode:"+e.getErrCode()+" errorMsg:"+e.getErrMsg());
        }

    }
}
