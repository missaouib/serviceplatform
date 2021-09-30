package co.yixiang.mp.service.dto;

import lombok.Data;

@Data
public class OrderTemplateMessage {
    private String orderId;
    private String orderStatus;
    private String orderDate;
    private String remark;
}
