package co.yixiang.modules.yaoshitong.service.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
public class MedSales4DrugstoreDto {

    private String userId;
    private String sellerId;
    private Timestamp lastOrderDate;
    private Timestamp firstOrderDate;
    private Integer ttlQty;
    private Integer purchaseTimes;
    private BigDecimal unitPrice;

}
