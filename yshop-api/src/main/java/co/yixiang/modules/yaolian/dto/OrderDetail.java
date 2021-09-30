package co.yixiang.modules.yaolian.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="药联订单明细对象", description="药联订单明细对象")
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "商品id，和连锁商品保持一致")
    private String drug_id;

    @ApiModelProperty(value = "商品通用名")
    private String common_name;

    @ApiModelProperty(value = "数量")
    private String amount;

    @ApiModelProperty(value = "商品原价")
    private String price;

    @ApiModelProperty(value = "结算扣率")
    private String settle_discount_rate;

    @ApiModelProperty(value = "商品条形码")
    private String code;

    @ApiModelProperty(value = "1:使用优惠价购买0:未使用到优惠价")
    private String activity_type;

}
