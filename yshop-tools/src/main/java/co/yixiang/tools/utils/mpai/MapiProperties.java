package co.yixiang.tools.utils.mpai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapiProperties {

    public static String  userCertificateName;

    public static String  serviceCertificateName;

    public static String  passwd;

    public static String  alias;

    public static String merchantNo;

    public static String notifyUrl;

    public static String  path;

    public static String institutionCode;

    public static String returnNotifyUrl;

    public static String  version;

    @Value("${mapi.userCertificateName}")
    public  void setUserCertificateName(String userCertificateName) {
        MapiProperties.userCertificateName = userCertificateName;
    }

    @Value("${mapi.serviceCertificateName}")
    public  void setServiceCertificateName(String serviceCertificateName) {
        MapiProperties.serviceCertificateName = serviceCertificateName;
    }

    @Value("${mapi.passwd}")
    public  void setPasswd(String passwd) {
        MapiProperties.passwd = passwd;
    }

    @Value("${mapi.alias}")
    public  void setAlias(String alias) {
        MapiProperties.alias = alias;
    }

    @Value("${mapi.version}")
    public  void setVersion(String version) {
        MapiProperties.version = version;
    }

    @Value("${mapi.merchantNo}")
    public void setMerchantNo(String merchantNo) {
        MapiProperties.merchantNo = merchantNo;
    }

    @Value("${mapi.notifyUrl}")
    public  void setNotifyUrl(String notifyUrl) {
        MapiProperties.notifyUrl = notifyUrl;
    }

    @Value("${mapi.path}")
    public  void setPath(String path) {
        MapiProperties.path = path;
    }

    @Value("${mapi.institutionCode}")
    public  void setInstitutionCode(String institutionCode) {
        MapiProperties.institutionCode = institutionCode;
    }

    @Value("${mapi.returnNotifyUrl}")
    public  void setReturnNotifyUrl(String returnNotifyUrl) {
        MapiProperties.returnNotifyUrl = returnNotifyUrl;
    }
}
