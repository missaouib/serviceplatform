package co.yixiang.modules.yiyaobao.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 *     加密工具类
 * </p>
 * User: <a href="mailto:hzxia@qq.com">Jacky</a> Date: 2009-11-21 Time:
 * 19:24:48 Version: 1.0
 */
@SuppressWarnings("unused")
public class CryptUtils {
    protected static Logger log = LoggerFactory.getLogger(CryptUtils.class);

	private static final int startKey = 956;
	private static final int multKey = 37269;
	private static final int addKey = 28820;

    public static final String DEFAULT_CHARSET_ENCODING = "UTF-8"; // 默认字符编码UTF-8

	public CryptUtils() {
	}

	private static String encrypt(String inString, int startKey, int multKey,
			int addKey) {
		String result = "";
		for (int i = 0; i < inString.length(); i++) {
			result = result
					+ (char) ((inString.charAt(i) ^ startKey >> 8) & 0xff);
			startKey = (result.charAt(i) + startKey) * multKey + addKey;
		}

		return result;
	}

	private static String decrypt(String inString, int startKey, int multKey,
			int addKey) {
		String result = "";
		for (int i = 0; i < inString.length(); i++) {
			result = result
					+ (char) ((inString.charAt(i) ^ startKey >> 8) & 0xff);
			startKey = (inString.charAt(i) + startKey) * multKey + addKey;
		}

		return result;
	}

	private static String cl_inttostr(int int1, int len) {
		String s = "" + int1;
		String s2 = "";
		int length = s.length();
		if (length >= len)
			return s;
		for (int i = 0; i < len - length; i++)
			s2 = s2 + "0";

		return s2 + int1;
	}

	private static String cl_chartobytestr(String s) {
		String result = "";
		for (int i = 0; i < s.length(); i++)
			result = result + cl_inttostr(s.charAt(i), 3);

		return result;
	}

	private static String cl_bytetocharstr(String s) {
		int i = 1;
		String result = "";
		if (s.length() % 3 == 0)
			for (; i < s.length(); i += 3)
				result = result
						+ (char) (Integer.parseInt(s.substring(i - 1, i + 2)) & 0xff);

		return result;
	}

	/**
	 * 进行数据加密
	 *
	 * @param s
	 * @return
	 */
	public static String encrypt(String s) {
		Calendar calendar = Calendar.getInstance();
		int years = calendar.get(1);
		int months = calendar.get(2);
		int days = calendar.get(5);
		int hours = calendar.get(11);
		int mins = calendar.get(12);
		int secs = calendar.get(13);
		int msec = calendar.get(14);
		int cl_StartKey = msec;
		if (cl_StartKey < 256)
			cl_StartKey += 256;
		int cl_Multkey = ((years - 1900) * 12 + months) * 30 + days
				+ cl_StartKey * 10 + cl_StartKey;
		int cl_AddKey = (23 * hours + mins) * 60 + secs + cl_StartKey * 10
				+ cl_StartKey;
		String result = cl_chartobytestr(encrypt(cl_inttostr(cl_StartKey, 3),
				956, 37269, 28820))
				+ cl_chartobytestr(encrypt(cl_inttostr(cl_Multkey, 5), 956,
						37269, 28820))
				+ cl_chartobytestr(encrypt(cl_inttostr(cl_AddKey, 5), 956,
						37269, 28820))
				+ cl_chartobytestr(encrypt(s, cl_StartKey, cl_Multkey,
						cl_AddKey));
		return result;
	}

	/**
	 * 进行数据解密
	 *
	 * @param s
	 * @return
	 */
	public static String decrypt(String s) throws Exception{

		try {
			if (s.length() < 9) {
				return s;
			} else {
				int cl_StartKey = Integer.parseInt(decrypt(cl_bytetocharstr(s
						.substring(0, 9)), 956, 37269, 28820));
				int cl_MultKey = Integer.parseInt(decrypt(cl_bytetocharstr(s
						.substring(9, 24)), 956, 37269, 28820));
				int cl_AddKey = Integer.parseInt(decrypt(cl_bytetocharstr(s
						.substring(24, 39)), 956, 37269, 28820));
				String result = decrypt(cl_bytetocharstr(s
						.substring(39, s.length())), cl_StartKey, cl_MultKey,
						cl_AddKey);
				return result;
			}
		} catch (Exception e) {
			log.error("error", e);
			throw e;
		}
	}

    /**
     *
     * @return DES算法密钥
     */
    public static byte[] generateKey() {
        try {

            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // 生成一个DES算法的KeyGenerator对象
            KeyGenerator kg = KeyGenerator.getInstance("DES");
            kg.init(sr);

            // 生成密钥
            SecretKey secretKey = kg.generateKey();

            // 获取密钥数据
            byte[] key = secretKey.getEncoded();

            return key;
        } catch (NoSuchAlgorithmException e) {
            log.error("DES算法，生成密钥出错!", e);
        }

        return null;
    }

    /**
     * DES ECB模式加密函数
     *
     * @param data
     *            加密数据
     * @param key
     *            密钥
     * @return 返回加密后的数据
     */
    public static byte[] encrypt(byte[] data, byte[] key) {

        try {

            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // 从原始密钥数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DES in ECB mode
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

            // 执行加密操作
            byte encryptedData[] = cipher.doFinal(data);

            return encryptedData;
        } catch (Exception e) {
            log.error("DES算法，加密数据出错!", e);
        }

        return null;
    }

    /**
     * 对字符串进行DES加密
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] encrypt(String data, String key) {
        return encrypt(data.getBytes(), key.getBytes());
    }

    /**
     * 返回经过BASE64编码后的加密字符串
     *
     * @param data
     * @param key
     * @return
     */
    public static String encryptString(byte[] data, byte[] key) {
        byte[] bytes = encrypt(data, key);

        BASE64Encoder encoder = new BASE64Encoder();

        return encoder.encode(bytes);
    }

    /**
     * 对字符串进行DES加密
     *
     * @param data
     * @param key
     * @return
     */
    public static String encryptString(String data, String key) {
        try {
            byte[] keys = key.getBytes(DEFAULT_CHARSET_ENCODING);

            return encryptString(data.getBytes(), keys);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * DES ECB模式解密函数
     *
     * @param data
     *            解密数据
     * @param key
     *            密钥
     * @return 返回解密后的数据
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // byte rawKeyData[] = /* 用某种方法获取原始密匙数据 */;

            // 从原始密匙数据创建一个DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DES in ECB mode
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);

            // 正式执行解密操作
            byte decryptedData[] = cipher.doFinal(data);

            return decryptedData;
        } catch (Exception e) {
            log.error("DES算法，解密出错!", e);
        }

        return null;
    }

    /**
     * 解密字符串
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] decrypt(String data, byte[] key) {
        BASE64Decoder decoder = new BASE64Decoder();

        try {
            byte[] bytes = decoder.decodeBuffer(data);

            return decrypt(bytes, key);
        } catch (IOException e) {
            log.error("数据解密出错!", e);
        }

        return null;
    }

    /**
     * 对字符串进行解密
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] decrypt(String data, String key) {
        return decrypt(data, key.getBytes());
    }

    /**
     * 数据解密
     *
     * @param data
     * @param key
     * @return
     */
    public static String decryptString(byte[] data, byte[] key) {
        return new String(decrypt(data, key));
    }

    /**
     * 对字符串进行解密
     *
     * @param data
     * @param key
     * @return
     */
    public static String decryptString(byte[] data, String key) {
        return new String(decrypt(data, key.getBytes()));
    }

    /**
     * 解密字符串
     *
     * @param data
     * @param key
     * @return
     */
    public static String decryptString(String data, byte[] key) {
        return new String(decrypt(data, key));
    }

    /**
     * 解密字符串
     *
     * @param data
     * @param key
     * @return
     */
    public static String decryptString(String data, String key) {
        return decryptString(data, key, DEFAULT_CHARSET_ENCODING);
    }

    /**
     * 解密字符串
     *
     * @param data
     * @param key
     * @param charsetEncoding
     * @return
     */
    public static String decryptString(String data, String key, String charsetEncoding) {
        try {
            return new String(decrypt(data, key), charsetEncoding);
        } catch (Exception e) {
            log.error("error", e);
            return new String(decrypt(data, key));
        }
    }

    /**
     * DES CBC模式加密函数
     *
     * @param data
     *            加密数据
     * @param key
     *            密钥
     * @return 返回加密后的数据
     */
    public static byte[] CBCEncrypt(byte[] data, byte[] key, byte[] iv) {

        try {
            // 从原始密钥数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            // 若采用NoPadding模式，data长度必须是8的倍数
            // Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");

            // 用密匙初始化Cipher对象
            IvParameterSpec param = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, param);

            // 执行加密操作
            byte encryptedData[] = cipher.doFinal(data);

            return encryptedData;
        } catch (Exception e) {
            log.error("DES算法，加密数据出错!", e);
        }

        return null;
    }

    /**
     * 返回经过BASE64编码后的加密字符串
     *
     * @param data
     * @param key
     * @return
     */
    public static String CBCEncryptString(byte[] data, byte[] key, byte[] iv) {
        byte[] bytes = CBCEncrypt(data, key, iv);

        BASE64Encoder encoder = new BASE64Encoder();

        return encoder.encode(bytes);
    }

    /**
     * DES CBC模式解密函数
     *
     * @param data
     *            解密数据
     * @param key
     *            密钥
     * @return 返回解密后的数据
     */
    public static byte[] CBCDecrypt(byte[] data, byte[] key, byte[] iv) {
        try {
            // 从原始密匙数据创建一个DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DES in CBC mode
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            // 若采用NoPadding模式，data长度必须是8的倍数
            // Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");

            // 用密匙初始化Cipher对象
            IvParameterSpec param = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, param);

            // 正式执行解密操作
            byte decryptedData[] = cipher.doFinal(data);

            return decryptedData;
        } catch (Exception e) {
            log.error("DES算法，解密出错!", e);
        }

        return null;
    }

    /**
     * 解密字符串
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] CBCDecrypt(String data, byte[] key, byte[] iv) {
        BASE64Decoder decoder = new BASE64Decoder();

        try {
            byte[] bytes = decoder.decodeBuffer(data);

            return CBCDecrypt(bytes, key, iv);
        } catch (IOException e) {
            log.error("数据解密出错!", e);
        }

        return null;
    }

    /**
     * 数据解密
     *
     * @param data
     * @param key
     * @return
     */
    public static String CBCDecryptString(byte[] data, byte[] key, byte[] iv) {
        return new String(CBCDecrypt(data, key, iv));
    }

    /**
     * 解密字符串
     *
     * @param data
     * @param key
     * @return
     */
    public static String CBCDecryptString(String data, byte[] key, byte[] iv) {
        return new String(CBCDecrypt(data, key, iv));
    }

    /**
     * MD5加密算法
     *
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String encoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();

        //加密后的字符串
        String ret = base64en.encode(md5.digest(str.getBytes("utf-8")));

        return ret;
    }

    public static String rstr2hex(String input) {
        String hexTab = "0123456789abcdef";
        String output = "";
        char x;

        for (int i = 0; i < input.length(); i++) {
            x = input.charAt(i);
            output += hexTab.charAt((x >>> 4) & 0x0F) +
                    hexTab.charAt(x & 0x0F);
        }

        return output;
    }



    public static void main(String args[]) throws Exception {
		for (int i = 0; i < 10; i++) {
			String s = "yiyao" + i;
			String ens = encrypt(s);
			String des = decrypt(ens);
			System.out.println("original string:" + s);
			System.out.println("encrypt string:" + ens);
			System.out.println("decrypt string:" + des);
			if (s.equals(des))
				System.out.println("ok!");
			else
				System.out.println("fail!");
		}
        System.out.println(encrypt("123"));

        String key = "11111111";
        byte[] iv = "22222222".getBytes();

        String str = "{'mobile':'18616678007','password':'aaaaaa'}";
        byte[] data = CryptUtils.encrypt(str, key);
        BASE64Encoder encoder = new BASE64Encoder();
        BASE64Decoder decoder = new BASE64Decoder();
        String dataString = encoder.encode(data);

        System.out.println("EBC mode:\n" + dataString);
        System.out.println("EBC String mode:" + CryptUtils.encryptString(str.getBytes(), key.getBytes()));

        System.out.println("EBC mode decode:" + new String(
                CryptUtils.decrypt(decoder.decodeBuffer(dataString), key.getBytes())));

        System.out.println("EBC String mode:" + CryptUtils.decryptString(dataString, key));

        System.out.print("CBC mode:");
        data = CryptUtils.CBCEncrypt("cbc mode test".getBytes(), key.getBytes(), iv);
        System.out.println(new String(CryptUtils.CBCDecrypt(data, key.getBytes(), iv)));

        System.out.println("EBC String mode:" + CryptUtils.decryptString("1Iq9jXPNKdO4N/DPDfPUZw==", "11111111"));

        String testData = CryptUtils.encryptString(str, "b2ctestkey");
        System.out.println("测试数据:" + testData);

        System.out.println("测试数据:" + CryptUtils.decryptString("2rxooReINySTE5y/sfH/VnI3zCfejgFjFw+GO7+kAX8mBr87tZOLKkTUhMEdBMVY", "b2ctestkey"));
        System.out.println("测试数据:" + CryptUtils.decryptString("/jK3Nx70oGu0MEPT0h1/3HKHZeIJcsjD9do33YmviX0=", "mobiletest"));

        System.out.println("UserSession:" + CryptUtils.encryptString("18616678007@@2@@" + (new Date()).getTime(), "mobiletest"));
//        String address="[{'createTime':null,'fullAddress':null,'updateTime':null,'provinceCode':null,'zipcode':null,'tel':null,'cityCode':null,'id':'1','createUser':null,'districtCode':null,'address':null,'isDefault':null,'userId':'1','name':'ceshi','updateUser':null,'mobile':null},{'createTime':null,'fullAddress':null,'updateTime':1436063193000,'provinceCode':'11','zipcode':'2222','tel':'22','cityCode':'1102','id':'2','createUser':null,'districtCode':'110228','address':'详细地址1111111','isDefault':1,'userId':'2','name':'ceshi2','updateUser':'admin','mobile':'22'},{'createTime':null,'fullAddress':'地址','updateTime':1436084938000,'provinceCode':'11','zipcode':'021','tel':'133','cityCode':'1102','id':'3','createUser':null,'districtCode':'110229','address':'123456','isDefault':1,'userId':'3','name':'ceshi123','updateUser':'admin','mobile':'133'},{'createTime':1436088076000,'fullAddress':' ','updateTime':1436025600000,'provinceCode':'11','zipcode':'021','tel':'133','cityCode':'1102','id':'4','createUser':'admin','districtCode':'110229','address':'123456','isDefault':1,'userId':'3','name':'ceshi12345','updateUser':'admin','mobile':'133'},{'createTime':1436088184000,'fullAddress':' ','updateTime':1436089920000,'provinceCode':'32','zipcode':'123','tel':'1234','cityCode':'3201','id':'5','createUser':'admin','districtCode':'320101','address':'123','isDefault':0,'userId':'3','name':'c2','updateUser':'admin','mobile':'1234'},{'createTime':1436161701000,'fullAddress':' ','updateTime':null,'provinceCode':' ','zipcode':' ','tel':' ','cityCode':' ','id':'6','createUser':'admin','districtCode':' ','address':' ','isDefault':0,'userId':'1','name':'dd','updateUser':' ','mobile':' '},{'createTime':1436336890000,'fullAddress':' ','updateTime':1436025600000,'provinceCode':'11','zipcode':'021','tel':'133','cityCode':'1102','id':'7','createUser':'admin','districtCode':'110229','address':'123456','isDefault':1,'userId':'3','name':'ceshi123456','updateUser':'admin','mobile':'133'}]";
//        System.out.println("address"+address);
//        String addressEncode=CryptUtils.encryptString(address,"b2ctestkey");
//        System.out.println("addressEncode:"+addressEncode);
        String addressEncode="l7o/lyZDVpOZYPBrjp2xoQdNU8ddGCHWOi1RC1y94MgKQ29sslEY3d1TdT90Kof1nMTHxcN/ZGLG1dkV9pWQWr0K/fDwDv+gVS1uksyk57ARQlcnL2Bj+l58paaKrKO1CkNvbLJRGN3Zmj+cU/ciSQpDb2yyURjdTQ07RNL145eazft2k2+ncCvLbSnLGzCY40AKwopAn6I2ge82YklIDw0u8uKLOMD31jbXyCPXx/lu2jT3w88cif7DgdSs6cUYeQV5wwLztxY+NWh0BEDEhrcn7eu3K2ryF2QVPyWZzkr3gTCt6gSQ82HpZ+2NYAwmlVmaGeI6kbIBbdWr/zX1C6sJv6v6SYjClpG9LMMUfAIPJeRZptOpVXZPLZ4/4W0xL8Ydf5m0vDgkMCb2ifvy2glxdDLS6bB7r/5VFhGDue+y+LUB4ePI6sZxKbgPyxvk8f9e9YEVkB6pAggd9Fn4YR7jgFRklX58Ap+D9Bv8jRUCsnQIBjHO4OnmNTkPNI5QuCc8jwRR7YF3xzuGjcTuFBdshbeGnRGX8IzwEIF4IcTDHV26OtKQNVsug0nKnP0j2jYQ/Oe8rk9Q8rivetR3W7vCdTX082ZCHszzT5GWd8vczRrKMFRrBUZsFiqOvtY73RAdog45m1eeLl2YzZEBRYUS7q5P6WRbF6wuo/sx4AWrzJAS8h1DnHJ+gkXu20QlB2vvIH4xok3ljoSf4+S6E9tVdW0wnOtBnWFl8rX3G74AleaFhV+g7AaRo7BAj1yZjwZ4ZgPwbh0p0kP8sSm9IveBMK3qBJDzISmpBNdoZh9qYc5CpPcJcC/MubMpsqpoWMc+RywuARIAprxUKdB6sj8IctR2Zz7gy3gFDd+oGZcCn4P0G/yNFeQ0c7XHCZFHAXAKia8sXnMElwUr9j+MbrPwC1aAWflVms37dpNvp3Ary20pyxswmONACsKKQJ+iNoHvNmJJSA8z+uUI85DhjQsFJlg3CDoJjUV+7Xrl+BCGa889XnHtzP7DgdSs6cUYYiqTEFNjCwHvfZDJsYVmgpQ92Vr0MiQnwIH3tLsfkMD3gTCt6gSQ8/Nl3z/ULpZVR3xX6xjV7zBVfYRprYDB0GpnPQhxvWtpNGtmaiQiSbNbJvBFOOgjEowjtXNLHcQYhEC9KvMgfeePBnhmA/BuHbQAuo7HusAvgtHrG1pnFtcRMoA+hZMI2YvwWR8ddb/OPljmz8z6VKoOC95f3mz334REe9ByfVjqjZdLJtxNb7BV3r5uIIQGU+efFGdhAZsd0oYu3JHh9cQIlY0e2yFLH79+yO94h3m3rHGazUMiFnan2YFmw082Z9otjzGQ4kgIKL207Aqi7Tq35uSxrm3N5DECfE7bn3Wd6eW6XHC9y+9ak0Dg+q5EkyUPWRj267rPwv21ckRS5LBl/aGWoL4/JUX41fY7izXgOl9n5I9IT6oUk2wyjOkBIfeBMK3qBJDz82XfP9QullVHfFfrGNXvMFV9hGmtgMHQamc9CHG9a2k0a2ZqJCJJs1sm8EU46CMS+CpjtTPRpLCEQL0q8yB9548GeGYD8G4dtAC6jse6wC+C0esbWmcW1xH/Ane79tiuPqnfOqQzpDk+WObPzPpUqg4L3l/ebPffsM8O+XudVYU58KWwaGgZWrN/BbpX5PMsWl38Jah6ooOxf4vjwiIjo4JdwhPgwPkGv163SwKAmbDy0297695xRnJ+gkXu20QlB2vvIH4xok3DHV26OtKQNVsug0nKnP0jnpgRNmusb1NQ8rivetR3W44pIZdTwVGdRmwWKo6+1jt7vklI32TtDZ4uXZjNkQFF66GClXAULrFAMgrqZ7nR503nneEG06Xwedsc7HgOVDU4PJEpyXJx8HJjJKXSHUjpT0xn1hvmiTx3vyg+1yCipFASJTN3O1SzdcDUJx/D7eYv2LcteskqadY218gj18f5RsccAlcJxzyWkb0swxR8AhwXoaiaQi6Z5yBC8cCdMdZc6X/cHF+dLj8IctR2Zz7gEpN+CK+NKZsKXIKtnEPRnbF/i+PCIiOjAvUE/oCNt+9GKRCvYiStjfgYxMz6FxKvvAuwEpmf9tQSdyU2u7fNFtKTu6g7/yeSXOl/3BxfnS5Q8rivetR3W7DxH4qZXsteUGH79Oe2cdoKzWQ0VOmM1BiHCqq/r83tCRk0hmZiKQDvFIP4TPPNs83Mrlfi8lV2aS6AOF1asWZVfYRprYDB0C2sshQ2hnyZX0qDlXdn2R7vVHkQ+M/MOVmAi6Ru+74raqTOMYE9k3Cok5dZey9S+NW7drPszNsc3f7tP8U49pnFT5KG9h5oFiE5roDxgwK+5yBC8cCdMdY7mu+wqMwZguIm0SfEFY9Spz9SwbWb8OFaZMGjiTVaStmfxH/Q2QoyGio+VIBS2/u6MIhbFBZWEgaI9oY3f3Z0+BjEzPoXEq+8C7ASmZ/21BJ3JTa7t80W0pO7qDv/J5I7mu+wqMwZggaNyFVnXEoT1jbXyCPXx/lgIQIDqfQ8m0ZsFiqOvtY73RAdog45m1eeLl2YzZEBReuhgpVwFC6xF6wuo/sx4AUqkQA8AA9pgyQwJvaJ+/LavAuwEpmf9tSZKJj6qg0wRwgW7+/mmdzoqqbR2VbS5T4=";
        String addressDecode=CryptUtils.decryptString(addressEncode, "b2ctestkey");
        System.out.println("addressDecode:"+addressDecode);
        System.out.println("encode{}:" + URLEncoder.encode(CryptUtils.encryptString("{}", "b2ctestkey")));
        System.out.println("{}:"+ CryptUtils.encryptString("{}", "b2ctestkey"));

        String ret = "VAp1ixlYT92E6vErKD+XnqZoc9doJnIJWD8lUZUOPfE=";

        ret = URLDecoder.decode(URLEncoder.encode(ret));

        System.out.println("aaa====="+CryptUtils.decryptString(ret, "b2ctestkey", "GBK"));


        String token2 = "Xw2e9LThOfqhImVj5zGJUtl5PI48HzdnU7R4cJNcPLAgbpvpD+snWg+2e2C/u/dqiM4Ot4zVRKq7\r\n2g3EBHfeHLgP0rvTWKlA";

        token2 = URLDecoder.decode(URLEncoder.encode(token2));

        System.out.println(CryptUtils.decryptString(token2, "mobiletest", "GBK"));


        System.out.println("加密："+ CryptUtils.encryptString("{\"patientName\":\"visa\",\"patientMobile\":\"18017890127\",\"codeType\":\"h5\"}", "b2ctestkey"));
        System.out.println("解密:"+
        CryptUtils.decryptString("PlxTflyQlVE=", "b2ctestkey", "GBK"));
    }
}
