package co.yixiang.modules.api.dto;

/**
 * Created by kevinChen on 2017/12/29.
 * 北京CA签名结果通知 head
 */
public class NotifyReceiptHead {
    String clientId;
    String serviceId;
    String templateId;
    String sign;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


    @Override
    public String toString() {
        return "NotifyReceiptHead{" +
                "clientId='" + clientId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", templateId='" + templateId + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
