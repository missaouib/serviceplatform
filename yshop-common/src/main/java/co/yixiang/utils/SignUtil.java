package co.yixiang.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.apache.commons.codec.digest.DigestUtils;

public class SignUtil {
    public static String getSign(String nonce,String timestamp,String token) {
        String sign = "";
        try {

            String str = timestamp + nonce + token;
            sign = Hashing.sha1().hashString(str, Charsets.UTF_8)
                    .toString().toUpperCase();

        }catch (Exception e) {

        }


        return sign;
    }

    public static String getSha1Signature(String timestamp, String nonce, String secureKey) {
        String str = timestamp.toString() + nonce + secureKey;
        String signature = DigestUtils.sha1Hex(str);
        return signature;
    }
}
