package co.yixiang.modules.taiping.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TaipingOrder {
    private BigDecimal totalPrice;
    private List<TaipingOrderDetail> details;
}
