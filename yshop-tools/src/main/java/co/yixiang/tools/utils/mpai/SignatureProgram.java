package co.yixiang.tools.utils.mpai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.bestpay.api.exception.BestpayApiException;
import com.bestpay.api.util.InspectionSign;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @Author: liujianqun
 * @Description:
 * @Date: 2017/9/8
 * @Moidfy by:zhouxirui
 */
public class SignatureProgram {

    public static void main(String[] args) throws GeneralSecurityException, IOException {

        //--------------生成签名demo------------
//        InputStream resourceAsStream = SignatureProgram.class.getClassLoader().getResourceAsStream("广州上药益药药房有限公司.P12");
//        String passwd = "69524094";
//        String alias = "conname";
//        String keyStoreType = "PKCS12";
//
//        KeyCertInfo keyCertInfo = CryptoUtil.fileStreamToKeyCertInfo(resourceAsStream,passwd,keyStoreType,alias);
//
//        //请求参数
//        Map<String,String> translateResultData=new HashMap<>();
//        translateResultData.put("institutionCode","3178000003978343");
//        translateResultData.put("merchantNo","8630029000159147");
//        translateResultData.put("outTradeNo","201709191472852012807774852354");
//        translateResultData.put("tradeAmt","11");
//        translateResultData.put("subject","subject");
//        translateResultData.put("productCode","68800020109");
//        translateResultData.put("buyerLoginNo","18691832778");
//        translateResultData.put("ccy","156");
//        translateResultData.put("requestDate","2017-09-19 23:59:15");
//        translateResultData.put("operator","operator");
//        translateResultData.put("ledgerAccount","ledgerAccount");
//        translateResultData.put("notifyUrl","notifyUrl");
//        translateResultData.put("timeOut","0");
//        translateResultData.put("storeCode","storeCode");
//        translateResultData.put("storeName","storeName");
//        translateResultData.put("goodsInfo","goodsInfo");
//        translateResultData.put("remark","remark");
//
//        BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
//        Signature signature = Signature.getInstance("SHA256withRSA", bouncyCastleProvider);
//        Map<String, String> map = (Map)JSONObject.parseObject(JSON.toJSONString(translateResultData), Map.class);
//        String content = com.bestpay.api.util.SignatureUtil.assembelSignaturingData(map);
//        String sign = com.bestpay.api.util.SignatureUtil.sign(signature, content, (PrivateKey)keyCertInfo.getPrivateKey());
//        map.put("sign", sign);
//        System.out.println(JSON.toJSONString(map));
        //--------------生成签名demo------------

        //--------------验证翼支付签名demo------------

        String jsonS="{\"buyerContractNo\":\"F100350109B1B22877E9AD5BF09529B78D7F2208B834B12748316F6E19D428FE\",\"ccy\":\"156\",\"discountAmt\":\"0\",\"goodsInfo\":\"Mi6\",\"institutionCode\":\"3178035915054245\",\"memo\":\"pay_product\",\"merchantNo\":\"3178035915054245\",\"originalTradeNo\":null,\"outTradeNo\":\"1441939498928701440_TS1632620141\",\"payAmt\":\"1\",\"resultCode\":null,\"resultMsg\":null,\"sign\":\"Lnx53G7IW6EnKz5Ghdtfyq1MdktehiZ1Xq4O8eSR7jBKeAoRSsS2/qX8JKO/iZWMPuMWTNCi/YQRhtpFqDCWF+6n7FAIpcx8MdOTp+1w6ScTWkpwL57XFM6x3VvqAmSaqCHPnzGM9/+DfFO9FpOoXidZQhZceolWRFfrdu2avQ4=\",\"tradeAmt\":\"1\",\"tradeFinishedDate\":\"2021-09-26 09:36:47\",\"tradeNo\":\"20210926100000210002110290121099\",\"tradeReason\":null,\"tradeStatus\":\"SUCCESS\",\"tradeType\":\"REAL_TIME_PRO\"}";
        Map mapTypes = JSON.parseObject(jsonS);
        try {
            boolean isOk =  com.bestpay.api.util.SignatureUtil.checkSign(mapTypes, "天翼电子商务有限公司new.cer");
            System.out.println(isOk);
        } catch (BestpayApiException e) {
            e.printStackTrace();
        }
        String checksign = String.valueOf(mapTypes.get("sign"));
        String checkContent = assembelSignaturingData(mapTypes);
        System.out.println(checkContent);
        InputStream pubStream = SignatureProgram.class.getClassLoader().getResourceAsStream("天翼电子商务有限公司new.cer");
        byte pubByte[] = new byte[2048] ;
        pubStream.read(pubByte);
        pubStream.close();
        X509Certificate x509Certificate = CryptoUtil.base64StrToCert(Base64Encrypt.getBASE64ForByte(pubByte));
        BouncyCastleProvider bouncyCastleProvider2 = new BouncyCastleProvider();
        Signature signatureCheck = Signature.getInstance("SHA1withRSA",bouncyCastleProvider2);
        boolean isOk = SignatureUtil.verify(signatureCheck,checkContent,checksign,x509Certificate.getPublicKey());

        System.out.println(isOk);
        //--------------验证翼支付签名demo------------
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
}
