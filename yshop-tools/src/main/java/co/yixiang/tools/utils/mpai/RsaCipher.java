package co.yixiang.tools.utils.mpai;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.interfaces.RSAPublicKey;

public abstract class RsaCipher
{
    public static final Object LOCK = new Object();
    private static BouncyCastleProvider bouncyCastleProvider;

    public static byte[] enDecryptByRsa(byte[] data, Key key, int mode)
            throws GeneralSecurityException
    {
        byte[] var21;
        BouncyCastleProvider provider = getInstanceProvider();
        ByteArrayOutputStream outputStream = null;
        try
        {
            outputStream = new ByteArrayOutputStream();
            Cipher cp = Cipher.getInstance("RSA/ECB/PKCS1Padding", provider);
            cp.init(mode, key);
            int blockSize = cp.getBlockSize();
            int blocksNum = (int)Math.ceil((double) data.length / (double)blockSize);
            int calcSize = blockSize;
            Object buffer = null;

            for (int i = 0; i < blocksNum; ++i) {
                if (i == blocksNum - 1) {
                    calcSize = data.length - i * blockSize;
                }

                byte[] var22 = cp.doFinal(data, i * blockSize, calcSize);
                try
                {
                    outputStream.write(var22);
                } catch (IOException var19) {
                    throw new GeneralSecurityException("RSA加/解密时出现异常", var19);
                }
            }

            var21 = outputStream.toByteArray();
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                }
                catch (IOException var18)
                {
                }

        }

        return var21;
    }

    public static RSAPublicKey restoreKeyByModules(String modulesHex) throws GeneralSecurityException {
        BigInteger modules = new BigInteger(modulesHex, 16);
        BigInteger publicExponent = new BigInteger("65537");
        RSAPublicKeyImpl rsaPublicKey = new RSAPublicKeyImpl(modules, publicExponent);
        return rsaPublicKey;
    }

    public static byte[] enDecryptByRsa(byte[] data, Key key, EncryptMode mode) throws GeneralSecurityException {
        return enDecryptByRsa(data, key, 1 + mode.ordinal());
    }

    private static BouncyCastleProvider getInstanceProvider() {
        if (bouncyCastleProvider == null) {
            Object var0 = LOCK;
            synchronized (LOCK) {
                if (bouncyCastleProvider == null)
                    bouncyCastleProvider = new BouncyCastleProvider();
            }

        }

        return bouncyCastleProvider; }

    public static enum EncryptMode {
        ENCRYPT, DECRYPT;
    }
}