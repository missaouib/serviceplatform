package co.yixiang.tools.utils.mpai;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Author: liujianqun
 * @Description:
 * @Date: 2017/8/7
 * @Moidfy by:
 */

public class CryptoUtil {

    private static BouncyCastleProvider bouncyCastleProvider;
    private static final Object LOCK = new Object();
    private static CertificateFactory factory = initFactory();

    private static CertificateFactory initFactory() {
        try {
            return CertificateFactory.getInstance("X.509");
        } catch (CertificateException var1) {
            throw new RuntimeException(var1);
        }
    }
    /**
     * @desc SHA256 加密公共类
     * @author liujianqun
     * @method getSHA256
     * @param  str 加密字符串
     * @return java.lang.String
     */
 public static String getSHA256(String str,String algorithm){
    MessageDigest messageDigest;
    String encdeStr = "";
    try {
        messageDigest = MessageDigest.getInstance(algorithm);
        byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
        encdeStr = Hex.encodeHexString(hash);
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }
        return encdeStr;
    }

    public static String getSHA256Com(String str)
    {
        MessageDigest md = null;
        String result = "";
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes("utf-8"));
            byte[] digest = md.digest();
            result=new String (Base64.encode(digest));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static byte[] getSHA256Byte(String str,String algorithm){
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static BouncyCastleProvider getInstanceProvider() {
        if(bouncyCastleProvider == null) {
            Object var0 = LOCK;
            synchronized(LOCK) {
                if(bouncyCastleProvider == null) {
                    bouncyCastleProvider = new BouncyCastleProvider();
                }
            }
        }

        return bouncyCastleProvider;
    }

    public static byte[] enDecryptByRsa(byte[] data, Key key, int mode) throws GeneralSecurityException {
        BouncyCastleProvider provider = getInstanceProvider();
        ByteArrayOutputStream outputStream = null;

        byte[] var21;
        try {
            outputStream = new ByteArrayOutputStream();
            Cipher cp = Cipher.getInstance("RSA/ECB/PKCS1Padding", provider);
            cp.init(mode, key);
            int blockSize = cp.getBlockSize();
            int blocksNum = (int)Math.ceil((double)data.length / (double)blockSize);
            int calcSize = blockSize;
            Object buffer = null;

            for(int i = 0; i < blocksNum; ++i) {
                if(i == blocksNum - 1) {
                    calcSize = data.length - i * blockSize;
                }

                byte[] var22 = cp.doFinal(data, i * blockSize, calcSize);

                try {
                    outputStream.write(var22);
                } catch (IOException var19) {
                    throw new GeneralSecurityException("RSA加/解密时出现异常", var19);
                }
            }

            var21 = outputStream.toByteArray();
        } finally {
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException var18) {
                }
            }

        }

        return var21;
    }
    public static X509Certificate base64StrToCert(String base64Cert) throws GeneralSecurityException {
        try {
            ByteArrayInputStream ex = new ByteArrayInputStream((new BASE64Decoder()).decodeBuffer(base64Cert));
            X509Certificate cert = (X509Certificate)factory.generateCertificate(ex);
            if(cert == null) {
                throw new GeneralSecurityException("将cer从base64转换为对象失败");
            } else {
                return cert;
            }
        } catch (IOException var3) {
            throw new GeneralSecurityException("将cer从base64转换为对象失败", var3);
        }
    }


    public static String signWithSHA2(InputStream fisP12, String pwd, String plainText)
    {
        String result ="";
        KeyStore inputKeyStore = null;
        try {
            if (fisP12 == null ||(pwd == null) || (plainText == null) || (pwd.equals("")) || (plainText.equals("")) ) {
                return  result;
            }
            inputKeyStore = KeyStore.getInstance("PKCS12");
            char[] inPassword = (pwd == null) ? null : pwd.toCharArray();
            inputKeyStore.load(fisP12, inPassword);
          //  fisP12.close();
            PrivateKey privateKey = (PrivateKey)inputKeyStore.getKey("conname", inPassword);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(plainText.getBytes("utf-8"));
            byte[] digest = md.digest();
           // String digestText = new String(Base64.encode(digest));

            byte[] signData = RsaCipher.enDecryptByRsa(digest, privateKey, RsaCipher.EncryptMode.ENCRYPT);
            result = new String(Base64.encode(signData));
            return result;
/* 获取证书
   X509Certificate x509Certificate = (X509Certificate)inputKeyStore.getCertificate("conname");
            byte[] encoded = x509Certificate.getEncoded();
            String base64Cert = new String(Base64.encode(encoded));*/

        } catch (Exception e) {
            return "";
        }
    }
    public static  String getCert(InputStream fisP12, String pwd)
    {

        char[] inPassword = (pwd == null) ? null : pwd.toCharArray();
        try {
            KeyStore inputKeyStore = KeyStore.getInstance("PKCS12");
            inputKeyStore.load(fisP12, inPassword);
         //   fisP12.close();
            X509Certificate x509Certificate = (X509Certificate)inputKeyStore.getCertificate("conname");
            byte[] encoded = x509Certificate.getEncoded();
            return  new String(Base64.encode(encoded));

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
    ///alias = conname
    public static KeyCertInfo fileStreamToKeyCertInfo(InputStream fisP12, String pwd, String keyStoreType, String alias){

        KeyCertInfo result = null;
        try {
            KeyStore inputKeyStore = KeyStore.getInstance(keyStoreType);
            char[] inPassword = (pwd == null) ? null : pwd.toCharArray();
            inputKeyStore.load(fisP12, inPassword);
            PrivateKey privateKey = (PrivateKey)inputKeyStore.getKey(alias, inPassword);
            X509Certificate x509Certificate = (X509Certificate)inputKeyStore.getCertificate(alias);
            byte[] encoded = x509Certificate.getEncoded();
            String base64Cert = new String(Base64.encode(encoded));
            result = new KeyCertInfo();
            result.setBase64Cert(base64Cert);
            result.setPrivateKey(privateKey);
            return result;
        } catch (Exception e) {
            return result;
        }
    }
    public static String signWithSHA2(String plainText,Key privateKey)
    {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(plainText.getBytes("utf-8"));
            byte[] digest = md.digest();
            byte[] signData = RsaCipher.enDecryptByRsa(digest, privateKey, RsaCipher.EncryptMode.ENCRYPT);
            return new String(Base64.encode(signData));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static byte[] getSHA2Digest(String plainText)
    {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(plainText.getBytes("utf-8"));
            byte[] digest = md.digest();
            return digest;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String signWithBase(byte[] digest,Key privateKey)
    {
        MessageDigest md = null;
        try {
            byte[] signData = RsaCipher.enDecryptByRsa(digest, privateKey, RsaCipher.EncryptMode.ENCRYPT);
            return new String(Base64.encode(signData));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static PrivateKey byte2PrivateKey(byte[] src,String algorithm)
    {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);//"RSA"
            PKCS8EncodedKeySpec pKCS8EncodedKeySpec =new PKCS8EncodedKeySpec(src);
            PrivateKey privateKey= keyFactory.generatePrivate(pKCS8EncodedKeySpec);
            return privateKey;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static PublicKey byte2PublicKey(byte[] src,String algorithm)
    {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);//"RSA"
            X509EncodedKeySpec x509KeySpec2 = new X509EncodedKeySpec(src);
            PublicKey privateKey= keyFactory.generatePublic(x509KeySpec2);
            return privateKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }
}
