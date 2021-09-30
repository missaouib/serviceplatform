package co.yixiang.modules.shop.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "Product4ProjectDTO对象", description = "特定项目中的药品对象")
public class Product4ProjectDTO implements Serializable {
    @ApiModelProperty(value = "药品名")
    private String medName;
    @ApiModelProperty(value = "通用名")
    private String commonName;
    @ApiModelProperty(value = "规格(如500ml)")
    private String spec;
    @ApiModelProperty(value = "单位(如：盒)")
    private String unit;
    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;
    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;
    @ApiModelProperty(value = "药品图片")
    String imagePath;
    @ApiModelProperty(value = "药品id")
    Integer productId;
    @ApiModelProperty(value = "药品针对药房的唯一码")
    String productUniqueId;
    @ApiModelProperty(value = "药品特殊备注信息")
    String remarks;
    /** 是否固定药品数量 0/否 1/是 0/否 1/是 */
    private Integer isFixNum;

    private Integer num;
}
