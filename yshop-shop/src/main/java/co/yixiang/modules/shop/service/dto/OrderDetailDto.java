package co.yixiang.modules.shop.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "订单明细对象", description = "订单明细对象")
public class OrderDetailDto {
    @ApiModelProperty(value = "药品ID")
    private Integer drug_id;

    private String unique;

    @ApiModelProperty(value = "药品数量")
    private Integer quantity;

    /** 商品名称 */
    private String storeName;

    @ApiModelProperty(value = "通用名")
    private String commonName;

    @ApiModelProperty(value = "规格(如500ml)")
    private String spec;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @ApiModelProperty(value = "单位(如：盒)")
    private String unit;

    private Double price;
}
