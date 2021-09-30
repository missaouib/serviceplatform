package co.yixiang.modules.taibao.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "OrderDetailProjectParam对象", description = "项目下单明细对象")
@Data
public class TbOrderDetailProjectParam implements Serializable {

    @ApiModelProperty(value = "药品id")
    private Integer productId;
    @ApiModelProperty(value = "药品针对药房的唯一码")
    private String productUniqueId;
    @ApiModelProperty(value = "数量")
    private Integer num;

    private String medCommonName;
    private String medManufacturer;
    private String medName;
    private String medSku;
    private String medSpec;
    private String medUnit;
    private String pictureUrl;
    private String storeName;
    private String unitPrice;
}
