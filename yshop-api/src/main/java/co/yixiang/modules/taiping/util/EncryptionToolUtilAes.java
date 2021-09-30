package co.yixiang.modules.taiping.util;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * @author yuexig.li
 * 用于AES对称加密
 * on 2020/09/01
 *
 */
public class EncryptionToolUtilAes {

    private final static Logger logger = LoggerFactory.getLogger(EncryptionToolUtilAes.class);
    //对称加密 密钥 项目部署来源于配置文件
    public static String CipherKey = "LXJK@xsyffwk2020";




    /**
     * 对称加密
     * 加密
     * @param input
     * @param key
     * @return
     */
    public static String encrypt(String input, String key) {
        byte[] crypted;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes("utf-8"));
        } catch (Exception e) {
            logger.warn("AES encrypt error:", e);
            throw new RuntimeException("AES encrypt error:" + e.getMessage());
        }
        return java.util.Base64.getEncoder().encodeToString(crypted);
    }

    /**
     * 对称加密
     * 解密
     * @param input
     * @param key
     * @return
     */
    public static String decrypt(String input, String key) {
        byte[] output;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(java.util.Base64.getDecoder().decode(input));
        } catch (Exception e) {
            logger.warn("AES decrypt error:", e);
            throw new RuntimeException("AES decrypt error:" + e.getMessage());
        }
        try {
            return new String(output,"utf-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.toString());
            logger.warn("charset e.toString():{}", e.toString());
            return null;
        }
    }


    // 测试方法
    public static void main(String[] args) {

        logger.info("-----------------对称加密秘钥：LXJK@tpbbfwk2020 -----------------------------");

        logger.info("加密前 realName:{}","base");
        logger.info("加密后 realName:{}",EncryptionToolUtilAes.encrypt("base", "LXJK@xsyffwk2020"));
       String abc = EncryptionToolUtilAes.decrypt("X/kDDZbgtbcO+z14mvjv3A==", "ZAMB@xsyffwk2021");
        logger.info("abc:{}",abc);
        logger.info("加密前 certiType:{}","o0dKd5YIlA-PB8EBE9VgpkUGetIM");
        logger.info("加密后 certiType:{}",EncryptionToolUtilAes.encrypt("o0dKd5YIlA-PB8EBE9VgpkUGetIM", "ZAMB@xsyffwk2021"));

        logger.info("加密前 certiNo:{}","1626050102322");
        logger.info("加密后 certiNo:{}",EncryptionToolUtilAes.encrypt(String.valueOf(System.currentTimeMillis()), "ZAMB@xsyffwk2021"));

        logger.info("加密前 serviceCardNo:{}","all");
        logger.info("加密后 serviceCardNo:{}",EncryptionToolUtilAes.encrypt("all", CipherKey));

            // 测试环境
            //   https://wbtest.life.cntaiping.com/taiping-lxjk/html/shop/dist/index.html#/tpbbServers?
            //   realName=加密真实姓名&certiType=加密证件类型&certiNo=加密证件号&serviceCardNo=加密展示服务卡&callerName=TPBB

        logger.info("测试环境 URL:{}","https://wbtest.life.cntaiping.com/taiping-lxjk/html/shop/dist/index.html#/tpbbServers?"
                +"realName="+EncryptionToolUtilAes.encrypt("李彩虹", CipherKey)+"&certiType="+EncryptionToolUtilAes.encrypt("1", CipherKey)
        +"&certiNo="+EncryptionToolUtilAes.encrypt("610402197108230028", CipherKey)+"&serviceCardNo=all"
        +"&callerName=TPBBLXJK");
//        测试环境 URL:https://wbtest.life.cntaiping.com/taiping-lxjk/html/shop/dist/index.html#/tpbbServers?
//        realName=S4S059FfYHdmqY4/PKCGvw==&certiType=o/I2WuYJSIC/7D+5cl87bA==&certiNo=R+8T+b6NSSPVKVv3dZvwcK/yZ5YG1Y45NZvXfmfQzGg=&serviceCardNo=all&callerName=TPBBLXJK
        
        // 生产环境
        //   https://ininwb.life.cntaiping.com/taiping-lxjk/html/shop/dist/index.html#/tpbbServers?
        //   realName=加密真实姓名&certiType=加密证件类型&certiNo=加密证件号&serviceCardNo=加密展示服务卡&callerName=TPBB
        logger.info("生产环境 URL:{}","https://ininwb.life.cntaiping.com/taiping-lxjk/html/shop/dist/index.html#/tpbbServers?"
                +"realName="+EncryptionToolUtilAes.encrypt("李彩虹", CipherKey)+"&certiType="+EncryptionToolUtilAes.encrypt("1", CipherKey)
                +"&certiNo="+EncryptionToolUtilAes.encrypt("610402197108230028", CipherKey)+"&serviceCardNo=all"
                +"&callerName=TPBBLXJK");
        //生产环境 URL:https://ininwb.life.cntaiping.com/taiping-lxjk/html/shop/dist/index.html#/tpbbServers?
        // realName=S4S059FfYHdmqY4/PKCGvw==&certiType=o/I2WuYJSIC/7D+5cl87bA==&certiNo=R+8T+b6NSSPVKVv3dZvwcK/yZ5YG1Y45NZvXfmfQzGg=&serviceCardNo=all&callerName=TPBBLXJK

        //        示例代码
        String textContent = "{\"cardNumber\":\"" +"HD200921142655000001\""
                +",\"cardPassWord\":\"" +"111111"+
                "\"}";
        String cardNo = "{\"cardNumber\":\"HD2012A000001\"}";
        textContent = "12345";
        logger.info(textContent);
//        对称加密
        String data = EncryptionToolUtilAes.encrypt(textContent, CipherKey);
        logger.info("加密结果={}",data);
//        对称解密
        String dataDEC = EncryptionToolUtilAes.decrypt("hgp99wQQy3q0XsqXypld5w==", CipherKey);
        logger.info("解密结果====={}",dataDEC);


        String result = EncryptionToolUtilAes.decrypt("iBsO54oMdw6SamNCQ8283NlFv5ezIWlS4e1bhQGLQ50r+nooodeP0DRrts+XNNBfevzI4Cz0r+65dSbBqmXwwo5Z1aMDCK4IJNexM359g7YUBD42NDDH+6EPziBZkmE/F2H6ecMEc4agXvfjI7RmFfFVhIzU1SU5OCyEs0uxYrCdncReeBldnYhyt3rhQlmt", CipherKey);

        logger.info("result==="+result);
//        TaipingCard card = new TaipingCard();
//        card.setCardNumber("1000000023");
//        card.setCardType("85折卡");
//        card.setAgentCate("1");
//        card.setSellChannel("1");
//        card.setOrganID("102");
//        card.setInsertTime("2020-06-29 10:34:00");
//        JSONObject jsonObject = JSONUtil.parseObj(card);
//        String carddata = JSONUtil.toJsonStr(jsonObject);
        String cardDataEnc = EncryptionToolUtilAes.encrypt("1000000023", CipherKey);
        logger.info("乐享卡加密：{}",cardDataEnc);


//        TaipingPayable payable = new TaipingPayable();
//        payable.setAgentCate("1");
//        payable.setCardNumber("1000000023");
//        payable.setCardType("85折卡");
//        payable.setFeeID("100001");
//        payable.setInsertTime("2020-06-29 10:34:00");
//        payable.setNegativeRecord(1);
//        payable.setOrganID("102");
//        payable.setSellChannel("1");
//        JSONObject payable_jsonObject = JSONUtil.parseObj(payable);
//        String payabledata = JSONUtil.toJsonStr(payable_jsonObject);
//        String payableDataEnc = EncryptionToolUtilAes.encrypt(payabledata, CipherKey);
//        logger.info("太平应付记录：{}",payableDataEnc);
    }
}