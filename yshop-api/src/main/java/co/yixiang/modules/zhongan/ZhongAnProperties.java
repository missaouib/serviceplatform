package co.yixiang.modules.zhongan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZhongAnProperties {

    public static String appKey;

    public static String url;

    public static String version;

    public static String privateKey;

    @Value("${zhonganpuyao.appKey}")
    public static void setAppKey(String appKey) {
        ZhongAnProperties.appKey = appKey;
    }

    @Value("${zhonganpuyao.url}")
    public static void setUrl(String url) {
        ZhongAnProperties.url = url;
    }

    @Value("${zhonganpuyao.version}")
    public static void setVersion(String version) {
        ZhongAnProperties.version = version;
    }

    @Value("${zhonganpuyao.privateKey}")
    public static void setPrivateKey(String privateKey) {
        ZhongAnProperties.privateKey = privateKey;
    }
}
