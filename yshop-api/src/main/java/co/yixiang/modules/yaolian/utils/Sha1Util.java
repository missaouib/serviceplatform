package co.yixiang.modules.yaolian.utils;

import co.yixiang.utils.DateUtils;
import reactor.core.Exceptions;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class Sha1Util {

    public static String getSha1(String str) throws Exception{
        if (null == str || 0 == str.length()){
            return null;
        }
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSign(String nonce,String timestamp,String token) {
        String sign = "";
        try {
        String[] data = {nonce, String.valueOf(timestamp), token};
        Arrays.sort(data);
        String sign_origin = "";
        for(int i=0;i<data.length;i++) {
            sign_origin += data[i];
        }


            sign = getSha1(sign_origin);
        }catch (Exception e) {

        }


        return sign;
    }

    public static void main(String[] args) {
        try {
        String nonce = "7896B8FDBE9527DFE88A6DF3B088569D";
        long timestamp = System.currentTimeMillis();
        System.out.println(String.valueOf(timestamp/1000));
        String token = "uniondrug";
        String[] data = {nonce, String.valueOf(timestamp/1000), token};
        Arrays.sort(data);
        String sign_origin = "";
        for(int i=0;i<data.length;i++) {
            sign_origin += data[i];
        }
        String sign = null;
        sign = getSha1(sign_origin);
        System.out.println(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
