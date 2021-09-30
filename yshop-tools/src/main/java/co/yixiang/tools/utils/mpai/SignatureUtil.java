package co.yixiang.tools.utils.mpai;

import org.apache.commons.codec.binary.Base64;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author: zhoujinlai
 * @Description:
 * @Date: 2021-07-29
 * @Moidfy by:
 */
public class SignatureUtil {

    private static final String charcterCode = "UTF-8";
    /**
     * SHA256WithRSA签名
     * @param data
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    public static String sign(Signature signature,String data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
            SignatureException, UnsupportedEncodingException {

        signature.initSign(privateKey);

        signature.update(data.getBytes(charcterCode));

        byte[] signBytes =  signature.sign();
        return  new String (org.bouncycastle.util.encoders.Base64.encode(signBytes));
    }

    public static boolean verify(Signature signature,String data, String sign, PublicKey publicKey) throws UnsupportedEncodingException {
        if(data == null || sign == null || publicKey == null){
            return false;
        }
        byte[] signBytes = org.bouncycastle.util.encoders.Base64.decode(sign.getBytes(charcterCode));
        try {
            signature.initVerify(publicKey);
            signature.update(data.getBytes(charcterCode));
            return signature.verify(signBytes);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 二进制数据编码为BASE64字符串
     * @param bytes
     * @return
     */
    public static String encodeBase64(byte[] bytes){
        return new String(Base64.encodeBase64(bytes));
    }

    /**
     * BASE64解码
     * @param bytes
     * @return
     */
    public static byte[] decodeBase64(byte[] bytes) {
        byte[] result = null;
        try {
            result = Base64.decodeBase64(bytes);
        } catch (Exception e) {
            return null;
        }
        return result;
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
