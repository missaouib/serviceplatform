package co.yixiang.mp.yiyaobao.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@Slf4j
public class SkuSellerPriceStock {
    private String sku;
    private BigDecimal price;
    // 益药宝的药房id
    private String sellerId;
    private String sellerName;
    private Integer stock;
    // 商城的药房id
    private Integer storeId;
    // 商城的药品id
    private Integer productid;

    private String image;
    /* 1 停用 0 启用*/
    private Integer status;

    private int batchNo;

    private String medPartnerMedicineId;
}
