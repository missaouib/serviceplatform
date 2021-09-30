package co.yixiang.tools.utils.mpai;

import java.security.Key;

/**
 * Created by jianqun on 2017/8/19.
 */
public class KeyCertInfo {
    public Key getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(Key privateKey) {
        this.privateKey = privateKey;
    }

    public String getBase64Cert() {
        return base64Cert;
    }

    public void setBase64Cert(String base64Cert) {
        this.base64Cert = base64Cert;
    }

    private Key privateKey;
    private String base64Cert;

}
