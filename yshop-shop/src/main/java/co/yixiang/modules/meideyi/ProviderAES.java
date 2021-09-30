package co.yixiang.modules.meideyi;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * 对Provider开放接口的AES加密解密
 * 
 * @author MIC美德医中国
 *
 */
public class ProviderAES {
    private static final String AES = "AES"; //加密方式
    private static final String STR_IV = "sdfoisvnosijfslx";//iv向量，长度为16字节，也就是128bit。不区分供应商。

    public static final String SEED = "YY2021.%&$d"; //密钥种子。实际从配置文件读取，为每个供应商单独配置。
    public static final Long MAX_TIME = 3 * 60 * 1000L; //aes密文有效时长，3分钟。实际从配置文件读取，为每个供应商单独配置。

    public static void main(String[] args) {
        long now = System.currentTimeMillis();
        String data = String.valueOf(now);
        //data = "1615527348927";
        String encrypted = encrypt(data, SEED);
        //encrypted = "aN3ToaNgK+c5XY6k64hpM0n3jjzMXCW0ymR/AW1FFYpgu3c3jL5lYYs=";
        String decrypted = decrypt(encrypted, SEED);
        System.out.println("密文：" + encrypted);
        System.out.println("明文：" + decrypted);
    }

    /**
     * 加密
     * 
     * @param data 原文
     * @param seed  随机密钥的种子
     * @return
     */
    public static String encrypt(String data, String seed) {
        if (data == null || seed == null) {
            return null;
        }
        try {
            byte[] dataByte = data.getBytes("UTF-8");
            KeyGenerator keygen = KeyGenerator.getInstance(AES);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed.getBytes("UTF-8"));
            keygen.init(128, random);
            SecretKey sKey = keygen.generateKey();
            byte[] encodedKey = sKey.getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(encodedKey, AES);
            //System.out.println("加密密钥:" + Base64.encodeBase64String(encodedKey));
            //CFB加密模式，填充方式选择NoPadding
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            //添加向量ivspec
            IvParameterSpec ivspec = new IvParameterSpec(STR_IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivspec);
            //为了避免java不同版本下Base64的差异，这里用Apache的Base64。
            //为了在url中传值，将+和/替换成-和_，同时删除结尾的=，这里用URLSafe版的编码实现。
            return Base64.encodeBase64URLSafeString(cipher.doFinal(dataByte));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param data 密文
     * @param seed 随机密钥的种子
     * @return
     */
    public static String decrypt(String data, String seed) {
        if (data == null || seed == null) {
            return null;
        }
        try {
            byte[] dataByte = Base64.decodeBase64((data.getBytes()));
            KeyGenerator keygen = KeyGenerator.getInstance(AES);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed.getBytes("UTF-8"));
            keygen.init(128, random);
            SecretKey sKey = keygen.generateKey();
            byte[] encodedKey = sKey.getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(encodedKey, AES);
            //System.out.println("解密密钥:" + Base64.encodeBase64String(encodedKey));
            //CFB加密模式，填充方式选择NoPadding
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            //添加向量ivspec
            IvParameterSpec ivspec = new IvParameterSpec(STR_IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivspec);
            return new String(cipher.doFinal(dataByte), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
