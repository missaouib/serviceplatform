package co.yixiang.modules.zhongan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value="众安普药订单明细传输对象", description="众安普药订单明细传输对象")
public class OrderGoods {
    @ApiModelProperty(value = "商品SkuCode")
    private String goodsSkuCode;
    @ApiModelProperty(value = "商品数量")
    private Integer goodsCount;
    @ApiModelProperty(value = "商品单价")
    private BigDecimal goodsPrice;
    @ApiModelProperty(value = "商品实际价格")
    private BigDecimal goodsRealPrice;

    @ApiModelProperty(value = "药品商品名称")
    private String goodsName;
    @ApiModelProperty(value = "药品通用名称")
    private String goodsCommonName;
    @ApiModelProperty(value = "药品规格")
    private String goodsSpecification;
    @ApiModelProperty(value = "药品剂型")
    private String goodsDosageForm;
    @ApiModelProperty(value = "国药准字或其它国家编码")
    private String goodsApprovalNumber;


}
