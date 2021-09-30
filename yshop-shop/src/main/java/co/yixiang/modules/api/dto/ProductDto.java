package co.yixiang.modules.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class ProductDto {

    @ApiModelProperty(value = "药品id")
    private Integer id;

    /** 商品名称 */
    @ApiModelProperty(value = "商品名称")
    private String storeName;

    private BigDecimal price;

    /** 零售单价 */
    @ApiModelProperty(value = "建议零售单价")
    private BigDecimal unitPrice;

    /** 最低价 */
    @ApiModelProperty(value = "最低价")
    private BigDecimal minPrice;

    /** 最高价 */
    @ApiModelProperty(value = "最高价")
    private BigDecimal maxPrice;

    @ApiModelProperty(value = "批准文号")
    private String licenseNumber;

    @ApiModelProperty(value = "通用名")
    private String commonName;

    @ApiModelProperty(value = "规格(如500ml)")
    private String spec;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @ApiModelProperty(value = "单位(如：盒)")
    private String unit;

    @ApiModelProperty(value = "适应症")
    private String indication;

    @ApiModelProperty(value = "用法用量")
    private String directions;

    @ApiModelProperty(value = "图片")
    private String sliderImage;

    @ApiModelProperty(value = "病种id，多个时用英文逗号分隔")
    private String diseaseName;

    @ApiModelProperty(value = "药品分类，1/OTC，2/RX处方药，3/RX特药")
    private String typeName;
}
