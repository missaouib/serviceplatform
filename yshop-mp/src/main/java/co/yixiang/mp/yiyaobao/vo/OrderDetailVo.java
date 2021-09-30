package co.yixiang.mp.yiyaobao.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailVo {
    @ApiModelProperty(value = "商品名称")
    private String productName;
    @ApiModelProperty(value = "商品规格")
    private String spec;
    @ApiModelProperty(value = "商品数量")
    private Integer qty;
    @ApiModelProperty(value = "商品单价")
    private BigDecimal unitPrice;
    @ApiModelProperty(value = "商品总金额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "折扣率")
    private BigDecimal discountRate;
    @ApiModelProperty(value = "商品折后单价")
    private BigDecimal discountPrice;
    @ApiModelProperty(value = "商品折后总金额")
    private BigDecimal discountTotalAmount;

    private String id;

    private String sku;
}
