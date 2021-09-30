package co.yixiang.modules.api.dto;

/**
 * Created by kevinChen on 2017/12/29.
 * 北京CA签名结果通知
 */
public class NotifyReceipt {
    NotifyReceiptHead head;
    NotifyReceiptBody body;

    public NotifyReceiptHead getHead() {
        return head;
    }

    public void setHead(NotifyReceiptHead head) {
        this.head = head;
    }

    public NotifyReceiptBody getBody() {
        return body;
    }

    public void setBody(NotifyReceiptBody body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NotifyReceipt{" +
                "head=" + head +
                ", body=" + body +
                '}';
    }
}
