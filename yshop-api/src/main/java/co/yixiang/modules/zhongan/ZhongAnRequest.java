package co.yixiang.modules.zhongan;

import lombok.Data;

@Data
public class ZhongAnRequest {
    private String appKey;
    private String sign;
    private String signType;
    private String timestamp;
    private String serviceName;
    private String format;
    private String charset;
    private String version;
    private String bizContent;

}
