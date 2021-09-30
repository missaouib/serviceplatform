package co.yixiang.modules.taiping.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TaipingOrder {
    private BigDecimal totalPrice;
    private List<TaipingOrderDetail> details;
}
