package co.yixiang.modules.taiping.entity;

import lombok.Data;

@Data
public class OrderStatusDto {
    private String orderNumber;
    private String statusTime;
    private String productName;
    private Integer statusCode;
}
