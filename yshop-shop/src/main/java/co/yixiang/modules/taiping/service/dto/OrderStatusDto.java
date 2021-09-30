package co.yixiang.modules.taiping.service.dto;

import lombok.Data;

@Data
public class OrderStatusDto {
    private String orderNumber;
    private String statusTime;
    private String productName;
    private Integer statusCode;
}
