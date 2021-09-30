package co.yixiang.modules.api.dto;

/**
 * Created by kevinChen on 2017/12/29.
 * 北京CA签名结果通知 body
 */
public class NotifyReceiptBody {
    String timeStampSign;
    String pdfSign;
    String uniqueId;
    String requestId;
    String signFlowId;
    String certEndDate;

    public String getCertEndDate() {
        return certEndDate;
    }

    public void setCertEndDate(String certEndDate) {
        this.certEndDate = certEndDate;
    }

    public String getSignFlowId() {
        return signFlowId;
    }

    public void setSignFlowId(String signFlowId) {
        this.signFlowId = signFlowId;
    }

    public String getTimeStampSign() {
        return timeStampSign;
    }

    public void setTimeStampSign(String timeStampSign) {
        this.timeStampSign = timeStampSign;
    }

    public String getPdfSign() {
        return pdfSign;
    }

    public void setPdfSign(String pdfSign) {
        this.pdfSign = pdfSign;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    @Override
    public String toString() {
        return "NotifyReceiptBody{" +
                "timeStampSign='" + timeStampSign + '\'' +

                ", uniqueId='" + uniqueId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", signFlowId='" + signFlowId + '\'' +
                ", certEndDate='" + certEndDate + '\'' +
                '}';
    }
}
