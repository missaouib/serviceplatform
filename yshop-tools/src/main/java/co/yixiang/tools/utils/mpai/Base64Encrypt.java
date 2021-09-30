package co.yixiang.tools.utils.mpai;



import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Base64加解密工具
 * @author jumping
 * @version 1.0.0
 * @time 2015/07/07
 */
public class Base64Encrypt {
    /***
     * BASE64加密
     * @param s 字符串
     * @return
     */
    public static String getBASE64(String s) {
        if(StringUtils.isEmpty(s)){
            return null;
        }
        Base64.Encoder encoder = Base64.getEncoder();
        try {
            byte [] bytes = s.getBytes("UTF-8");
            return encoder.encodeToString(bytes);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /***
     * BASE64加密
     * @param s  字节
     * @return
     */
	public static String getBASE64ForByte(byte[] s) {
		if (s == null)
            return null;
		return Base64.getEncoder().encodeToString(s);
	}

    /***
     * BASE64解密
     * @param s 字符串
     * @return
     * @throws Exception
     */
	public static byte[] getByteArrFromBase64(String s) throws Exception {
		if (s == null)
			return null;
		return Base64.getDecoder().decode(s);
	}

    /***
     * BASE64解密
     * @param s
     * @return
     */
	public static String getFromBASE64(String s) {
		Base64.Decoder decoder = Base64.getDecoder();
		try {
			byte[] b = decoder.decode(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

    /***
     *  将 BASE64 编码的字符串 s 进行解码
     * @param s  字符串
     * @param charset  编码
     * @return
     */
	public static String getFromBASE64(String s, String charset) {
        Base64.Decoder decoder = Base64.getDecoder();
		try {
			byte[] b = decoder.decode(s);
			return new String(b, charset);
		} catch (Exception e) {
			return null;
		}
	}
}
