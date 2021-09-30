package co.yixiang.modules.taiping.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaipingOrderDetail {
    private String drugCode;
    private String tradeName;
    private BigDecimal unitPrice;
    private Integer count;
    private BigDecimal totalPrice;
}
